package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.PreparedWorkflow;

/**
 * A Galaxy workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class PreparedWorkflowGalaxy implements PreparedWorkflow<GalaxyAnalysisId, WorkflowInputsGalaxy> {
	
	private WorkflowInputsGalaxy workflowInputs;
	private GalaxyAnalysisId galaxyAnalysisId;
	
	/**
	 * Builds a new PreparedWorkflowGalaxy with the given parameters.
	 * @param galaxyAnalysisId  The analysisId for the workflow history.
	 * @param workflowInputs  The inputs to this workflow.
	 */
	public PreparedWorkflowGalaxy(GalaxyAnalysisId galaxyAnalysisId, WorkflowInputsGalaxy workflowInputs) {
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
	public GalaxyAnalysisId getRemoteAnalysisId() {
		return galaxyAnalysisId;
	}
}
