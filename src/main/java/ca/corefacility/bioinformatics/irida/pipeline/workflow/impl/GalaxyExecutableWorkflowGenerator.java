package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow;

public class GalaxyExecutableWorkflowGenerator
{
	public ExecutableWorkflowGalaxy generateExecutableWorkflow(Workflow workflow)
	{
		return new ExecutableWorkflowGalaxy(workflow.toString());
	}
}
