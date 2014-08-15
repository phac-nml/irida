package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowInvalidException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.preparation.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.analysis.prepration.galaxy.AnalysisPreparationServiceGalaxy;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <A> The type of Analysis expected to be performed.
 * @param <S> The type of AnalysisSubmissionGalaxy to perform.
 */
public abstract class AnalysisExecutionServiceGalaxy
	<A extends Analysis, P extends AnalysisPreparationServiceGalaxy<T>, T extends AnalysisSubmissionGalaxy>
	implements AnalysisExecutionService<A,T> {
	
	private P preparationService;
	
	protected GalaxyHistoriesService galaxyHistoriesService;
	protected GalaxyWorkflowService galaxyWorkflowService;
	
	public AnalysisExecutionServiceGalaxy(GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService, P preparationService) {
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.preparationService = preparationService;
	}
	
	@Override
	public T executeAnalysis(T analysisSubmission)
					throws ExecutionManagerException {
		
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
		validateWorkflow(analysisSubmission.getRemoteWorkflow());
		
		PreparedWorkflowGalaxy preparedWorkflow = preparationService.prepareAnalysisWorkspace(analysisSubmission);
		WorkflowInputs input = preparedWorkflow.getWorkflowInputs();
		
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(input);
		analysisSubmission.setRemoteAnalysisId(preparedWorkflow.getRemoteAnalysisId());
		analysisSubmission.setOutputs(output);
		
		return analysisSubmission;
	}
	

	@Override
	public A getAnalysisResults(T submittedAnalysis)
			throws ExecutionManagerException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(T submittedAnalysis)
			throws ExecutionManagerException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		
		String analysisId = submittedAnalysis.getRemoteAnalysisId().getValue();		
		return galaxyHistoriesService.getStatusForHistory(analysisId);
	}
	
	/**
	 * Validates the given workflow.
	 * @param remoteWorkflow  The Galaxy workflow to validate.
	 * @throws WorkflowException  If there was 
	 */
	public void validateWorkflow(RemoteWorkflowGalaxy remoteWorkflow) throws WorkflowException {
		checkNotNull(remoteWorkflow, "remoteWorkflow is null");
		
		if (!galaxyWorkflowService.validateWorkflowByChecksum(
				remoteWorkflow.getWorkflowChecksum(), remoteWorkflow.getWorkflowId())) {
			throw new WorkflowInvalidException("passed workflow with id=" +
					remoteWorkflow.getWorkflowId() + " does not have correct checksum " + 
					remoteWorkflow.getWorkflowChecksum());
		}
	}
}
