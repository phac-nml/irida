package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import static org.mockito.Mockito.*;
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

import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
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
	private File dataFile1;
	private File dataFile2;
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
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
		
		dataFile1 = new File(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = new File(this.getClass().getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
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
	public void testUploadSampleToLibrary() throws URISyntaxException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException
	{
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);		
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		LibraryFolder sampleFolder1 = new LibraryFolderTest();
		sampleFolder1.setName(galaxySample1.getSampleName());
		sampleFolder1.setFolderId(rootFolderID);
		
		LibraryFolder sampleFolder2 = new LibraryFolderTest();
		sampleFolder2.setName(galaxySample2.getSampleName());
		sampleFolder2.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder1)).thenReturn(sampleFolder1);
		when(librariesClient.createFolder(libraryID, sampleFolder2)).thenReturn(sampleFolder2);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient, times(2)).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder1);
		verify(librariesClient).createFolder(libraryID, sampleFolder2);
	}
	
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);

		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertFalse(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	/**
	 * Class used to implement equals() and hashCode() methods to get testing to work.
	 * @author aaron
	 *
	 */
	private class LibraryFolderTest extends LibraryFolder
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (!(o instanceof LibraryFolder))
				return false;
			LibraryFolder l = (LibraryFolder)o;
			
			if (this.getName() != l.getName())
				return false;
			if (this.getName() == null)
				return false;
			if (! this.getName().equals(l.getName()))
				return false;
			
			if (this.getCreateType() != l.getCreateType())
				return false;
			if (this.getCreateType() == null)
				return false;
			if (! this.getCreateType().equals(l.getCreateType()))
				return false;
			
			if (this.getDescription() != l.getDescription())
				return false;
			if (this.getDescription() == null)
				return false;
			if (! this.getDescription().equals(l.getDescription()))
				return false;
			
			if (this.getFolderId() != l.getFolderId())
				return false;
			if (this.getFolderId() == null)
				return false;
			if (! this.getFolderId().equals(l.getFolderId()))
				return false;
			
			return true;
		}
	}
}
