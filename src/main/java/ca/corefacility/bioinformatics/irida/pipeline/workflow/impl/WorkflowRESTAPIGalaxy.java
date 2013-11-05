package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.sun.jersey.api.client.ClientHandlerException;

public class WorkflowRESTAPIGalaxy
{
	private GalaxyInstance galaxyInstance;
	
	public WorkflowRESTAPIGalaxy(String galaxyURL, String apiKey)
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
		
		if (galaxyInstance == null)
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
						galaxyURL + ", apiKey=" + apiKey);
		}
	}
	
	public WorkflowRESTAPIGalaxy(GalaxyInstance galaxyInstance)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		this.galaxyInstance = galaxyInstance;
	}
	
	/**
	 * Imports the passed workflow into Galaxy.
	 * @param workflowGalaxy  The Galaxy Workflow to import.
	 * @return  The ID of the Galaxy Workflow, or null if not successfully imported.
	 * @throws WorkflowSubmissionException  If an error occurred while importing the workflow.
	 */
	public String importWorkflow(ExecutableWorkflowGalaxy workflowGalaxy) throws WorkflowSubmissionException
	{
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		com.github.jmchilton.blend4j.galaxy.beans.Workflow galaxyWorkflow = null;
		
		try
		{
			galaxyWorkflow = workflowsClient.importWorkflow(workflowGalaxy.getJson());
		}
		catch (ClientHandlerException e)
		{
			throw new WorkflowSubmissionException(e);
		}
		
		if (galaxyWorkflow == null)
		{
			throw new WorkflowSubmissionException("uploaded Galaxy workflow is null");
		}
		
		return galaxyWorkflow.getId();
	}
	
	/**
	 * Imports the files used within the given workflow to Galaxy.
	 * @param workflowGalaxy  The workflow containing the files to import.
	 * @return  An ID of the data library containing the files.
	 */
	public String importWorkflowFiles(ExecutableWorkflowGalaxy workflowGalaxy)
		throws WorkflowSubmissionException
	{
		return null;
	}
}
