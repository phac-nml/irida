package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.galaxy.AnalysisExecutionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.GalaxyPreparedWorkflowPhylogenomicsPipeline;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipeline
	implements AnalysisExecutionServiceGalaxy<AnalysisPhylogenomicsPipeline, AnalysisSubmissionGalaxyPhylogenomicsPipeline> {
	
	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowService galaxyWorkflowService;
	private GalaxyWorkflowPreparationServicePhylogenomicsPipeline preparationService;
	
	public AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService,
			GalaxyWorkflowPreparationServicePhylogenomicsPipeline preparationService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.preparationService = preparationService;
	}
	
	private void validateWorkflow(RemoteWorkflowGalaxy remoteWorkflow) throws WorkflowException {
		checkNotNull(remoteWorkflow, "remoteWorkflow is null");
		checkArgument(galaxyWorkflowService.validateWorkflowByChecksum(
				remoteWorkflow.getWorkflowChecksum(), remoteWorkflow.getWorkflowId()),
				"workflow checksums do not match");
	}
	
	@Override
	public AnalysisSubmissionGalaxyPhylogenomicsPipeline executeAnalysis(
			AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission)
					throws ExecutionManagerException {
		
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		validateWorkflow(analysisSubmission.getRemoteWorkflow());
		
		GalaxyPreparedWorkflowPhylogenomicsPipeline preparedWorkflow = preparationService.prepareWorkflowFiles(analysisSubmission);

		WorkflowInputs input = preparationService.prepareWorkflowInput(analysisSubmission, preparedWorkflow);
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(input);
		analysisSubmission.setRemoteAnalysisId(new GalaxyAnalysisId(preparedWorkflow.getWorkflowHistory().getId()));
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
