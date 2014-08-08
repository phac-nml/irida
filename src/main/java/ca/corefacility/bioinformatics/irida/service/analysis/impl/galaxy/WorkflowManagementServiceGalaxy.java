package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.Workflow;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.ExecutionManagerGalaxy;

/**
 * Implements workflow management for a Galaxy-based workflow execution system.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowManagementServiceGalaxy implements
	WorkflowManagementService<GalaxyAnalysisId, ExecutionManagerGalaxy> {
	
//	private static final Logger logger = LoggerFactory.getLogger(WorkflowManagementServiceGalaxy.class);
//	
//	private WorkflowsClient workflowsClient;
//	private GalaxyWorkflowService galaxyWorkflowService;
//	private GalaxyHistoriesService galaxyHistory;
	
	/**
	 * Given a Workflow, connects to Galaxy and validates the structure of this workflow.
	 * @param workflow  A Workflow to validate.
	 * @return  True if this workflow is valid, false otherwise.
	 */
	private boolean validateWorkflow(Workflow workflow) {
		return false;
	}
	
	private void prepareWorkflow() {
		
	}

	@Override
	public GalaxyAnalysisId executeAnalysis(
			AnalysisSubmission<ExecutionManagerGalaxy> analysisSubmission) throws WorkflowException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkArgument(validateWorkflow(analysisSubmission.getWorkflow()), "workflow is invalid");
		
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
