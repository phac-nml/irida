package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl.unit;

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

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl.GalaxySearch;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;

public class GalaxySearchTest
{
	@Mock private RolesClient rolesClient;
	@Mock private UsersClient usersClient;
	@Mock private LibrariesClient librariesClient;
	@Mock private GalaxyInstance galaxyInstance;
	
	private GalaxySearch galaxySearch;
	
	private static final String LIBRARY_ID = "1";
	private static final String LIBRARY_ID_2 = "2";
	private static final String LIBRARY_ID_3 = "3";
	private static final String INVALID_LIBRARY_ID = "0";
	private static final GalaxyObjectName LIBRARY_NAME = new GalaxyObjectName("Test");
	private static final GalaxyObjectName INVALID_LIBRARY_NAME = new GalaxyObjectName("InvalidTest");
	private static final UploadObjectName LIBRARY_NAME_3 = new GalaxyObjectName("Test3");
	private static final String LIBRARY_ID_MULTIPLE_CONTENTS = "10";
	private static final GalaxyFolderPath FOLDER_PATH = new GalaxyFolderPath("test_folder");
	private static final GalaxyFolderPath ILLUMINA_FOLDER_NAME = new GalaxyFolderPath("test_folder/illumina");
	private static final GalaxyFolderPath INVALID_FOLDER_NAME = new GalaxyFolderPath("invalid_folder");
	private static final String ADMIN_API_KEY = "0";
	
	private List<Library> allLibrariesList;
	private Map<String, LibraryContent> singleLibraryContentsAsMap;
	private Map<String, LibraryContent> multipleLibraryContentsAsMap;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{		
		MockitoAnnotations.initMocks(this);
		
		setupRolesTest();
		setupUserTest();
		setupLibraryTest();
		setupLibraryContentTest();

		galaxySearch = new GalaxySearch(galaxyInstance);
	}
	
