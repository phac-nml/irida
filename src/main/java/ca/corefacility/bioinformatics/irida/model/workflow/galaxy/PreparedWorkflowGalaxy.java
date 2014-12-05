package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.PreparedWorkflow;

/**
 * A Galaxy workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class PreparedWorkflowGalaxy implements PreparedWorkflow<WorkflowInputsGalaxy> {
	
	private WorkflowInputsGalaxy workflowInputs;
	private String galaxyAnalysisId;
	
	/**
	 * Builds a new PreparedWorkflowGalaxy with the given parameters.
	 * @param galaxyAnalysisId  The analysisId for the workflow history.
	 * @param workflowInputs  The inputs to this workflow.
	 */
	public PreparedWorkflowGalaxy(String galaxyAnalysisId, WorkflowInputsGalaxy workflowInputs) {
		this.galaxyAnalysisId = galaxyAnalysisId;
		this.workflowInputs = workflowInputs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowInputsGalaxy getWorkflowInputs() {
		return workflowInputs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRemoteAnalysisId() {
		return galaxyAnalysisId;
	}
}
