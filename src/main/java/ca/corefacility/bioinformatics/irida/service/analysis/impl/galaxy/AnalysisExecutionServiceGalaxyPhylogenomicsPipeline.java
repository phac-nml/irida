package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.GalaxyPreparedWorkflowPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.galaxy.AnalysisExecutionServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * An execution service for performing a Phylogenomics Pipeline analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipeline
	extends AnalysisExecutionServiceGalaxy<AnalysisPhylogenomicsPipeline, AnalysisSubmissionGalaxyPhylogenomicsPipeline> {
	
	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowPreparationServicePhylogenomicsPipeline preparationService;
	
	/**
	 * Builds a new Phylogenomis Pipeline analysis with the given service classes.
	 * @param galaxyHistoriesService  A GalaxyHistoriesService for interacting with Galaxy Histories.
	 * @param galaxyWorkflowService  A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param preparationService  A PreparationService for preparing workflows.
	 */
	public AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService,
			GalaxyWorkflowPreparationServicePhylogenomicsPipeline preparationService) {
		super(galaxyWorkflowService);
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.preparationService = preparationService;
	}
	
	@Override
	public AnalysisSubmissionGalaxyPhylogenomicsPipeline executeAnalysis(
			AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission)
					throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
		validateWorkflow(analysisSubmission.getRemoteWorkflow());
		
		GalaxyPreparedWorkflowPhylogenomicsPipeline preparedWorkflow = preparationService.prepareWorkflowFiles(analysisSubmission);
		WorkflowInputs input = preparedWorkflow.getWorkflowInputs();
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(input);
		analysisSubmission.setRemoteAnalysisId(preparedWorkflow.getRemoteAnalysisId());
		analysisSubmission.setOutputs(output);
		
		return analysisSubmission;
	}

	@Override
	public AnalysisPhylogenomicsPipeline getAnalysisResults(AnalysisSubmissionGalaxyPhylogenomicsPipeline submittedAnalysis)
			throws ExecutionManagerException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(AnalysisSubmissionGalaxyPhylogenomicsPipeline submittedAnalysis)
			throws ExecutionManagerException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		
		String analysisId = submittedAnalysis.getRemoteAnalysisId().getValue();		
		return galaxyHistoriesService.getStatusForHistory(analysisId);
	}
}
