package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

public class ExecutableWorkflowGalaxy
{
	private String jsonString = null;
	
	public ExecutableWorkflowGalaxy(String workflowString)
	{
		this.jsonString = workflowString;
	}

	public String getJson()
	{
		return jsonString;
	}
}
