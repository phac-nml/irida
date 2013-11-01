package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmitter;

public class WorkflowSubmitterGalaxy implements WorkflowSubmitter
{
	private GalaxyInstance galaxyInstance;
	private GalaxyExecutableWorkflowGenerator workflowGenerator;
	
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
		
		galaxyInstance = GalaxyInstanceFactory.get(galaxyURL, apiKey);
		workflowGenerator = new GalaxyExecutableWorkflowGenerator();
		
		if (galaxyInstance == null)
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
						galaxyURL + ", apiKey=" + apiKey);
		}
	}
	
	public WorkflowSubmitterGalaxy(GalaxyInstance galaxyInstance)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		this.galaxyInstance = galaxyInstance;
		workflowGenerator = new GalaxyExecutableWorkflowGenerator();
	}
	
	@Override
	public boolean submitWorkflow(Workflow workflow)
	{
		if (workflow == null)
		{
			throw new IllegalArgumentException("executableWorkflow is null");
		}
		
		ExecutableWorkflowGalaxy workflowGalaxy = workflowGenerator.generateExecutableWorkflow(workflow);
		
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		ClientResponse response = workflowsClient.importWorkflowResponse(workflowGalaxy.getJson());
		
		return response.getClientResponseStatus() == ClientResponse.Status.OK;
	}	
}
