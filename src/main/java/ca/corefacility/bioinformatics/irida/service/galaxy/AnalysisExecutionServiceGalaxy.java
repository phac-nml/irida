package ca.corefacility.bioinformatics.irida.service.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowInvalidException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionService;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <A> The type of Analysis expected to be performed.
 * @param <S> The type of AnalysisSubmissionGalaxy to perform.
 */
public abstract class AnalysisExecutionServiceGalaxy<A extends Analysis, T extends AnalysisSubmissionGalaxy>
	implements AnalysisExecutionService<A,T> {
	
	protected GalaxyWorkflowService galaxyWorkflowService;
	
	public AnalysisExecutionServiceGalaxy(GalaxyWorkflowService galaxyWorkflowService) {
		this.galaxyWorkflowService = galaxyWorkflowService;
	}
	
	/**
	 * Validates the given workflow.
	 * @param remoteWorkflow  The Galaxy workflow to validate.
	 * @throws WorkflowException  If there was 
	 */
	public void validateWorkflow(RemoteWorkflowGalaxy remoteWorkflow) throws WorkflowException {
		checkNotNull(remoteWorkflow, "remoteWorkflow is null");
		
		if (galaxyWorkflowService.validateWorkflowByChecksum(
				remoteWorkflow.getWorkflowChecksum(), remoteWorkflow.getWorkflowId())) {
			throw new WorkflowInvalidException("passed workflow with id=" +
					remoteWorkflow.getWorkflowId() + " does not have correct checksum " + 
					remoteWorkflow.getWorkflowChecksum());
		}
	}
}
