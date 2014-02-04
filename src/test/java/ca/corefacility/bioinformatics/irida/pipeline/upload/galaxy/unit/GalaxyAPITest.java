package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

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
	@Mock private GalaxyLibraryBuilder galaxyLibrary;
		
	final private GalaxyAccountEmail realAdminEmail = new GalaxyAccountEmail("admin@localhost");
	final private String libraryId = "1";
	final private String nonExistentLibraryId = "2";
	final private String rootFolderId = "2";
	final private GalaxyObjectName libraryName = new GalaxyObjectName("TestLibrary");
	final private String realAdminAPIKey = "0";
	final private GalaxyAccountEmail nonExistentAdminEmail = new GalaxyAccountEmail("admin_no_exist@localhost");
	final private GalaxyAccountEmail realUserEmail = new GalaxyAccountEmail("test@localhost");
	final private GalaxyAccountEmail fakeUserEmail = new GalaxyAccountEmail("fake@localhost");
	final private User realUser = new User();
	final private String realRoleId = "1";
	final private String adminRoleId = "0";
	final private GalaxyObjectName illuminaFolderName = new GalaxyObjectName("illumina_reads");
	final private GalaxyObjectName referencesFolderName = new GalaxyObjectName("references");
	final private GalaxyFolderPath illuminaFolderPath = new GalaxyFolderPath("/illumina_reads");
	final private GalaxyFolderPath referencesFolderPath = new GalaxyFolderPath("/references");
	final private String galaxyURL = "http://localhost/";
		
	private GalaxyAPI workflowRESTAPI;
	private Map<String, LibraryContent> libraryMap;
	private Path dataFile1;
	private Path dataFile2;
	private List<Path> dataFilesSingle;
	private List<Path> dataFilesDouble;
	private UploadResult expectedUploadResult;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException, CreateLibraryException, GalaxyConnectException
	{		
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
				
		when(galaxyInstance.getApiKey()).thenReturn(realAdminAPIKey);
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		when(galaxyInstance.getGalaxyUrl()).thenReturn(galaxyURL);
		
		when(galaxySearch.galaxyUserExists(realAdminEmail)).
			thenReturn(true);
				
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, realAdminEmail, galaxySearch, galaxyLibrary);
		workflowRESTAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		// setup files
		dataFile1 = Paths.get(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = Paths.get(this.getClass().getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<Path>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
		
		libraryMap = new HashMap<String, LibraryContent>();	

		realUser.setEmail(realUserEmail.getName());
	}
	
	private void setupBuildLibrary() throws MalformedURLException, UploadException
	{
		Library returnedLibrary = new Library(libraryName.getName());
		returnedLibrary.setId(libraryId);
		returnedLibrary.setUrl("/api/libraries/" + libraryId);
		expectedUploadResult = new GalaxyUploadResult(returnedLibrary, libraryName,
				realUserEmail, galaxyURL);
		
		Role realUserRole = new Role();
		realUserRole.setName(realUserEmail.getName());
		realUserRole.setId(realRoleId);
		
		Role realAdminRole = new Role();
		realAdminRole.setName(realAdminEmail.getName());
		realAdminRole.setId(adminRoleId);
		
		when(galaxySearch.findUserWithEmail(realUserEmail)).thenReturn(realUser);
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(realUserRole);
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(returnedLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(realAdminRole);
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(returnedLibrary);
		when(galaxyLibrary.changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail)))
			.thenReturn(returnedLibrary);
		when(galaxySearch.libraryContentAsMap(libraryId)).thenReturn(libraryMap);
	}
	
	private void setupExisitingLibrary() throws UploadException, MalformedURLException
	{
		List<Library> libraries = new LinkedList<Library>();
		Library existingLibrary = new Library(libraryName.getName());
		existingLibrary.setId(libraryId);
		libraries.add(existingLibrary);
		existingLibrary.setUrl("/api/libraries/" + libraryId);
		expectedUploadResult = new GalaxyUploadResult(existingLibrary, libraryName,
				realUserEmail, galaxyURL);
				
		User realUser = new User();
		realUser.setEmail(realUserEmail.getName());
		
		Role realUserRole = new Role();
		realUserRole.setName(realUserEmail.getName());
		realUserRole.setId(realRoleId);
		
		Role realAdminRole = new Role();
		realAdminRole.setName(realAdminEmail.getName());
		realAdminRole.setId(adminRoleId);
		
		when(galaxySearch.findUserWithEmail(realUserEmail)).thenReturn(realUser);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(realUserRole);
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(existingLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(realAdminRole);
		when(galaxySearch.findLibraryWithName(libraryName)).thenReturn(libraries);
		when(galaxySearch.libraryContentAsMap(libraryId)).thenReturn(libraryMap);
	}
	
	private void setupUploadSampleToLibrary(List<UploadSample> samples, List<LibraryFolder> folders,
			boolean libraryExists) throws MalformedURLException, UploadException
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
			UploadSample sample = samples.get(i);
			LibraryFolder folder = folders.get(i);
			
			UploadObjectName sampleName = sample.getSampleName();
			
			when(galaxyLibrary.createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq(sampleName))).thenReturn(folder);
		}
	}
	
	private void setupLibraryFolders() throws UploadException
	{		
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath.getName());
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath.getName());
		illuminaFolder.setFolderId(rootFolderId);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(null);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(null);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
		
		when(galaxySearch.findLibraryWithId(nonExistentLibraryId)).thenReturn(null);
	}
	
	private void setupLibraryFoldersWithIlluminaFolder() throws UploadException
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath.getName());
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath.getName());
		illuminaFolder.setFolderId(rootFolderId);
		
		LibraryContent illuminaContent = new LibraryContent();
		illuminaContent.setName(illuminaFolderPath.getName());
		illuminaContent.setType("folder");
		libraryMap.put(illuminaFolderPath.getName(), illuminaContent);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(illuminaContent);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(null);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	private void setupLibraryFoldersWithReferencesFolder() throws UploadException
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath.getName());
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryContent referenceContent = new LibraryContent();
		referenceContent.setName(referencesFolderPath.getName());
		referenceContent.setType("folder");
		libraryMap.put(referencesFolderPath.getName(), referenceContent);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath.getName());
		illuminaFolder.setFolderId(rootFolderId);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(null);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(referenceContent);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	private void setupLibraryFoldersWithBothFolders() throws UploadException
	{
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath.getName());
		referencesFolder.setFolderId(rootFolderId);
		
		LibraryContent referenceContent = new LibraryContent();
		referenceContent.setName(referencesFolderPath.getName());
		referenceContent.setType("folder");
		libraryMap.put(referencesFolderPath.getName(), referenceContent);
		
		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath.getName());
		illuminaFolder.setFolderId(rootFolderId);
		
		LibraryContent illuminaContent = new LibraryContent();
		illuminaContent.setName(illuminaFolderPath.getName());
		illuminaContent.setType("folder");
		libraryMap.put(illuminaFolderPath.getName(), illuminaContent);
		
		when(galaxySearch.findLibraryContentWithId(libraryId, illuminaFolderPath)).thenReturn(illuminaContent);
		when(galaxySearch.findLibraryContentWithId(libraryId, referencesFolderPath)).thenReturn(referenceContent);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), eq(referencesFolderName))).thenReturn(referencesFolder);
	}
	
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{	
		setupBuildLibrary();
		
		assertEquals(libraryId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail).getId());
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryFail() throws URISyntaxException, MalformedURLException, UploadException
	{	
		setupBuildLibrary();
		
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenThrow(new CreateLibraryException());
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail);
	}
	
	@Test(expected=GalaxyUserNotFoundException.class)
	public void testBuildGalaxyLibraryNoUser() throws URISyntaxException, MalformedURLException, UploadException
	{	
		setupBuildLibrary();
		
		when(galaxySearch.findUserWithEmail(fakeUserEmail)).thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,fakeUserEmail);
	}
	
	@Test(expected=GalaxyConnectException.class)
	public void testSetupInvalidAdminEmail() throws URISyntaxException, MalformedURLException, ConstraintViolationException, UploadException
	{
		setupBuildLibrary();
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, nonExistentAdminEmail);
	}
	
	@Test(expected=GalaxyUserNoRoleException.class)
	public void testBuildGalaxyLibraryNoUserRole() throws URISyntaxException, ConstraintViolationException, UploadException
	{
		when(galaxySearch.findUserWithEmail(realUserEmail)).thenReturn(realUser);
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(null);
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test(expected=ChangeLibraryPermissionsException.class)
	public void testBuildGalaxyLibraryNoSetPermissions() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupBuildLibrary();
		
		when(galaxyLibrary.changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail)))
			.thenThrow(new ChangeLibraryPermissionsException());
		
		workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail);
	}
	
	@Test
	public void testUploadSampleToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
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
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class), eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadExistingSampleFolderToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
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
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		
		// should still upload files since they didn't exist in sample folder
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadExistingSampleFileToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		String fileId = "4";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
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
		fileContent.setName(sampleFolder.getName() + "/" + dataFile1.getFileName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		
		// should not upload files since they do exist in sample folder
		verify(librariesClient, never()).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadOneExistingOneNewSampleFileToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFoldersWithBothFolders();
		
		String sampleFolderId = "3";
		String fileId = "4";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesDouble);
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
		fileContent.setName(sampleFolder.getName() + "/" + dataFile1.getFileName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		
		// should only run once to upload one of the files
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSampleToLibraryWithIlluminaFolder() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFoldersWithIlluminaFolder();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
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
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSampleToLibraryWithReferencesFolder() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFoldersWithReferencesFolder();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, sampleFolders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
		
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
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
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId1 = "3";
		String sampleFolderId2 = "4";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyObjectName("testData1"), dataFilesSingle);		
		GalaxySample galaxySample2 = new GalaxySample(new GalaxyObjectName("testData2"), dataFilesSingle);
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
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData1")));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData2")));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesDouble);
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
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findLibraryWithName(libraryName)).thenReturn(new LinkedList<Library>());
				
		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail));
		verify(galaxySearch).findLibraryWithName(libraryName);
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test(expected=GalaxyUserNotFoundException.class)
	public void testUploadSamplesUserDoesNotExist() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(false);
		workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail);
	}
	
	@Test
	public void testUploadSamplesToExistingLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, true);
				
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		
		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail));
		verify(galaxySearch).findLibraryWithName(libraryName);
		verify(galaxyLibrary, never()).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary, never()).changeLibraryOwner(any(Library.class), eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).findLibraryContentWithId(libraryId, illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(referencesFolderName));
		verify(galaxySearch).findLibraryContentWithId(libraryId, referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class), any(LibraryFolder.class),
				eq(new GalaxyObjectName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	@Test(expected=NoLibraryFoundException.class)
	public void testNoExistingLibrary() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, nonExistentLibraryId);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testNoCreateSampleFolder() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		String sampleFolderId = "3";
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		samples.add(galaxySample);
		
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/" + galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);
		
		setupUploadSampleToLibrary(samples, folders, false);
		when(galaxyLibrary.createLibraryFolder(any(Library.class), any(GalaxyObjectName.class)))
			.thenThrow(new CreateLibraryException());
		
		workflowRESTAPI.uploadFilesToLibrary(samples, libraryId);
	}
	
	@Test
	public void testUploadNoFiles() throws URISyntaxException, MalformedURLException, UploadException
	{
		setupLibraryFolders();
		
		List<UploadSample> samples = new ArrayList<UploadSample>();
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		
		setupUploadSampleToLibrary(samples, folders, false);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
	}	
}
