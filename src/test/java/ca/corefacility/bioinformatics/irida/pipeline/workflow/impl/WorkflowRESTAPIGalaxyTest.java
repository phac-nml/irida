package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.any;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.Main;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

public class WorkflowRESTAPIGalaxyTest
{
	@Mock private WorkflowsClient workflowsClient;
	@Mock private LibrariesClient librariesClient;
	@Mock private LibraryContent libraryContent;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private ClientResponse clientResponse;
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
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		
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
	
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException
	{
		String expectedId = "1";
		
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(expectedId);
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		
		assertEquals(expectedId, workflowRESTAPI.buildGalaxyLibrary(libraryName));
	}
	
	@Test
	public void testBuildGalaxyLibraryFail() throws URISyntaxException
	{
		String libraryName = "TestLibrary";
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(null);
		
		assertNull(workflowRESTAPI.buildGalaxyLibrary(libraryName));
	}
	
	@Test
	public void testUploadFilesToLibrary() throws URISyntaxException
	{
		File dataFile = new File(this.getClass().getResource("testData.fastq").toURI());
		List<File> dataFiles = new ArrayList<File>();
		dataFiles.add(dataFile);
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(dataFiles, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
	}
	
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException
	{
		File dataFile = new File(this.getClass().getResource("testData.fastq").toURI());
		List<File> dataFiles = new ArrayList<File>();
		dataFiles.add(dataFile);
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);		
		
		assertFalse(workflowRESTAPI.uploadFilesToLibrary(dataFiles, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
	}
}
