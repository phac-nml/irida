package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.execution.PreparedWorkflow;

/**
 * A Galaxy workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class PreparedWorkflowGalaxy implements PreparedWorkflow<WorkflowInputsGalaxy> {
	
	private WorkflowInputsGalaxy workflowInputs;
	private String galaxyAnalysisId;
	private String galaxyLibraryId;
	
	/**
	 * Builds a new PreparedWorkflowGalaxy with the given parameters.
	 * @param galaxyAnalysisId  The analysisId for the workflow history.
	 * @param galaxyLibraryId  The libraryId used to store data in Galaxy.
	 * @param workflowInputs  The inputs to this workflow.
	 */
	public PreparedWorkflowGalaxy(String galaxyAnalysisId, String galaxyLibraryId, WorkflowInputsGalaxy workflowInputs) {
		this.galaxyAnalysisId = galaxyAnalysisId;
		this.galaxyLibraryId = galaxyLibraryId;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRemoteDataId() {
		return galaxyLibraryId;
	}
}
