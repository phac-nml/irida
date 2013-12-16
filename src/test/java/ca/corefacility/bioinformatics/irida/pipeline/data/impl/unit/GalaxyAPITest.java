package ca.corefacility.bioinformatics.irida.pipeline.data.impl.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.data.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxyLibrary;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxySearch;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxyAPI;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientResponse;

public class GalaxyAPITest
{
	@Mock private LibraryContent libraryContent;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private LibrariesClient librariesClient;
	@Mock private ClientResponse okayResponse;
	@Mock private ClientResponse invalidResponse;
	
	@Mock private GalaxySearch galaxySearch;
	@Mock private GalaxyLibrary galaxyLibrary;
	
	final private String realAdminEmail = "admin@localhost";
	final private String libraryId = "1";
	final private String invalidLibraryId = "2";
	final private String rootFolderId = "2";
	final private String libraryName = "TestLibrary";
	final private String realAdminAPIKey = "0";
	final private String invalidAdminEmail = "admin_invalid@localhost";
	final private String realUserEmail = "test@localhost";
	final private String fakeUserEmail = "fake@localhost";
	final private String realRoleId = "1";
	final private String adminRoleId = "0";
		
	private GalaxyAPI workflowRESTAPI;
	private File dataFile1;
	private File dataFile2;
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException, CreateLibraryException
	{		
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		
		when(galaxyInstance.getApiKey()).thenReturn(realAdminAPIKey);
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		
		when(galaxySearch.checkValidAdminEmailAPIKey(realAdminEmail, realAdminAPIKey)).
			thenReturn(true);
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, realAdminEmail, galaxySearch, galaxyLibrary);
		
		// setup files
		dataFile1 = new File(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = new File(this.getClass().getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
	}
	
	private void setupBuildLibrary() throws CreateLibraryException
	{
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(libraryId);
		
		User realUser = new User();
		realUser.setEmail(realUserEmail);
		
		Role realUserRole = new Role();
		realUserRole.setName(realUserEmail);
		realUserRole.setId(realRoleId);
		
		Role realAdminRole = new Role();
		realAdminRole.setName(realAdminEmail);
		realAdminRole.setId(adminRoleId);
		
		when(galaxySearch.findUserWithEmail(realUserEmail)).thenReturn(realUser);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(realUserRole);
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(returnedLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(realAdminRole);
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(returnedLibrary);
		when(galaxyLibrary.changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail)))
			.thenReturn(returnedLibrary);
	}
	
	private void setupUploadSampleToLibrary(List<GalaxySample> samples, List<LibraryFolder> folders) throws CreateLibraryException
	{
		setupBuildLibrary();
		
		when(librariesClient.getRootFolder(libraryId)).thenReturn(libraryContent);
		when(libraryContent.getId()).thenReturn(rootFolderId);
		when(librariesClient.uploadFile(eq(libraryId), any(FileLibraryUpload.class))).thenReturn(okayResponse);
		
		for (int i = 0; i < samples.size(); i++)
		{
			GalaxySample sample = samples.get(i);
			LibraryFolder folder = folders.get(i);
			
			String sampleName = sample.getSampleName();
			
			when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(sampleName))).thenReturn(folder);
		}
	}
	
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException, CreateLibraryException
	{	
		setupBuildLibrary();
		
		assertEquals(libraryId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail));
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryFail() throws URISyntaxException, CreateLibraryException
	{	
		setupBuildLibrary();
		
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(null);
		
		assertNull(workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoUser() throws URISyntaxException, CreateLibraryException
	{	
		setupBuildLibrary();
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,fakeUserEmail);
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetupInvalidAdminEmail() throws URISyntaxException, CreateLibraryException
	{
		setupBuildLibrary();
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, invalidAdminEmail);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoUserRole() throws URISyntaxException, CreateLibraryException
	{				
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoSetPermissions() throws URISyntaxException, CreateLibraryException
	{
		setupBuildLibrary();
		
		when(galaxyLibrary.changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail)))
			.thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test
	public void testUploadSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData"));
		verify(librariesClient).uploadFile(eq(libraryId), any(FileLibraryUpload.class));
	}
		
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		when(librariesClient.uploadFile(eq(libraryId), any(FileLibraryUpload.class))).thenReturn(invalidResponse);
		
		assertFalse(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData"));
		verify(librariesClient).uploadFile(eq(libraryId), any(FileLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId1 = "3";
		String sampleFolderId2 = "4";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);		
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder1 = new LibraryFolder();
		sampleFolder1.setName(galaxySample1.getSampleName());
		sampleFolder1.setFolderId(sampleFolderId1);
		LibraryFolder sampleFolder2 = new LibraryFolder();
		sampleFolder2.setName(galaxySample2.getSampleName());
		sampleFolder2.setFolderId(sampleFolderId2);
		folders.add(sampleFolder1);
		folders.add(sampleFolder2);
		
		setupUploadSampleToLibrary(samples, folders);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData1"));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData2"));
		verify(librariesClient, times(2)).uploadFile(eq(libraryId), any(FileLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData"));
		verify(librariesClient, times(2)).uploadFile(eq(libraryId), any(FileLibraryUpload.class));
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
				
		assertTrue(workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail));
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq("testData"));
		verify(librariesClient).uploadFile(eq(libraryId), any(FileLibraryUpload.class));
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoExistingLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, invalidLibraryId);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoRootFolder() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		when(librariesClient.getRootFolder(libraryId)).thenReturn(null);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, libraryId);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoCreateSampleFolder() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), any(String.class))).thenReturn(null);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, libraryId);
	}
	
	@Test
	public void testUploadNoFiles() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		
		setupUploadSampleToLibrary(samples, folders);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
	}
	
}
