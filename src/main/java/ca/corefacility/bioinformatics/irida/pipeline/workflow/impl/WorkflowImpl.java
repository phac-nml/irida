package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow;

public class WorkflowImpl implements Workflow
{
	private String workflowJson;
	
	public WorkflowImpl(String galaxyWorkflowJson)
	{
		this.workflowJson = galaxyWorkflowJson;
	}
	
	@Override
	public String toString()
	{
		return workflowJson;
	}
}
