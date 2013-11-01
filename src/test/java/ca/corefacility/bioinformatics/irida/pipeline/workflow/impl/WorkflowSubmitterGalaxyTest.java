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

import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

public class WorkflowSubmitterGalaxyTest
{
	private WorkflowSubmitterGalaxy workflowSubmitter;
	private WorkflowsClient workflowsClient;
	private String goodWorkflowString;
	private String badWorkflowString;
	private com.github.jmchilton.blend4j.galaxy.beans.Workflow blendWorkflow;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{
		URL goodWorkflowURL = this.getClass().getResource("GalaxyWorkflowGood.ga");
		URL badWorkflowURL = this.getClass().getResource("GalaxyWorkflowBad.ga");
		Scanner goodWorkflowScanner = new Scanner(new File(goodWorkflowURL.toURI()));
		Scanner badWorkflowScanner = new Scanner(new File(badWorkflowURL.toURI()));
		
		goodWorkflowString = goodWorkflowScanner.useDelimiter("\\Z").next();
		goodWorkflowScanner.close();
		
		badWorkflowString = badWorkflowScanner.useDelimiter("\\Z").next();
		badWorkflowScanner.close();
		
		GalaxyInstance galaxyInstance = mock(GalaxyInstance.class);
		workflowsClient = mock(WorkflowsClient.class);
		
		blendWorkflow = mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
		
		when(galaxyInstance.getWorkflowsClient()).thenReturn(workflowsClient);
		
		workflowSubmitter = new WorkflowSubmitterGalaxy(galaxyInstance);
	}
	
	@Test
	public void testSubmitWorkflowGood() throws WorkflowSubmissionException
	{
		when(workflowsClient.importWorkflow(goodWorkflowString)).thenReturn(blendWorkflow);
		
		assertTrue(workflowSubmitter.submitWorkflow(new WorkflowImpl(goodWorkflowString)));
		verify(workflowsClient).importWorkflow(goodWorkflowString);
	}
	
	@Test(expected=WorkflowSubmissionException.class)
	public void testSubmitWorkflowBad() throws WorkflowSubmissionException
	{	
		when(workflowsClient.importWorkflow(badWorkflowString)).thenThrow(new ClientHandlerException());
		
		assertFalse(workflowSubmitter.submitWorkflow(new WorkflowImpl(badWorkflowString)));
		verify(workflowsClient).importWorkflow(badWorkflowString);
	}
}
