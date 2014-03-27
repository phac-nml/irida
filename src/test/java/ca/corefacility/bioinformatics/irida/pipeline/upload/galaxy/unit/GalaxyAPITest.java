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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker.UploadEventListener;
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
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Unit tests for the Galaxy API.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyAPITest {
	@Mock
	private LibraryContent libraryContent;
	@Mock
	private GalaxyInstance galaxyInstance;
	@Mock
	private LibrariesClient librariesClient;
	@Mock
	private ClientResponse okayResponse;
	@Mock
	private ClientResponse invalidResponse;

	@Mock
	private GalaxySearch galaxySearch;
	@Mock
	private GalaxyLibraryBuilder galaxyLibrary;
	
	@Mock
	private UploadEventListener uploadEventListener;

	final private GalaxyAccountEmail realAdminEmail = new GalaxyAccountEmail(
			"admin@localhost");
	final private String libraryId = "1";
	final private String nonExistentLibraryId = "2";
	final private String rootFolderId = "2";
	final private GalaxyProjectName libraryName = new GalaxyProjectName(
			"TestLibrary");
	final private String realAdminAPIKey = "0";
	final private GalaxyAccountEmail nonExistentAdminEmail = new GalaxyAccountEmail(
			"admin_no_exist@localhost");
	final private GalaxyAccountEmail realUserEmail = new GalaxyAccountEmail(
			"test@localhost");
	final private GalaxyAccountEmail fakeUserEmail = new GalaxyAccountEmail(
			"fake@localhost");
	final private User realUser = new User();
	final private String realRoleId = "1";
	final private String adminRoleId = "0";
	final private GalaxyFolderName illuminaFolderName = new GalaxyFolderName(
			"illumina_reads");
	final private GalaxyFolderName referencesFolderName = new GalaxyFolderName(
			"references");
	final private GalaxyFolderPath illuminaFolderPath = new GalaxyFolderPath(
			"/illumina_reads");
	final private GalaxyFolderPath referencesFolderPath = new GalaxyFolderPath(
			"/references");
	final private String galaxyURL = "http://localhost/";

	private GalaxyAPI workflowRESTAPI;
	private Map<String, LibraryContent> libraryMap;
	private Path dataFile1;
	private Path dataFile2;
	private List<Path> dataFilesSingle;
	private List<Path> dataFilesDouble;
	private UploadResult expectedUploadResult;

	/**
	 * Setup mock objects for testing.
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 * @throws CreateLibraryException
	 * @throws GalaxyConnectException
	 */
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException,
			CreateLibraryException, GalaxyConnectException {
		MockitoAnnotations.initMocks(this);

		when(okayResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.FORBIDDEN);

		when(galaxyInstance.getApiKey()).thenReturn(realAdminAPIKey);
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		when(galaxyInstance.getGalaxyUrl()).thenReturn(galaxyURL);

		when(galaxySearch.galaxyUserExists(realAdminEmail)).thenReturn(true);

		workflowRESTAPI = new GalaxyAPI(galaxyInstance, realAdminEmail,
				galaxySearch, galaxyLibrary);
		workflowRESTAPI.setDataStorage(Uploader.DataStorage.REMOTE);

		// setup files
		dataFile1 = Paths.get(this.getClass().getResource("testData1.fastq")
				.toURI());
		dataFile2 = Paths.get(this.getClass().getResource("testData2.fastq")
				.toURI());

		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);

		dataFilesDouble = new ArrayList<Path>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);

		libraryMap = new HashMap<String, LibraryContent>();

		realUser.setEmail(realUserEmail.getName());
	}

	/**
	 * Setup objects and return values for bulding libraries.
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	private void setupBuildLibrary() throws MalformedURLException,
			UploadException {
		Library returnedLibrary = new Library(libraryName.getName());
		returnedLibrary.setId(libraryId);
		returnedLibrary.setUrl("/api/libraries/" + libraryId);
		expectedUploadResult = new GalaxyUploadResult(returnedLibrary,
				libraryName, realUserEmail, galaxyURL);

		Role realUserRole = new Role();
		realUserRole.setName(realUserEmail.getName());
		realUserRole.setId(realRoleId);

		Role realAdminRole = new Role();
		realAdminRole.setName(realAdminEmail.getName());
		realAdminRole.setId(adminRoleId);

		when(galaxySearch.findUserWithEmail(realUserEmail))
				.thenReturn(realUser);
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(
				realUserRole);
		when(galaxySearch.userRoleExistsFor(realUserEmail)).thenReturn(true);
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(
				returnedLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(
				realAdminRole);
		when(galaxySearch.userRoleExistsFor(realAdminEmail)).thenReturn(true);
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(
				returnedLibrary);
		when(
				galaxyLibrary.changeLibraryOwner(any(Library.class),
						eq(realUserEmail), eq(realAdminEmail))).thenReturn(
				returnedLibrary);
		when(galaxySearch.libraryContentAsMap(libraryId))
				.thenReturn(libraryMap);
	}

	/**
	 * Setup objects for uploading to existing libraries.
	 * @throws UploadException
	 * @throws MalformedURLException
	 */
	private void setupExisitingLibrary() throws UploadException,
			MalformedURLException {
		List<Library> libraries = new LinkedList<Library>();
		Library existingLibrary = new Library(libraryName.getName());
		existingLibrary.setId(libraryId);
		libraries.add(existingLibrary);
		existingLibrary.setUrl("/api/libraries/" + libraryId);
		expectedUploadResult = new GalaxyUploadResult(existingLibrary,
				libraryName, null, galaxyURL);

		User realUser = new User();
		realUser.setEmail(realUserEmail.getName());

		Role realUserRole = new Role();
		realUserRole.setName(realUserEmail.getName());
		realUserRole.setId(realRoleId);

		Role realAdminRole = new Role();
		realAdminRole.setName(realAdminEmail.getName());
		realAdminRole.setId(adminRoleId);

		when(galaxySearch.findUserWithEmail(realUserEmail))
				.thenReturn(realUser);
		when(galaxySearch.galaxyUserExists(realUserEmail))
			.thenReturn(true);
		when(galaxySearch.findUserRoleWithEmail(realUserEmail)).thenReturn(
				realUserRole);
		when(galaxySearch.userRoleExistsFor(realUserEmail)).thenReturn(true);
		when(galaxySearch.findLibraryWithId(libraryId)).thenReturn(
				existingLibrary);
		when(galaxySearch.findUserRoleWithEmail(realAdminEmail)).thenReturn(
				realAdminRole);
		when(galaxySearch.userRoleExistsFor(realAdminEmail)).thenReturn(true);
		when(galaxySearch.findLibraryWithName(libraryName)).thenReturn(
				libraries);
		when(galaxySearch.libraryExists(libraryName)).thenReturn(true);
		when(galaxySearch.libraryContentAsMap(libraryId))
				.thenReturn(libraryMap);
	}

	/**
	 * Setup objects for uploading a list of samples to a list of library folders within a library.
	 * @param samples  A list of samples to upload.
	 * @param folders  A list of folders (parallel to samples) to upload into.
	 * @param libraryExists  True if the library already exists, false otherwise.
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	private void setupUploadSampleToLibrary(List<UploadSample> samples,
			List<LibraryFolder> folders, boolean libraryExists)
			throws MalformedURLException, UploadException {
		if (libraryExists) {
			setupExisitingLibrary();
		} else {
			setupBuildLibrary();
		}

		libraryMap.put("/", libraryContent);
		when(librariesClient.getRootFolder(libraryId)).thenReturn(
				libraryContent);
		when(libraryContent.getId()).thenReturn(rootFolderId);
		when(
				librariesClient.uploadFilesystemPathsRequest(eq(libraryId),
						any(FilesystemPathsLibraryUpload.class))).thenReturn(
				okayResponse);

		for (int i = 0; i < samples.size(); i++) {
			UploadSample sample = samples.get(i);
			LibraryFolder folder = folders.get(i);

			UploadFolderName sampleName = sample.getSampleName();

			when(
					galaxyLibrary.createLibraryFolder(any(Library.class),
							any(LibraryFolder.class), eq(sampleName)))
					.thenReturn(folder);
		}
	}

	/**
	 * Setup return values for handling library folders.
	 * @throws UploadException
	 */
	private void setupLibraryFolders() throws UploadException {
		LibraryFolder referencesFolder = new LibraryFolder();
		referencesFolder.setName(referencesFolderPath.getName());
		referencesFolder.setFolderId(rootFolderId);

		LibraryFolder illuminaFolder = new LibraryFolder();
		illuminaFolder.setName(illuminaFolderPath.getName());
		illuminaFolder.setFolderId(rootFolderId);

		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						illuminaFolderPath)).thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxySearch.libraryContentExists(libraryId,
						illuminaFolderPath)).thenReturn(false);
		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						referencesFolderPath)).thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxySearch.libraryContentExists(libraryId,
						referencesFolderPath)).thenReturn(false);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(referencesFolderName))).thenReturn(referencesFolder);

		when(galaxySearch.findLibraryWithId(nonExistentLibraryId)).thenThrow(
				new NoLibraryFoundException());
	}

	/**
	 * Setup return values for existing library with only an illumina folder.
	 * @throws UploadException
	 */
	private void setupLibraryFoldersWithIlluminaFolder() throws UploadException {
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

		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						illuminaFolderPath)).thenReturn(illuminaContent);
		when(
				galaxySearch.libraryContentExists(libraryId,
						illuminaFolderPath)).thenReturn(true);
		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						referencesFolderPath)).thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxySearch.libraryContentExists(libraryId,
						referencesFolderPath)).thenReturn(false);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(referencesFolderName))).thenReturn(referencesFolder);
	}

	/**
	 * Setup return values for an existing library with a reference folder.
	 * @throws UploadException
	 */
	private void setupLibraryFoldersWithReferencesFolder()
			throws UploadException {
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

		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						illuminaFolderPath)).thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxySearch.libraryContentExists(libraryId,
						illuminaFolderPath)).thenReturn(false);
		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						referencesFolderPath)).thenReturn(referenceContent);
		when(
				galaxySearch.libraryContentExists(libraryId,
						referencesFolderPath)).thenReturn(true);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(referencesFolderName))).thenReturn(referencesFolder);
	}

	/**
	 * Setup return values for a library with both illumina and reference folders.
	 * @throws UploadException
	 */
	private void setupLibraryFoldersWithBothFolders() throws UploadException {
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

		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						illuminaFolderPath)).thenReturn(illuminaContent);
		when(
				galaxySearch.libraryContentExists(libraryId,
						illuminaFolderPath)).thenReturn(true);
		when(
				galaxySearch.findLibraryContentWithId(libraryId,
						referencesFolderPath)).thenReturn(referenceContent);
		when(
				galaxySearch.libraryContentExists(libraryId,
						referencesFolderPath)).thenReturn(true);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(referencesFolderName))).thenReturn(referencesFolder);
	}

	/**
	 * Test building a Galaxy library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupBuildLibrary();

		assertEquals(libraryId,
				workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail)
						.getId());
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class),
				eq(realUserEmail), eq(realAdminEmail));
	}

	/**
	 * Test failure to build a Galaxy library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = CreateLibraryException.class)
	public void testBuildGalaxyLibraryFail() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupBuildLibrary();

		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenThrow(
				new CreateLibraryException());

		workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail);
	}

	/**
	 * Test build Galaxy library with unknown user.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyUserNotFoundException.class)
	public void testBuildGalaxyLibraryNoUser() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupBuildLibrary();

		when(galaxySearch.findUserWithEmail(fakeUserEmail)).thenThrow(new GalaxyUserNotFoundException());

		workflowRESTAPI.buildGalaxyLibrary(libraryName, fakeUserEmail);
	}

	/**
	 * Test setup API with unknown admin email.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testSetupUnknownAdminEmail() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException {
		setupBuildLibrary();

		workflowRESTAPI = new GalaxyAPI(galaxyInstance, nonExistentAdminEmail);
	}

	/**
	 * Test build Galaxy library with no user role found.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyUserNoRoleException.class)
	public void testBuildGalaxyLibraryNoUserRole() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		when(galaxySearch.findUserWithEmail(realUserEmail))
				.thenReturn(realUser);
		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.userRoleExistsFor(realUserEmail))
				.thenReturn(false);

		workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail);
	}

	/**
	 * Test build Galaxy library with error changing permissions.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = ChangeLibraryPermissionsException.class)
	public void testBuildGalaxyLibraryNoSetPermissions()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupBuildLibrary();

		when(
				galaxyLibrary.changeLibraryOwner(any(Library.class),
						eq(realUserEmail), eq(realAdminEmail))).thenThrow(
				new ChangeLibraryPermissionsException());

		workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail);
	}

	/**
	 * Test upload a sample to a library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleToLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Test upload a sample to a library which already contains that sample folder.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadExistingSampleFolderToLibrary()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupLibraryFoldersWithBothFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));

		// should still upload files since they didn't exist in sample folder
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading sample file to library where sample already exists.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadExistingSampleFileToLibrary()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupLibraryFoldersWithBothFolders();

		String sampleFolderId = "3";
		String fileId = "4";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);

		// add sample file to map of already existing files
		LibraryContent fileContent = new LibraryContent();
		fileContent.setName(sampleFolder.getName() + "/"
				+ dataFile1.getFileName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));

		// should not upload files since they do exist in sample folder
		verify(librariesClient, never()).uploadFilesystemPathsRequest(
				eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading mixture of existing/non existing sample files to library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadOneExistingOneNewSampleFileToLibrary()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupLibraryFoldersWithBothFolders();

		String sampleFolderId = "3";
		String fileId = "4";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesDouble);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		// add sample folder to map of already existing folders
		LibraryContent folderContent = new LibraryContent();
		folderContent.setName(sampleFolder.getName());
		folderContent.setId(sampleFolder.getFolderId());
		libraryMap.put(sampleFolder.getName(), folderContent);

		// add sample file to map of one already existing file
		LibraryContent fileContent = new LibraryContent();
		fileContent.setName(sampleFolder.getName() + "/"
				+ dataFile1.getFileName());
		fileContent.setId(fileId);
		libraryMap.put(fileContent.getName(), fileContent);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));

		// should only run once to upload one of the files
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading sample to library with illumina folder (don't want multiple folders created).
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleToLibraryWithIlluminaFolder()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupLibraryFoldersWithIlluminaFolder();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).findLibraryContentWithId(libraryId,
				illuminaFolderPath);
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading a sample to a library which contains a references folder (no multiple folders created).
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleToLibraryWithReferencesFolder()
			throws URISyntaxException, MalformedURLException, UploadException {
		setupLibraryFoldersWithReferencesFolder();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> sampleFolders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		sampleFolders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, sampleFolders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary, never()).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading library to file and it fails.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);
		when(
				librariesClient.uploadFilesystemPathsRequest(eq(libraryId),
						any(FilesystemPathsLibraryUpload.class))).thenReturn(
				invalidResponse);

		assertFalse(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading multiple samples to a library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId1 = "3";
		String sampleFolderId2 = "4";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(new GalaxyFolderName(
				"testData2"), dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder1 = new LibraryFolder();
		sampleFolder1.setName(illuminaFolderPath + "/"
				+ galaxySample1.getSampleName());
		sampleFolder1.setFolderId(sampleFolderId1);
		LibraryFolder sampleFolder2 = new LibraryFolder();
		sampleFolder2.setName(illuminaFolderPath + "/"
				+ galaxySample2.getSampleName());
		sampleFolder2.setFolderId(sampleFolderId2);
		folders.add(sampleFolder1);
		folders.add(sampleFolder2);

		setupUploadSampleToLibrary(samples, folders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary)
				.createLibraryFolder(any(Library.class),
						any(LibraryFolder.class),
						eq(new GalaxyFolderName("testData1")));
		verify(galaxyLibrary)
				.createLibraryFolder(any(Library.class),
						any(LibraryFolder.class),
						eq(new GalaxyFolderName("testData2")));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(
				eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}
	
	/**
	 * Tests that the sample progress listener receives events.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testSampleProgressMonitor() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();
		
		String sampleFolderId1 = "3";
		String sampleFolderId2 = "4";
		
		GalaxyFolderName sample1Name = new GalaxyFolderName("testData1");
		GalaxyFolderName sample2Name = new GalaxyFolderName("testData2");

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(sample1Name, dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(sample2Name, dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder1 = new LibraryFolder();
		sampleFolder1.setName(illuminaFolderPath + "/"
				+ galaxySample1.getSampleName());
		sampleFolder1.setFolderId(sampleFolderId1);
		LibraryFolder sampleFolder2 = new LibraryFolder();
		sampleFolder2.setName(illuminaFolderPath + "/"
				+ galaxySample2.getSampleName());
		sampleFolder2.setFolderId(sampleFolderId2);
		folders.add(sampleFolder1);
		folders.add(sampleFolder2);

		setupUploadSampleToLibrary(samples, folders, false);

		workflowRESTAPI.addUploadEventListener(uploadEventListener);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(uploadEventListener).progressUpdate(2, 1, sample1Name);
		verify(uploadEventListener).progressUpdate(2, 2, sample2Name);
	}

	/**
	 * Tests uploading sample with multiple files to library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesDouble);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient, times(2)).uploadFilesystemPathsRequest(
				eq(libraryId), any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading samples (with creating library).
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSamples() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);

		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);
		when(galaxySearch.libraryExists(libraryName)).thenReturn(false);

		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(
				samples, libraryName, realUserEmail));
		assertTrue(expectedUploadResult.newLocationCreated());
		verify(galaxySearch).libraryExists(libraryName);
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class),
				eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading samples, no user exists.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyUserNotFoundException.class)
	public void testUploadSamplesUserDoesNotExist() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);

		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(false);
		workflowRESTAPI.uploadSamples(samples, libraryName, realUserEmail);
	}

	/**
	 * Tests uploading samples to existing library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSamplesToExistingLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, true);

		when(galaxySearch.galaxyUserExists(realUserEmail)).thenReturn(true);

		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(
				samples, libraryName, realUserEmail));
		assertFalse(expectedUploadResult.newLocationCreated());
		verify(galaxySearch).libraryExists(libraryName);
		verify(galaxySearch).findLibraryWithName(libraryName);
		verify(galaxyLibrary, never()).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary, never()).changeLibraryOwner(any(Library.class),
				eq(realUserEmail), eq(realAdminEmail));
		verify(galaxySearch).libraryContentExists(libraryId,
				illuminaFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxySearch).libraryContentExists(libraryId,
				referencesFolderPath);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				any(LibraryFolder.class), eq(new GalaxyFolderName("testData")));
		verify(librariesClient).uploadFilesystemPathsRequest(eq(libraryId),
				any(FilesystemPathsLibraryUpload.class));
	}

	/**
	 * Tests uploading files to non-existing file.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = NoLibraryFoundException.class)
	public void testNoExistingLibrary() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);

		workflowRESTAPI.uploadFilesToLibrary(samples, nonExistentLibraryId);
	}

	/**
	 * Tests uploading files to library, error creating sample folder.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test(expected = CreateLibraryException.class)
	public void testNoCreateSampleFolder() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		String sampleFolderId = "3";

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		samples.add(galaxySample);

		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(illuminaFolderPath + "/"
				+ galaxySample.getSampleName());
		sampleFolder.setFolderId(sampleFolderId);
		folders.add(sampleFolder);

		setupUploadSampleToLibrary(samples, folders, false);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						any(GalaxyFolderName.class))).thenThrow(
				new CreateLibraryException());

		workflowRESTAPI.uploadFilesToLibrary(samples, libraryId);
	}

	/**
	 * Tests uploading empty list of samples.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws UploadException
	 */
	@Test
	public void testUploadNoFiles() throws URISyntaxException,
			MalformedURLException, UploadException {
		setupLibraryFolders();

		List<UploadSample> samples = new ArrayList<UploadSample>();
		List<LibraryFolder> folders = new ArrayList<LibraryFolder>();

		setupUploadSampleToLibrary(samples, folders, false);

		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryId));
	}
	
	/**
	 * Tests checking for connection in case of Galaxy properly connected.
	 */
	@Test
	public void testIsConnectedValid() {
		when(galaxySearch.galaxyUserExists(realAdminEmail)).thenReturn(true);
		
		assertTrue(workflowRESTAPI.isConnected());
	}
	
	/**
	 * Tests checking for connection in case of Galaxy improperly connected.
	 */
	@Test
	public void testIsConnectedInvalid() {
		when(galaxySearch.galaxyUserExists(realAdminEmail)).thenReturn(false);
		
		assertFalse(workflowRESTAPI.isConnected());
	}
	
	/**
	 * Tests checking for connection in case of Galaxy improperly connected (exception).
	 */
	@Test
	public void testIsConnectedInvalidException() {
		when(galaxySearch.galaxyUserExists(realAdminEmail)).thenThrow(new ClientHandlerException());
		
		assertFalse(workflowRESTAPI.isConnected());
	}
}
