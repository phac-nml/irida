package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyLibrary;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySearch;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.LibraryUploadException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
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
	final private String illuminaFolderName = "illumina_reads";
	final private String referencesFolderName = "references";
	final private String illuminaFolderPath = "/illumina_reads";
	final private String referencesFolderPath = "/references";
		
	private GalaxyAPI workflowRESTAPI;
	private Map<String, LibraryContent> libraryMap;
	private File dataFile1;
	private File dataFile2;
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
	private URL libraryURL;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException, CreateLibraryException
	{		
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		
		when(galaxyInstance.getApiKey()).thenReturn(realAdminAPIKey);
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		when(galaxyInstance.getGalaxyUrl()).thenReturn("http://localhost/");
		
		when(galaxySearch.checkValidAdminEmailAPIKey(realAdminEmail, realAdminAPIKey)).
			thenReturn(true);
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, realAdminEmail, false, galaxySearch, galaxyLibrary);
		
		// setup files
		dataFile1 = new File(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = new File(this.getClass().getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
		
		libraryMap = new HashMap<String, LibraryContent>();
	}
	
	private void setupBuildLibrary() throws CreateLibraryException, MalformedURLException
	{
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(libraryId);
		libraryURL = new URL("http://localhost/api/libraries/" + libraryId);
		returnedLibrary.setUrl("/api/libraries/" + libraryId);
		
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
		when(galaxySearch.libraryContentAsMap(libraryId)).thenReturn(libraryMap);
	}
	
	private void setupExisitingLibrary() throws CreateLibraryException, MalformedURLException
	{
		List<Library> libraries = new LinkedList<Library>();
		Library existingLibrary = new Library(libraryName);
		existingLibrary.setId(libraryId);
		libraries.add(existingLibrary);
		libraryURL = new URL("http://localhost/api/libraries/" + libraryId);
		existingLibrary.setUrl("/api/libraries/" + libraryId);
				
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
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(existingLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(realAdminRole);
		when(galaxySearch.findLibraryWithName(libraryName)).thenReturn(libraries);
		when(galaxySearch.libraryContentAsMap(libraryId)).thenReturn(libraryMap);
	}
	
	private void setupUploadSampleToLibrary(List<GalaxySample> samples, List<LibraryFolder> folders,
			boolean libraryExists) throws CreateLibraryException, MalformedURLException
	{
		if (libraryExists)
		{
			setupExisitingLibrary();
		}
		else
		{
			setupBuildLibrary();
		}
		
		libraryMap.put("/", libraryContent);
		when(librariesClient.getRootFolder(libraryId)).thenReturn(libraryContent);
		when(libraryContent.getId()).thenReturn(rootFolderId);
		when(librariesClient.uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class)))
			.thenReturn(okayResponse);
			
		for (int i = 0; i < samples.size(); i++)
		{
			GalaxySample sample = samples.get(i);
			LibraryFolder folder = folders.get(i);
			
			String sampleName = sample.getSampleName();
			
			when(galaxyLibrary.createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq(sampleName))).thenReturn(folder);
		}
	}
	
	private void setupLibraryFolders()
	{		
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath);
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath);
		illuminaFolder.setFolderId(rootFolderId);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(null);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(null);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	private void setupLibraryFoldersWithIlluminaFolder()
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath);
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath);
		illuminaFolder.setFolderId(rootFolderId);
		
		LibraryContent illuminaContent = new LibraryContent();
		illuminaContent.setName(illuminaFolderPath);
		illuminaContent.setType("folder");
		libraryMap.put(illuminaFolderPath, illuminaContent);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(illuminaContent);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(null);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	private void setupLibraryFoldersWithReferencesFolder()
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath);
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryContent referenceContent = new LibraryContent();
		referenceContent.setName(referencesFolderPath);
		referenceContent.setType("folder");
		libraryMap.put(referencesFolderPath, referenceContent);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath);
		illuminaFolder.setFolderId(rootFolderId);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(null);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(referenceContent);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	private void setupLibraryFoldersWithBothFolders()
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath);
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryContent referenceContent = new LibraryContent();
		referenceContent.setName(referencesFolderPath);
		referenceContent.setType("folder");
		libraryMap.put(referencesFolderPath, referenceContent);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath);
		illuminaFolder.setFolderId(rootFolderId);
		
		LibraryContent illuminaContent = new LibraryContent();
		illuminaContent.setName(illuminaFolderPath);
		illuminaContent.setType("folder");
		libraryMap.put(illuminaFolderPath, illuminaContent);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(illuminaContent);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(referenceContent);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException, CreateLibraryException, MalformedURLException
	{	
		setupBuildLibrary();
		
		assertEquals(libraryId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail).getId());
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryFail() throws URISyntaxException, CreateLibraryException, MalformedURLException
	{	
		setupBuildLibrary();
		
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(null);
		
		assertNull(workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoUser() throws URISyntaxException, CreateLibraryException, MalformedURLException
	{	
		setupBuildLibrary();
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,fakeUserEmail);
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetupInvalidAdminEmail() throws URISyntaxException, CreateLibraryException, MalformedURLException
	{
		setupBuildLibrary();
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, invalidAdminEmail, false);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoUserRole() throws URISyntaxException, CreateLibraryException
	{				
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoSetPermissions() throws URISyntaxException, CreateLibraryException, MalformedURLException
	{
		setupBuildLibrary();
		
		when(galaxyLibrary.changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail)))
			.thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test
	public void testUploadSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadExistingSampleFolderToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		
		// should still upload files since they didn't exist in sample folder
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadExistingSampleFileToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		String fileId = "4";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);
		
		// add sample file to map of already existing files
		LibraryContent fileContent = new LibraryContent();
		fileContent.setName(sampleFolder.getName() + "/" + dataFile1.getName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		
		// should not upload files since they do exist in sample folder
		verify(librariesClient, never()).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadOneExistingOneNewSampleFileToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		String fileId = "4";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);
		
		// add sample file to map of one already existing file
		LibraryContent fileContent = new LibraryContent();
		fileContent.setName(sampleFolder.getName() + "/" + dataFile1.getName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		
		// should only run once to upload one of the files
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSampleToLibraryWithIlluminaFolder() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFoldersWithIlluminaFolder();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSampleToLibraryWithReferencesFolder() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFoldersWithReferencesFolder();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderPath));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
		
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		when(librariesClient.uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class))).thenReturn(invalidResponse);
		
		assertFalse(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId1 = "3";
		String sampleFolderId2 = "4";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);		
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder1 = new LibraryFolder();
		sampleFolder1.setName(illuminaFolderPath + "/" + galaxySample1.getSampleName());
		sampleFolder1.setFolderId(sampleFolderId1);
		LibraryFolder sampleFolder2 = new LibraryFolder();
		sampleFolder2.setName(illuminaFolderPath + "/" + galaxySample2.getSampleName());
		sampleFolder2.setFolderId(sampleFolderId2);
		folders.add(sampleFolder1);
		folders.add(sampleFolder2);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData1"));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData2"));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
				
		assertEquals(libraryURL, workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail));
		verify(galaxySearch).findLibraryWithName(libraryName);
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSamplesToExistingLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, true);
				
		assertEquals(libraryURL, workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail));
		verify(galaxySearch).findLibraryWithName(libraryName);
		verify(galaxyLibrary, never()).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary, never()).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq("testData"));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoExistingLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, invalidLibraryId);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoCreateSampleFolder() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), any(String.class))).thenReturn(null);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, libraryId);
	}
	
	@Test
	public void testUploadNoFiles() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		setupLibraryFolders();
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
	}	
}
