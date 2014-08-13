package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.AnalysisSubmissionTestImpl;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.RemoteWorkflowGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.Lists;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowManagementServiceGalaxy implements
	WorkflowManagementService<GalaxyAnalysisId, AnalysisSubmissionTestImpl> {
	
	private class PreparedWorkflow {
		private CollectionResponse sequenceFilesCollection;
		@SuppressWarnings("unused")
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

//		public Dataset getReferenceDataset() {
//			return referenceDataset;
//		}

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
	
	private PreparedWorkflow prepareWorkflow(AnalysisSubmissionTestImpl analysisSubmission) throws ExecutionManagerException {
		
		Set<Path> sequenceFiles = analysisSubmission.getSequenceFiles();
		Path referenceFile = analysisSubmission.getReferenceFile();
		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();
		
		List<Dataset> sequenceDatasets = galaxyHistoriesService.
				uploadFilesListToHistory(Lists.newArrayList(sequenceFiles), InputFileType.FASTQ_SANGER, workflowHistory);
		
		Dataset referenceDataset = galaxyHistoriesService.
				fileToHistory(referenceFile, InputFileType.FASTQ_SANGER, workflowHistory);
		
		CollectionResponse collectionResponse = 
				constructCollectionList(sequenceDatasets, workflowHistory);

		return new PreparedWorkflow(collectionResponse, referenceDataset, workflowHistory);
	}

	private CollectionResponse constructCollectionList(List<Dataset> sequenceDatasets,
			History workflowHistory) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * @throws ExecutionManagerException 
	 */
	@Override
	public GalaxyAnalysisId executeAnalysis(
			AnalysisSubmissionTestImpl analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkArgument(validateWorkflow(analysisSubmission.getRemoteWorkflow()), "workflow is invalid");
		
		PreparedWorkflow preparedWorkflow = prepareWorkflow(analysisSubmission);
		RemoteWorkflow<ExecutionManagerGalaxy> remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		
		String workflowId = remoteWorkflow.getWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);
		
		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getSequenceFileInputLabel());
		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getReferenceFileInputLabel());
		
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(preparedWorkflow.getWorkflowHistory().getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getSequenceFilesCollection().getId(),
				WorkflowInputs.InputSourceType.HDCA));
		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getSequenceFilesCollection().getId(),
				WorkflowInputs.InputSourceType.HDCA));
		
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(inputs);
		
		// I need to store the outputs someplace
		
		return new GalaxyAnalysisId(output.getHistoryId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getAnalysisResults(GalaxyAnalysisId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowStatus getWorkflowStatus(GalaxyAnalysisId workflowId)
			throws ExecutionManagerException {
		return galaxyHistoriesService.getStatusForHistory(workflowId.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelAnalysis(GalaxyAnalysisId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}
}
