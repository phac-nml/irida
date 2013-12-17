package ca.corefacility.bioinformatics.irida.pipeline.data.impl.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxySearch;

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
	private static final String INVALID_LIBRARY_ID = "2";
	private static final String LIBRARY_NAME = "Test";
	private static final String FOLDER_NAME = "test_folder";
	private static final String INVALID_FOLDER_NAME = "invalid_folder";
	
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
		
		Library library = new Library(LIBRARY_NAME);
		library.setId(LIBRARY_ID);
		
		List<Library> librariesList = new ArrayList<Library>();
		librariesList.add(library);
		
		when(librariesClient.getLibraries()).thenReturn(librariesList);
	}
	
	private void setupLibraryContentTest()
	{
		List<LibraryContent> libraryContents = new ArrayList<LibraryContent>();
		LibraryContent validFolder = new LibraryContent();
		validFolder.setName(FOLDER_NAME);
		validFolder.setType("folder");
		libraryContents.add(validFolder);
		
		when(librariesClient.getLibraryContents(LIBRARY_ID)).thenReturn(libraryContents);
	}
	
	@Test
	public void testFindLibrary()
	{		
		Library library = galaxySearch.findLibraryWithId(LIBRARY_ID);
		assertNotNull(library);
		assertEquals(LIBRARY_ID, library.getId());
		assertEquals(LIBRARY_NAME, library.getName());
	}
	
	@Test
	public void testNoFindLibrary()
	{		
		Library library = galaxySearch.findLibraryWithId(INVALID_LIBRARY_ID);
		assertNull(library);
	}
	
	@Test
	public void testFindUserRoleWithEmail()
	{		
		Role foundRole = galaxySearch.findUserRoleWithEmail("role1@localhost");
		assertNotNull(foundRole);
		assertEquals("role1@localhost", foundRole.getName());
		assertEquals("1", foundRole.getId());
	}
	
	@Test
	public void testNoFindUserRoleWithEmail()
	{		
		Role foundRole = galaxySearch.findUserRoleWithEmail("invalid@localhost");
		assertNull(foundRole);
	}	
	
	@Test
	public void testFindUserWithEmail()
	{		
		User foundUser = galaxySearch.findUserWithEmail("user1@localhost");
		assertNotNull(foundUser);
		assertEquals("user1@localhost", foundUser.getEmail());
		assertEquals("1", foundUser.getId());
	}
	
	@Test
	public void testNoFindUserWithEmail()
	{		
		User foundUser = galaxySearch.findUserWithEmail("invalid@localhost");
		assertNull(foundUser);
	}
	
	@Test
	public void testCheckValidAdminEmailAPIKey()
	{		
		assertTrue(galaxySearch.checkValidAdminEmailAPIKey("user1@localhost", null));
		assertFalse(galaxySearch.checkValidAdminEmailAPIKey("invalid@localhost", null));
	}
	
	@Test
	public void testFindLibraryContentWithId()
	{
		LibraryContent validFolder = galaxySearch.findLibraryContentWithId(LIBRARY_ID, FOLDER_NAME);
		assertEquals("folder", validFolder.getType());
		assertEquals(FOLDER_NAME, validFolder.getName());
	}
	
	@Test
	public void testFindLibraryContentWithIdInvalidFolder()
	{
		assertNull(galaxySearch.findLibraryContentWithId(LIBRARY_ID, INVALID_FOLDER_NAME));
	}
	
	@Test
	public void testFindLibraryContentWithIdInvalidLibraryId()
	{
		assertNull(galaxySearch.findLibraryContentWithId(INVALID_LIBRARY_ID, FOLDER_NAME));
	}
}
