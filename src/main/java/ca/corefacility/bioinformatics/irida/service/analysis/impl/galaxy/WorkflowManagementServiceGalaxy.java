package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.*;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowManagementServiceGalaxy implements WorkflowManagementService<GalaxyAnalysisId> {
	
	private boolean validateWorkflow() {
		return false;
	}
	
	private void prepareWorkflow() {
		
	}

	@Override
	public GalaxyAnalysisId executeAnalysis(
			AnalysisSubmission analysisSubmission) throws WorkflowException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
		validateWorkflow();
		prepareWorkflow();
		
		throw new UnsupportedOperationException();
	}

	@Override
	public Analysis getAnalysisResults(GalaxyAnalysisId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(GalaxyAnalysisId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelAnalysis(GalaxyAnalysisId workflowId)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}
}
