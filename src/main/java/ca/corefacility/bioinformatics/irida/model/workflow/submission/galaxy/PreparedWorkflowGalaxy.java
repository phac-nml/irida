package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.PreparedWorkflow;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

public class PreparedWorkflowGalaxy implements PreparedWorkflow {
	
	private WorkflowInputs workflowInputs;
	private GalaxyAnalysisId galaxyAnalysisId;
	
	public PreparedWorkflowGalaxy(GalaxyAnalysisId galaxyAnalysisId, WorkflowInputs workflowInputs) {
		this.galaxyAnalysisId = galaxyAnalysisId;
		this.workflowInputs = workflowInputs;
	}
	
	public WorkflowInputs getWorkflowInputs() {
		return workflowInputs;
	}

	public GalaxyAnalysisId getRemoteAnalysisId() {
		return galaxyAnalysisId;
	}
}
