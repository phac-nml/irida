package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.sun.jersey.api.client.ClientHandlerException;

public class WorkflowRESTAPIGalaxyTest
{
	@Mock private WorkflowsClient workflowsClient;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private com.github.jmchilton.blend4j.galaxy.beans.Workflow blendWorkflow;
	
	private WorkflowRESTAPIGalaxy workflowRESTAPI;
	private String goodWorkflowString;
	private String badWorkflowString;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{
		URL goodWorkflowURL = this.getClass().getResource("GalaxyWorkflowGood.ga");
		URL badWorkflowURL = this.getClass().getResource("GalaxyWorkflowBad.ga");
		Scanner goodWorkflowScanner = new Scanner(new File(goodWorkflowURL.toURI()));
		Scanner badWorkflowScanner = new Scanner(new File(badWorkflowURL.toURI()));
		
		MockitoAnnotations.initMocks(this);
		
		goodWorkflowString = goodWorkflowScanner.useDelimiter("\\Z").next();
		goodWorkflowScanner.close();
		
		badWorkflowString = badWorkflowScanner.useDelimiter("\\Z").next();
		badWorkflowScanner.close();
		
		when(galaxyInstance.getWorkflowsClient()).thenReturn(workflowsClient);
		
		workflowRESTAPI = new WorkflowRESTAPIGalaxy(galaxyInstance);
	}
	
	@Test
	public void testSubmitWorkflowGood() throws WorkflowSubmissionException
	{
		when(workflowsClient.importWorkflow(goodWorkflowString)).thenReturn(blendWorkflow);
		when(blendWorkflow.getId()).thenReturn("id");
		
		assertEquals("id", workflowRESTAPI.importWorkflow(new ExecutableWorkflowGalaxy(goodWorkflowString)));
		verify(workflowsClient).importWorkflow(goodWorkflowString);
	}
	
	@Test(expected=WorkflowSubmissionException.class)
	public void testSubmitWorkflowBad() throws WorkflowSubmissionException
	{	
		when(workflowsClient.importWorkflow(badWorkflowString)).thenThrow(new ClientHandlerException());
		
		workflowRESTAPI.importWorkflow(new ExecutableWorkflowGalaxy(badWorkflowString));
	}
}
