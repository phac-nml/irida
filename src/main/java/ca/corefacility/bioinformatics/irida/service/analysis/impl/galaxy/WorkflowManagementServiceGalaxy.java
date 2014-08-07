package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.*;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyWorkflowId;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowManagementServiceGalaxy implements WorkflowManagementService<GalaxyWorkflowId> {
	
	private boolean validateWorkflow() {
		return false;
	}
	
	private void prepareWorkflow() {
		
	}

	@Override
	public GalaxyWorkflowId executeAnalysis(
			AnalysisSubmission analysisSubmission) throws WorkflowException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
		validateWorkflow();
		prepareWorkflow();
		
		throw new UnsupportedOperationException();
	}

	@Override
	public Analysis getAnalysisResults(GalaxyWorkflowId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(GalaxyWorkflowId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelAnalysis(GalaxyWorkflowId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}
}
