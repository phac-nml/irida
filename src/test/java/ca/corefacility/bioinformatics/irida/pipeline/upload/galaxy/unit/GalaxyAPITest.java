package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.LibraryContentId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadEventListener;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryContentSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUserSearch;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

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
	private RolesClient rolesClient;
	@Mock
	private UsersClient usersClient;
	@Mock
	private ClientResponse okayResponse;
	@Mock
	private ClientResponse invalidResponse;

	@Mock
	private GalaxyLibrarySearch galaxySearch;
	@Mock
	private GalaxyLibraryContentSearch galaxyLibraryContentSearch;
	@Mock
	private GalaxyRoleSearch galaxyRoleSearch;
	@Mock
	private GalaxyUserSearch galaxyUserSearch;
	@Mock
	private GalaxyLibraryBuilder galaxyLibrary;
	
	@Mock
	private UploadEventListener uploadEventListener;
	
	@Mock
	private UniformInterfaceException uniformInterfaceException;

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
	LibraryContentId illuminaContentId = new LibraryContentId(libraryId, illuminaFolderPath);
	final private GalaxyFolderPath referencesFolderPath = new GalaxyFolderPath(
			"/references");
	LibraryContentId referencesContentId = new LibraryContentId(libraryId, referencesFolderPath);
	final private String galaxyURL = "http://localhost/";

	private GalaxyUploaderAPI workflowRESTAPI;
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
		when(galaxyInstance.getRolesClient()).thenReturn(rolesClient);
		when(galaxyInstance.getUsersClient()).thenReturn(usersClient);

		when(galaxyUserSearch.exists(realAdminEmail)).thenReturn(true);

		workflowRESTAPI = new GalaxyUploaderAPI(galaxyInstance, realAdminEmail,
				galaxySearch, galaxyLibraryContentSearch, galaxyRoleSearch, galaxyUserSearch, galaxyLibrary);
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

		when(galaxyUserSearch.findById(realUserEmail))
				.thenReturn(realUser);
		when(galaxyUserSearch.exists(realUserEmail)).thenReturn(true);
		when(galaxyRoleSearch.findById(realUserEmail)).thenReturn(
				realUserRole);
		when(galaxyRoleSearch.exists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findById(libraryId)).thenReturn(
				returnedLibrary);
		when(galaxyRoleSearch.findById(realAdminEmail)).thenReturn(
				realAdminRole);
		when(galaxyRoleSearch.exists(realAdminEmail)).thenReturn(true);
		when(galaxyLibrary.buildEmptyLibrary(libraryName)).thenReturn(
				returnedLibrary);
		when(
				galaxyLibrary.changeLibraryOwner(any(Library.class),
						eq(realUserEmail), eq(realAdminEmail))).thenReturn(
				returnedLibrary);
		when(galaxyLibraryContentSearch.libraryContentAsMap(libraryId))
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

		when(galaxyUserSearch.findById(realUserEmail))
				.thenReturn(realUser);
		when(galaxyUserSearch.exists(realUserEmail))
			.thenReturn(true);
		when(galaxyRoleSearch.findById(realUserEmail)).thenReturn(
				realUserRole);
		when(galaxyRoleSearch.exists(realUserEmail)).thenReturn(true);
		when(galaxySearch.findById(libraryId)).thenReturn(
				existingLibrary);
		when(galaxyRoleSearch.findById(realAdminEmail)).thenReturn(
				realAdminRole);
		when(galaxyRoleSearch.exists(realAdminEmail)).thenReturn(true);
		when(galaxySearch.findByName(libraryName)).thenReturn(
				libraries);
		when(galaxySearch.existsByName(libraryName)).thenReturn(true);
		when(galaxyLibraryContentSearch.libraryContentAsMap(libraryId))
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
				galaxyLibraryContentSearch.findById(illuminaContentId))
						.thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxyLibraryContentSearch.exists(illuminaContentId)).thenReturn(false);
		when(
				galaxyLibraryContentSearch.findById(referencesContentId))
						.thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxyLibraryContentSearch.exists(referencesContentId)).thenReturn(false);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(illuminaFolderName))).thenReturn(illuminaFolder);
		when(
				galaxyLibrary.createLibraryFolder(any(Library.class),
						eq(referencesFolderName))).thenReturn(referencesFolder);

		when(galaxySearch.findById(nonExistentLibraryId)).thenThrow(
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
				galaxyLibraryContentSearch.findById(illuminaContentId)).thenReturn(illuminaContent);
		when(
				galaxyLibraryContentSearch.exists(illuminaContentId)).thenReturn(true);
		when(
				galaxyLibraryContentSearch.findById(referencesContentId))
						.thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxyLibraryContentSearch.exists(referencesContentId)).thenReturn(false);
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
				galaxyLibraryContentSearch.findById(illuminaContentId))
						.thenThrow(new NoGalaxyContentFoundException());
		when(
				galaxyLibraryContentSearch.exists(illuminaContentId)).thenReturn(false);
		when(
				galaxyLibraryContentSearch.findById(referencesContentId)).thenReturn(referenceContent);
		when(
				galaxyLibraryContentSearch.exists(referencesContentId)).thenReturn(true);
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
				galaxyLibraryContentSearch.findById(illuminaContentId)).thenReturn(illuminaContent);
		when(
				galaxyLibraryContentSearch.exists(illuminaContentId)).thenReturn(true);
		when(
				galaxyLibraryContentSearch.findById(referencesContentId)).thenReturn(referenceContent);
		when(
				galaxyLibraryContentSearch.exists(referencesContentId)).thenReturn(true);
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
		
		URL url = new URL(galaxyURL);

		when(galaxyUserSearch.findById(fakeUserEmail))
			.thenThrow(new GalaxyUserNotFoundException(fakeUserEmail, url));

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

		workflowRESTAPI = new GalaxyUploaderAPI(galaxyInstance, nonExistentAdminEmail);
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
		when(galaxyUserSearch.findById(realUserEmail))
				.thenReturn(realUser);
		when(galaxyUserSearch.exists(realUserEmail)).thenReturn(true);
		when(galaxyRoleSearch.exists(realUserEmail))
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibraryContentSearch).findById(illuminaContentId);
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibraryContentSearch).findById(illuminaContentId);
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibraryContentSearch).findById(illuminaContentId);
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibraryContentSearch).findById(illuminaContentId);
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(referencesFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		verify(uploadEventListener).sampleProgressUpdate(2, 0, sample1Name);
		verify(uploadEventListener).sampleProgressUpdate(2, 1, sample2Name);
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
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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

		when(galaxyUserSearch.exists(realUserEmail)).thenReturn(true);
		when(galaxySearch.existsByName(libraryName)).thenReturn(false);

		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(
				samples, libraryName, realUserEmail));
		assertTrue(expectedUploadResult.newLocationCreated());
		verify(galaxySearch).existsByName(libraryName);
		verify(galaxyLibrary).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary).changeLibraryOwner(any(Library.class),
				eq(realUserEmail), eq(realAdminEmail));
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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

		when(galaxyUserSearch.exists(realUserEmail)).thenReturn(false);
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

		when(galaxyUserSearch.exists(realUserEmail)).thenReturn(true);

		assertEquals(expectedUploadResult, workflowRESTAPI.uploadSamples(
				samples, libraryName, realUserEmail));
		assertFalse(expectedUploadResult.newLocationCreated());
		verify(galaxySearch).existsByName(libraryName);
		verify(galaxySearch).findByName(libraryName);
		verify(galaxyLibrary, never()).buildEmptyLibrary(libraryName);
		verify(galaxyLibrary, never()).changeLibraryOwner(any(Library.class),
				eq(realUserEmail), eq(realAdminEmail));
		verify(galaxyLibraryContentSearch).exists(illuminaContentId);
		verify(galaxyLibrary).createLibraryFolder(any(Library.class),
				eq(illuminaFolderName));
		verify(galaxyLibraryContentSearch).exists(referencesContentId);
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
		when(galaxyUserSearch.exists(realAdminEmail)).thenReturn(true);
		
		assertTrue(workflowRESTAPI.isConnected());
	}
	
	/**
	 * Tests checking for connection in case of Galaxy improperly connected.
	 */
	@Test
	public void testIsConnectedInvalid() {
		when(galaxyUserSearch.exists(realAdminEmail)).thenReturn(false);
		
		assertFalse(workflowRESTAPI.isConnected());
	}
	
	/**
	 * Tests checking for connection in case of Galaxy improperly connected (exception).
	 */
	@Test
	public void testIsConnectedInvalidException() {
		when(galaxyUserSearch.exists(realAdminEmail)).thenThrow(new ClientHandlerException());
		
		assertFalse(workflowRESTAPI.isConnected());
	}
	
	/**
	 * Tests checking for connection in case of Galaxy improperly connected (exception).
	 */
	@Test
	public void testIsConnectedInvalidNewException() {
		when(galaxyUserSearch.exists(realAdminEmail)).thenThrow(uniformInterfaceException);
		
		assertFalse(workflowRESTAPI.isConnected());
	}
}
