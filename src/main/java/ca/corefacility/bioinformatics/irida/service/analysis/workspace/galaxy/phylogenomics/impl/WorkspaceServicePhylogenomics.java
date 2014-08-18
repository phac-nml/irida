package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Tasks for executing a Phylogenomics Pipeline in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkspaceServicePhylogenomics 
	extends AnalysisWorkspaceServiceGalaxy<RemoteWorkflowPhylogenomics,
		AnalysisSubmissionPhylogenomics, AnalysisPhylogenomicsPipeline> {
	
	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowService galaxyWorkflowService;
	
	public WorkspaceServicePhylogenomics(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedWorkflowGalaxy prepareAnalysisWorkspace(AnalysisSubmissionPhylogenomics analysisSubmission)
			throws ExecutionManagerException {
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
				fileToHistory(referenceFile.getFile(), InputFileType.FASTA, workflowHistory);
		
		CollectionResponse collectionResponse = 
				galaxyHistoriesService.constructCollectionList(sequenceDatasets, workflowHistory);
		
		RemoteWorkflowPhylogenomics remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		
		String workflowId = remoteWorkflow.getWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);
		
		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getInputSequenceFilesLabel());
		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getInputReferenceFileLabel());
		
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId,
				new WorkflowInputs.WorkflowInput(collectionResponse.getId(),
				WorkflowInputs.InputSourceType.HDCA));
		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(referenceDataset.getId(),
				WorkflowInputs.InputSourceType.HDA));
		
		GalaxyAnalysisId analysisId = new GalaxyAnalysisId(workflowHistory.getId());
		
		return new PreparedWorkflowGalaxy(analysisId, new WorkflowInputsGalaxy(inputs));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisPhylogenomicsPipeline getAnalysisResults(
			AnalysisSubmissionPhylogenomics analysisSubmission)
			throws ExecutionManagerException {
		throw new UnsupportedOperationException();
	}
}
