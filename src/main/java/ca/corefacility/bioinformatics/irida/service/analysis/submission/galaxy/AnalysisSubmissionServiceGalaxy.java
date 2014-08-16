package ca.corefacility.bioinformatics.irida.service.analysis.submission.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowInvalidException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.prepration.galaxy.AnalysisPreparationServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.submission.AnalysisSubmissionService;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <A> The type of Analysis expected to be performed.
 * @param <P> The type of AnalysisPreparationService to use.
 * @param <R> The type of RemoteWorkflow to use.
 * @param <S> The type of AnalysisSubmissionGalaxy to perform.
 */
public abstract class AnalysisSubmissionServiceGalaxy
	<A extends Analysis, P extends AnalysisPreparationServiceGalaxy<R,S>, 
	R extends RemoteWorkflowGalaxy, S extends AnalysisSubmissionGalaxy<R>>
	implements AnalysisSubmissionService<A,S> {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisSubmissionServiceGalaxy.class);
	
	private P preparationService;
	
	protected GalaxyHistoriesService galaxyHistoriesService;
	protected GalaxyWorkflowService galaxyWorkflowService;
	
	public AnalysisSubmissionServiceGalaxy(GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService, P preparationService) {
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.preparationService = preparationService;
	}
	
	@Override
	public S executeAnalysis(S analysisSubmission)
					throws ExecutionManagerException {
		
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		String analysisName = analysisSubmission.getClass().getSimpleName();
		RemoteWorkflowGalaxy remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		logger.debug("Running " + analysisName + ": " + remoteWorkflow);
		
		logger.trace("Validating " + analysisName + ": " + remoteWorkflow);
		validateWorkflow(analysisSubmission.getRemoteWorkflow());
		
		logger.trace("Preparing " + analysisName + ": " + remoteWorkflow);
		PreparedWorkflowGalaxy preparedWorkflow = preparationService.prepareAnalysisWorkspace(analysisSubmission);
		WorkflowInputs input = preparedWorkflow.getWorkflowInputs();
		
		logger.trace("Executing " + analysisName + ": " + remoteWorkflow);
		WorkflowOutputs output = galaxyWorkflowService.runWorkflow(input);
		analysisSubmission.setRemoteAnalysisId(preparedWorkflow.getRemoteAnalysisId());
		analysisSubmission.setOutputs(output);
		
		return analysisSubmission;
	}
	

	@Override
	public A getAnalysisResults(S submittedAnalysis)
			throws ExecutionManagerException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(S submittedAnalysis)
			throws ExecutionManagerException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		
		String analysisId = submittedAnalysis.getRemoteAnalysisId().getValue();		
		return galaxyHistoriesService.getStatusForHistory(analysisId);
	}
	
	/**
	 * Validates the given workflow.
	 * @param remoteWorkflow  The Galaxy workflow to validate.
	 * @throws WorkflowException  If there was an issue validating the workflow.
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