	private void setupRolesTest()
	{
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
	
	private void setupUserTest()
	{
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
	
	private void setupLibraryTest()
	{		
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		
		Library library = new Library(LIBRARY_NAME.getName());
		library.setId(LIBRARY_ID);
		
		allLibrariesList = new ArrayList<Library>();
		allLibrariesList.add(library);
		
		when(librariesClient.getLibraries()).thenReturn(allLibrariesList);
	}
	
	private void setupLibraryContentTest()
	{
		List<LibraryContent> singleLibraryContents = new ArrayList<LibraryContent>();
		LibraryContent validFolder = new LibraryContent();
		validFolder.setName(FOLDER_PATH.getName());
		validFolder.setType("folder");
		singleLibraryContents.add(validFolder);
		
		when(librariesClient.getLibraryContents(LIBRARY_ID)).thenReturn(singleLibraryContents);
		
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
		
		when(librariesClient.getLibraryContents(LIBRARY_ID_MULTIPLE_CONTENTS)).thenReturn(multipleLibraryContents);
		
		multipleLibraryContentsAsMap = new HashMap<String, LibraryContent>();
		multipleLibraryContentsAsMap.put(FOLDER_PATH.getName(), validFolder1);
		multipleLibraryContentsAsMap.put(ILLUMINA_FOLDER_NAME.getName(), validFolder2);
		
		when(librariesClient.getLibraryContents(INVALID_LIBRARY_ID)).thenReturn(null);
	}
	
	private ArrayList<Library> convertLibraryToArrayList(List<Library> libraries)
	{
		ArrayList<Library> newLibraries = new ArrayList<Library>();
		
		for (Library library : libraries)
		{
			newLibraries.add(library);
		}
		
		return newLibraries;
	}
	
	@Test
	public void testFindLibrary()
	{		
		Library library = galaxySearch.findLibraryWithId(LIBRARY_ID);
		assertNotNull(library);
		assertEquals(LIBRARY_ID, library.getId());
		assertEquals(LIBRARY_NAME.getName(), library.getName());
	}
	
	@Test
	public void testNoFindLibrary()
	{		
		Library library = galaxySearch.findLibraryWithId(INVALID_LIBRARY_ID);
		assertNull(library);
	}
	
	@Test
	public void testFindLibraryByName()
	{
		List<Library> libraries = galaxySearch.findLibraryWithName(LIBRARY_NAME);
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
	
	@Test
	public void testInvalidFindLibraryByName()
	{
		List<Library> libraries = galaxySearch.findLibraryWithName(INVALID_LIBRARY_NAME);
		assertNotNull(libraries);
		assertEquals(0, libraries.size());
	}
	
	@Test
	public void testFindUserRoleWithEmail()
	{		
		Role foundRole = galaxySearch.findUserRoleWithEmail(new GalaxyAccountEmail("role1@localhost"));
		assertNotNull(foundRole);
		assertEquals("role1@localhost", foundRole.getName());
		assertEquals("1", foundRole.getId());
	}
	
	@Test
	public void testNoFindUserRoleWithEmail()
	{		
		Role foundRole = galaxySearch.findUserRoleWithEmail(new GalaxyAccountEmail("invalid@localhost"));
		assertNull(foundRole);
	}	
	
	@Test
	public void testFindUserWithEmail()
	{		
		User foundUser = galaxySearch.findUserWithEmail(new GalaxyAccountEmail("user1@localhost"));
		assertNotNull(foundUser);
		assertEquals("user1@localhost", foundUser.getEmail());
		assertEquals("1", foundUser.getId());
	}
	
	@Test
	public void testNoFindUserWithEmail()
	{		
		User foundUser = galaxySearch.findUserWithEmail(new GalaxyAccountEmail("invalid@localhost"));
		assertNull(foundUser);
	}
	
	@Test
	public void testUserDoesExist()
	{		
		assertTrue(galaxySearch.galaxyUserExists(new GalaxyAccountEmail("user1@localhost")));
	}
	
	@Test
	public void testUserDoesNotExist()
	{		
		assertFalse(galaxySearch.galaxyUserExists(new GalaxyAccountEmail("invalid@localhost")));
	}
	
	@Test
	public void testCheckValidAdminEmailAPIKey() throws GalaxyConnectException
	{		
		assertTrue(galaxySearch.checkValidAdminEmailAPIKey(new GalaxyAccountEmail("user1@localhost"), ADMIN_API_KEY));
		assertFalse(galaxySearch.checkValidAdminEmailAPIKey(new GalaxyAccountEmail("invalid@localhost"), ADMIN_API_KEY));
	}
	
	@Test
	public void testFindLibraryContentWithId()
	{
		LibraryContent validFolder = galaxySearch.findLibraryContentWithId(LIBRARY_ID, FOLDER_PATH);
		assertEquals("folder", validFolder.getType());
		assertEquals(FOLDER_PATH.getName(), validFolder.getName());
	}
	
	@Test
	public void testLibraryContentAsMap()
	{
		Map<String, LibraryContent> validFolder = galaxySearch.libraryContentAsMap(LIBRARY_ID);
		assertEquals(singleLibraryContentsAsMap, validFolder);
		
		validFolder = galaxySearch.libraryContentAsMap(LIBRARY_ID_MULTIPLE_CONTENTS);
		assertEquals(multipleLibraryContentsAsMap, validFolder);
	}
	
	@Test
	public void testLibraryContentAsMapNoContent()
	{
		Map<String, LibraryContent> validFolder = galaxySearch.libraryContentAsMap(INVALID_LIBRARY_ID);
		assertNull(validFolder);
	}
	
	@Test
	public void testFindLibraryContentWithIdInvalidFolder()
	{
		assertNull(galaxySearch.findLibraryContentWithId(LIBRARY_ID, INVALID_FOLDER_NAME));
	}
	
	@Test
	public void testFindLibraryContentWithIdInvalidLibraryId()
	{
		assertNull(galaxySearch.findLibraryContentWithId(INVALID_LIBRARY_ID, FOLDER_PATH));
	}
}
