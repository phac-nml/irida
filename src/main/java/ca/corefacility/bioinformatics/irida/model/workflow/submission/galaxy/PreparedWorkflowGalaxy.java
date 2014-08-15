package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.PreparedWorkflow;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

/**
 * A Galaxy workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class PreparedWorkflowGalaxy implements PreparedWorkflow {
	
	private WorkflowInputs workflowInputs;
	private GalaxyAnalysisId galaxyAnalysisId;
	
	/**
	 * Builds a new PreparedWorkflowGalaxy with the given parameters.
	 * @param galaxyAnalysisId  The analysisId for the workflow history.
	 * @param workflowInputs  The inputs to this workflow.
	 */
	public PreparedWorkflowGalaxy(GalaxyAnalysisId galaxyAnalysisId, WorkflowInputs workflowInputs) {
		this.galaxyAnalysisId = galaxyAnalysisId;
		this.workflowInputs = workflowInputs;
	}
	
	/**
	 * Gets the inputs to this workflow.
	 * @return  The inputs to this workflow.
	 */
	public WorkflowInputs getWorkflowInputs() {
		return workflowInputs;
	}

	/**
	 * Gets the analysis id for this workflow.
	 * @return  The analysis id for this workflow.
	 */
	public GalaxyAnalysisId getRemoteAnalysisId() {
		return galaxyAnalysisId;
	}
}
