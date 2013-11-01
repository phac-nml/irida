package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmitter;

public class WorkflowSubmitterGalaxy implements WorkflowSubmitter
{
	private WorkflowRESTAPIGalaxy workflowAPIGalaxy;
	private GalaxyExecutableWorkflowGenerator workflowGenerator;
	
	public WorkflowSubmitterGalaxy(WorkflowRESTAPIGalaxy workflowAPIGalaxy)
	{
		if (workflowAPIGalaxy == null)
		{
			throw new IllegalArgumentException("galaxyURL is null");
		}
		
		this.workflowAPIGalaxy = workflowAPIGalaxy;
		workflowGenerator = new GalaxyExecutableWorkflowGenerator();
	}
	
	public WorkflowSubmitterGalaxy(String galaxyURL, String apiKey)
	{
		if (galaxyURL == null)
		{
			throw new IllegalArgumentException("galaxyURL is null");
		}
		else if (apiKey == null)
		{
			throw new IllegalArgumentException("apiKey is null");			
		}
		
		workflowAPIGalaxy = new WorkflowRESTAPIGalaxy(galaxyURL, apiKey);
		workflowGenerator = new GalaxyExecutableWorkflowGenerator();
	}
	
	@Override
	public boolean submitWorkflow(ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow workflow)
		throws WorkflowSubmissionException
	{
		if (workflow == null)
		{
			throw new IllegalArgumentException("executableWorkflow is null");
		}
		
		ExecutableWorkflowGalaxy workflowGalaxy = workflowGenerator.generateExecutableWorkflow(workflow);
		
		String workflowId = workflowAPIGalaxy.importWorkflow(workflowGalaxy);
		
		return workflowId != null;
	}
}
