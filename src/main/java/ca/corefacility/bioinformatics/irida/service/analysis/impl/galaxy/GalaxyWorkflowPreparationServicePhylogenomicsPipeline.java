package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

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
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Prepares a Phylogenomics Pipeline for execution in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowPreparationServicePhylogenomicsPipeline {
	
	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowService galaxyWorkflowService;
	
	public class GalaxyPreparedWorkflowPhylogenomicsPipeline {
		private CollectionResponse sequenceFilesCollection;
		private Dataset referenceDataset;
		private History workflowHistory;
		
		public GalaxyPreparedWorkflowPhylogenomicsPipeline(CollectionResponse sequenceFilesCollection,
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
	
	public GalaxyWorkflowPreparationServicePhylogenomicsPipeline(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
	}
	
	public GalaxyPreparedWorkflowPhylogenomicsPipeline prepareWorkflowFiles(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
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

		return new GalaxyPreparedWorkflowPhylogenomicsPipeline(collectionResponse, referenceDataset, workflowHistory);
	}
	
	public WorkflowInputs prepareWorkflowInput(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission,
			GalaxyPreparedWorkflowPhylogenomicsPipeline preparedWorkflow) throws WorkflowException {
		RemoteWorkflowGalaxy remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		
		String workflowId = remoteWorkflow.getWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);
		
		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				analysisSubmission.getSequenceFileInputLabel());
		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				analysisSubmission.getReferenceFileInputLabel());
		
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(preparedWorkflow.getWorkflowHistory().getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getSequenceFilesCollection().getId(),
				WorkflowInputs.InputSourceType.HDCA));
		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(preparedWorkflow.getReferenceDataset().getId(),
				WorkflowInputs.InputSourceType.HDA));
		
		return inputs;
	}
}
