package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Unit tests for GalaxySearch.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxySearchTest {
	@Mock
	private RolesClient rolesClient;
	@Mock
	private UsersClient usersClient;
	@Mock
	private LibrariesClient librariesClient;
	@Mock
	private GalaxyInstance galaxyInstance;

	private GalaxySearch galaxySearch;

	private static final String LIBRARY_ID = "1";
	private static final String LIBRARY_ID_2 = "2";
	private static final String LIBRARY_ID_3 = "3";
	private static final String INVALID_LIBRARY_ID = "0";
	private static final GalaxyProjectName LIBRARY_NAME = new GalaxyProjectName(
			"Test");
	private static final GalaxyProjectName INVALID_LIBRARY_NAME = new GalaxyProjectName(
			"InvalidTest");
	private static final UploadProjectName LIBRARY_NAME_3 = new GalaxyProjectName(
			"Test3");
	private static final String LIBRARY_ID_MULTIPLE_CONTENTS = "10";
	private static final GalaxyFolderPath FOLDER_PATH = new GalaxyFolderPath(
			"test_folder");
	private static final GalaxyFolderPath ILLUMINA_FOLDER_NAME = new GalaxyFolderPath(
			"test_folder/illumina");
	private static final GalaxyFolderPath INVALID_FOLDER_NAME = new GalaxyFolderPath(
			"invalid_folder");

	private List<Library> allLibrariesList;
	private Map<String, LibraryContent> singleLibraryContentsAsMap;
	private Map<String, LibraryContent> multipleLibraryContentsAsMap;

	/**
	 * Setup objects for test.
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException {
		MockitoAnnotations.initMocks(this);

		setupRolesTest();
		setupUserTest();
		setupLibraryTest();
		setupLibraryContentTest();

		galaxySearch = new GalaxySearch(galaxyInstance);
	}

	/**
	 * Setup user roles.
	 */
	private void setupRolesTest() {
		when(galaxyInstance.getRolesClient()).thenReturn(rolesClient);

		Role role1 = new Role();
		role1.setName("role1@localhost");
		role1.setId("1");

		Role role2 = new Role();
		role2.setName("role2@localhost");
		role2.setId("2");

		List<Role> roleList = new ArrayList<Role>();
		roleList.add(role1);
		roleList.add(role2);

		when(rolesClient.getRoles()).thenReturn(roleList);
	}

	/**
	 * Setup users.
	 */
	private void setupUserTest() {
		when(galaxyInstance.getUsersClient()).thenReturn(usersClient);

		User user1 = new User();
		user1.setEmail("user1@localhost");
		user1.setId("1");

		User user2 = new User();
		user2.setEmail("user2@localhost");
		user2.setId("2");

		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);

		when(usersClient.getUsers()).thenReturn(userList);
	}

	/**
	 * Setup libraries.
	 */
	private void setupLibraryTest() {
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);

		Library library = new Library(LIBRARY_NAME.getName());
		library.setId(LIBRARY_ID);

		allLibrariesList = new ArrayList<Library>();
		allLibrariesList.add(library);

		when(librariesClient.getLibraries()).thenReturn(allLibrariesList);
	}

	/**
	 * Setup library contents.
	 */
	private void setupLibraryContentTest() {
		List<LibraryContent> singleLibraryContents = new ArrayList<LibraryContent>();
		LibraryContent validFolder = new LibraryContent();
		validFolder.setName(FOLDER_PATH.getName());
		validFolder.setType("folder");
		singleLibraryContents.add(validFolder);

		when(librariesClient.getLibraryContents(LIBRARY_ID)).thenReturn(
				singleLibraryContents);

		singleLibraryContentsAsMap = new HashMap<String, LibraryContent>();
		singleLibraryContentsAsMap.put(FOLDER_PATH.getName(), validFolder);

		List<LibraryContent> multipleLibraryContents = new ArrayList<LibraryContent>();
		LibraryContent validFolder1 = new LibraryContent();
		validFolder1.setName(FOLDER_PATH.getName());
		validFolder1.setType("folder");
		multipleLibraryContents.add(validFolder1);

		LibraryContent validFolder2 = new LibraryContent();
		validFolder2.setName(ILLUMINA_FOLDER_NAME.getName());
		validFolder2.setType("folder");
		multipleLibraryContents.add(validFolder2);

		when(librariesClient.getLibraryContents(LIBRARY_ID_MULTIPLE_CONTENTS))
				.thenReturn(multipleLibraryContents);

		multipleLibraryContentsAsMap = new HashMap<String, LibraryContent>();
		multipleLibraryContentsAsMap.put(FOLDER_PATH.getName(), validFolder1);
		multipleLibraryContentsAsMap.put(ILLUMINA_FOLDER_NAME.getName(),
				validFolder2);

		when(librariesClient.getLibraryContents(INVALID_LIBRARY_ID))
				.thenReturn(null);
	}

	/**
	 * Convert list of libraries to an array list.
	 * @param libraries  A general list of libraries.
	 * @return An array list of libraries.
	 */
	private ArrayList<Library> convertLibraryToArrayList(List<Library> libraries) {
		ArrayList<Library> newLibraries = new ArrayList<Library>();

		for (Library library : libraries) {
			newLibraries.add(library);
		}

		return newLibraries;
	}

	/**
	 * Tests finding a library.
	 * @throws NoLibraryFoundException
	 */
	@Test
	public void testFindLibrary() throws NoLibraryFoundException {
		Library library = galaxySearch.findLibraryWithId(LIBRARY_ID);
		assertNotNull(library);
		assertEquals(LIBRARY_ID, library.getId());
		assertEquals(LIBRARY_NAME.getName(), library.getName());
	}

	/**
	 * Tests not finding a library.
	 * @throws NoLibraryFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testNoFindLibrary() throws NoLibraryFoundException {
		galaxySearch.findLibraryWithId(INVALID_LIBRARY_ID);
	}

	/**
	 * Tests finding a library by name.
	 * @throws NoLibraryFoundException 
	 */
	@Test
	public void testFindLibraryByName() throws NoLibraryFoundException {
		List<Library> libraries = galaxySearch
				.findLibraryWithName(LIBRARY_NAME);
		List<Library> returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(allLibrariesList, returnedLibraries);

		// add new library to all libraries list with same name
		Library newLibrary = new Library(LIBRARY_NAME.getName());
		newLibrary.setId(LIBRARY_ID_2);
		allLibrariesList.add(newLibrary);

		libraries = galaxySearch.findLibraryWithName(LIBRARY_NAME);
		returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(allLibrariesList, returnedLibraries);

		// add new library to all libraries with different name
		// expected list (equal to above returnedLibraries list)
		List<Library> expectedList = returnedLibraries;
		newLibrary = new Library(LIBRARY_NAME_3.getName());
		newLibrary.setId(LIBRARY_ID_3);
		allLibrariesList.add(newLibrary);

		libraries = galaxySearch.findLibraryWithName(LIBRARY_NAME);
		returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(expectedList, returnedLibraries);
	}

	/**
	 * Tests finding invalid library.
	 * @throws NoLibraryFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testInvalidFindLibraryByName() throws NoLibraryFoundException {
		galaxySearch.findLibraryWithName(INVALID_LIBRARY_NAME);
	}
	
	/**
	 * Tests checking existence of library.
	 */
	@Test
	public void testLibraryExists() {
		assertTrue(galaxySearch.libraryExists(LIBRARY_NAME));
	}
	
	/**
	 * Tests checking non-existence of library.
	 */
	@Test
	public void testNoLibraryExists() {
		assertFalse(galaxySearch.libraryExists(INVALID_LIBRARY_NAME));
	}
	
	/**
	 * Tests checking existence of library content.
	 */
	@Test
	public void testLibraryContentExists() {
		assertTrue(galaxySearch.libraryContentExists(LIBRARY_ID_MULTIPLE_CONTENTS, ILLUMINA_FOLDER_NAME));
	}
	
	/**
	 * Tests checking non-existence of library content (no content).
	 */
	@Test
	public void testLibraryNoContentExists() {
		assertFalse(galaxySearch.libraryContentExists(LIBRARY_ID_MULTIPLE_CONTENTS, INVALID_FOLDER_NAME));
	}
	
	/**
	 * Tests checking non-existence of library content (no library).
	 */
	@Test
	public void testNoLibraryContentExists() {
		assertFalse(galaxySearch.libraryContentExists(INVALID_LIBRARY_ID, ILLUMINA_FOLDER_NAME));
	}

	/**
	 * Tests finding a user role.
	 * @throws GalaxyUserNoRoleException 
	 */
	@Test
	public void testFindUserRoleWithEmail() throws GalaxyUserNoRoleException {
		Role foundRole = galaxySearch.findUserRoleWithEmail(new GalaxyAccountEmail("role1@localhost"));
		assertNotNull(foundRole);
		assertEquals("role1@localhost", foundRole.getName());
		assertEquals("1", foundRole.getId());
	}

	/**
	 * Tests no finding a user role.
	 * @throws GalaxyUserNoRoleException 
	 */
	@Test(expected=GalaxyUserNoRoleException.class)
	public void testNoFindUserRoleWithEmail() throws GalaxyUserNoRoleException {
		galaxySearch.findUserRoleWithEmail(new GalaxyAccountEmail("invalid@localhost"));
	}
	
	/**
	 * Tests finding a user role.
	 */
	@Test
	public void testUserRoleExistsFor() {
		assertTrue(galaxySearch.userRoleExistsFor(new GalaxyAccountEmail("role1@localhost")));
	}
	
	/**
	 * Tests not finding a user role.
	 */
	@Test
	public void testNoUserRoleExistsFor() {
		assertFalse(galaxySearch.userRoleExistsFor(new GalaxyAccountEmail("invalid@localhost")));
	}

	/**
	 * Tests finding a user.
	 * @throws GalaxyUserNotFoundException 
	 */
	@Test
	public void testFindUserWithEmail() throws GalaxyUserNotFoundException {
		User foundUser = galaxySearch.findUserWithEmail(new GalaxyAccountEmail(
				"user1@localhost"));
		assertNotNull(foundUser);
		assertEquals("user1@localhost", foundUser.getEmail());
		assertEquals("1", foundUser.getId());
	}

	/**
	 * Tests not finding a user.
	 * @throws GalaxyUserNotFoundException 
	 */
	@Test(expected=GalaxyUserNotFoundException.class)
	public void testNoFindUserWithEmail() throws GalaxyUserNotFoundException {
		galaxySearch.findUserWithEmail(new GalaxyAccountEmail(
				"invalid@localhost"));
	}

	/**
	 * Tests if user exists.
	 */
	@Test
	public void testUserDoesExist() {
		assertTrue(galaxySearch.galaxyUserExists(new GalaxyAccountEmail(
				"user1@localhost")));
	}

	/**
	 * Tests if user does not exist.
	 */
	@Test
	public void testUserDoesNotExist() {
		assertFalse(galaxySearch.galaxyUserExists(new GalaxyAccountEmail(
				"invalid@localhost")));
	}

	/**
	 * Tests finding library content with library id.
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testFindLibraryContentWithId()
			throws NoGalaxyContentFoundException {
		LibraryContent validFolder = galaxySearch.findLibraryContentWithId(
				LIBRARY_ID, FOLDER_PATH);
		assertEquals("folder", validFolder.getType());
		assertEquals(FOLDER_PATH.getName(), validFolder.getName());
	}

	/**
	 * Tests getting library content as a map.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test
	public void testLibraryContentAsMap() throws NoGalaxyContentFoundException {
		Map<String, LibraryContent> validFolder = galaxySearch
				.libraryContentAsMap(LIBRARY_ID);
		assertEquals(singleLibraryContentsAsMap, validFolder);

		validFolder = galaxySearch
				.libraryContentAsMap(LIBRARY_ID_MULTIPLE_CONTENTS);
		assertEquals(multipleLibraryContentsAsMap, validFolder);
	}

	/**
	 * Tests finding no library content with id.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testLibraryContentAsMapNoContent() throws NoGalaxyContentFoundException {
		galaxySearch.libraryContentAsMap(INVALID_LIBRARY_ID);
	}

	/**
	 * Tests finding library content with invalid folder.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testFindLibraryContentWithIdInvalidFolder() throws NoGalaxyContentFoundException {
		galaxySearch.findLibraryContentWithId(LIBRARY_ID,
				INVALID_FOLDER_NAME);
	}

	/**
	 * Tests finding library content with invalid library id.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testFindLibraryContentWithIdInvalidLibraryId() throws NoGalaxyContentFoundException {
		galaxySearch.findLibraryContentWithId(INVALID_LIBRARY_ID,
				FOLDER_PATH);
	}
}
