package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

public interface PreparedWorkflowGalaxy extends PreparedWorkflow {
	
	public WorkflowInputs getWorkflowInputs();
}
