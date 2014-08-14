package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowManagementServiceGalaxy implements
	WorkflowManagementService<AnalysisSubmissionGalaxyPhylogenomicsPipeline> {
	
	private final static String sequenceFileInputLabel = "sequence_reads";
	private final static String refereneFileInputLabel = "reference";
	
	private class PreparedWorkflow {
		private CollectionResponse sequenceFilesCollection;
		private Dataset referenceDataset;
		private History workflowHistory;
		
		public PreparedWorkflow(CollectionResponse sequenceFilesCollection,
				Dataset referenceDataset, History workflowHistory) {
			this.sequenceFilesCollection = sequenceFilesCollection;
			this.referenceDataset = referenceDataset;
			this.workflowHistory = workflowHistory;
		}

		public CollectionResponse getSequenceFilesCollection() {
			return sequenceFilesCollection;
		}

		public Dataset getReferenceDataset() {
			return referenceDataset;
		}

		public History getWorkflowHistory() {
			return workflowHistory;
		}
	}
	
	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowService galaxyWorkflowService;
	
	public WorkflowManagementServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
	}
	
	/**
	 * Given a Workflow, connects to Galaxy and validates the structure of this workflow.
	 * @param workflow  A Workflow to validate.
	 * @return  True if this workflow is valid, false otherwise.
	 * @throws WorkflowException 
	 */
	private boolean validateWorkflow(RemoteWorkflowGalaxy remoteWorkflow) throws WorkflowException {
		return galaxyWorkflowService.validateWorkflowByChecksum(
				remoteWorkflow.getWorkflowChecksum(), remoteWorkflow.getWorkflowId());
	}
	
	private PreparedWorkflow prepareWorkflow(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission) throws ExecutionManagerException {
		
		Set<SequenceFile> sequenceFiles = analysisSubmission.getInputFiles();
		List<Path> sequenceFilePaths = new LinkedList<>();
		for (SequenceFile file : sequenceFiles) {
			sequenceFilePaths.add(file.getFile());
		}
		
		ReferenceFile referenceFile = analysisSubmission.getReferenceFile();
		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();
		
		List<Dataset> sequenceDatasets = galaxyHistoriesService.
				uploadFilesListToHistory(sequenceFilePaths, InputFileType.FASTQ_SANGER, workflowHistory);
		
		Dataset referenceDataset = galaxyHistoriesService.
				fileToHistory(referenceFile.getFile(), InputFileType.FASTQ_SANGER, workflowHistory);
		
		CollectionResponse collectionResponse = 
				galaxyHistoriesService.constructCollectionList(sequenceDatasets, workflowHistory);

		return new PreparedWorkflow(collectionResponse, referenceDataset, workflowHistory);
	}

	/**
	 * {@inheritDoc}
	 * @throws ExecutionManagerException 
	 */
	@Override
	public AnalysisSubmissionGalaxyPhylogenomicsPipeline executeAnalysis(
			AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkArgument(validateWorkflow(analysisSubmission.getRemoteWorkflow()), "workflow is invalid");
		
		PreparedWorkflow preparedWorkflow = prepareWorkflow(analysisSubmission);
		RemoteWorkflowGalaxy remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		
		String workflowId = remoteWorkflow.getWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);
		
		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				sequenceFileInputLabel);
		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				refereneFileInputLabel);
		
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(preparedWorkflow.getWorkflowHistory().getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getSequenceFilesCollection().getId(),
				WorkflowInputs.InputSourceType.HDCA));
		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getReferenceDataset().getId(),
				WorkflowInputs.InputSourceType.HDA));
		
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(inputs);
		
		return analysisSubmission;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getAnalysisResults(AnalysisSubmissionGalaxyPhylogenomicsPipeline submittedAnalysis)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowStatus getWorkflowStatus(AnalysisSubmissionGalaxyPhylogenomicsPipeline submittedAnalysis)
			throws ExecutionManagerException {
		return galaxyHistoriesService.getStatusForHistory(submittedAnalysis.getRemoteAnalysisId().getValue());
	}
}
