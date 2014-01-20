package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyLibrary;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySearch;

public class GalaxyLibraryTest
{
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private GalaxySearch galaxySearch;
	@Mock private LibrariesClient librariesClient;
	@Mock private ClientResponse okayResponse;
	@Mock private ClientResponse invalidResponse;
	
	private final static String LIBRARY_ID = "1";
	private final static String USER_EMAIL = "user@localhost";
	private final static String ADMIN_EMAIL = "admin@localhost";
	private final static String INVALID_EMAIL = "invalid@localhost";
	private final static String ROOT_FOLDER_ID = "10";
	
	private Library testLibrary;
	private GalaxyLibrary galaxyLibrary;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{		
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);

		setupLibrariesTest();
		setupPermissionsTest();
		setupFoldersTest();
		
		galaxyLibrary = new GalaxyLibrary(galaxyInstance, galaxySearch);
	}
	
	private void setupLibrariesTest()
	{
		testLibrary = new Library();
		testLibrary.setName("test");
		testLibrary.setId(LIBRARY_ID);
		
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
	}
	
	private void setupFoldersTest()
	{
		LibraryContent rootFolder = new LibraryContent();
		rootFolder.setName("/");
		rootFolder.setId(ROOT_FOLDER_ID);
		
		when(librariesClient.getRootFolder(LIBRARY_ID)).thenReturn(rootFolder);
	}
	
	private void setupPermissionsTest()
	{
		Role userRole = new Role();
		userRole.setName(USER_EMAIL);
		
		Role adminRole = new Role();
		adminRole.setName(ADMIN_EMAIL);
		
		when(galaxySearch.findUserRoleWithEmail(USER_EMAIL)).thenReturn(userRole);
		when(galaxySearch.findUserRoleWithEmail(ADMIN_EMAIL)).thenReturn(adminRole);
	}
	
	@Test
	public void testCreateLibraryFolderRoot() throws CreateLibraryException
	{
		LibraryFolder folder = new LibraryFolder();
		folder.setName("folder_name");
		folder.setId("1");
		
		when(librariesClient.createFolder(eq(LIBRARY_ID), any(LibraryFolder.class))).thenReturn(folder);
		
		LibraryFolder newFolder = galaxyLibrary.createLibraryFolder(testLibrary, "new_folder");
		assertNotNull(newFolder);
		assertEquals("folder_name", newFolder.getName());
		assertEquals("1", newFolder.getId());
	}
	
	@Test
	public void testCreateLibraryFolderNoRoot() throws CreateLibraryException
	{
		when(librariesClient.getRootFolder(LIBRARY_ID)).thenReturn(null);
		
		LibraryFolder folder = galaxyLibrary.createLibraryFolder(testLibrary, "new_folder");
		assertNull(folder);
	}
	
	@Test
	public void testCreateLibraryFolder() throws CreateLibraryException
	{
		LibraryFolder folder = new LibraryFolder();
		folder.setName("folder_name");
		folder.setId("1");
		
		when(librariesClient.createFolder(eq(LIBRARY_ID), any(LibraryFolder.class))).thenReturn(folder);
		
		LibraryFolder newFolder = galaxyLibrary.createLibraryFolder(testLibrary, folder, "new_folder");
		assertNotNull(newFolder);
		assertEquals("folder_name", newFolder.getName());
		assertEquals("1", newFolder.getId());
	}
	
	@Test
	public void testChangeLibraryOwner() throws CreateLibraryException
	{
		when(librariesClient.setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class))).
			thenReturn(okayResponse);
		
		Library library = galaxyLibrary.changeLibraryOwner(testLibrary, USER_EMAIL, ADMIN_EMAIL);
		assertNotNull(library);
		assertEquals(testLibrary.getName(),library.getName());
		assertEquals(testLibrary.getId(),library.getId());
		verify(librariesClient).setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class));
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testChangeLibraryOwnerInvalidUser() throws CreateLibraryException
	{
		when(librariesClient.setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class))).
			thenReturn(okayResponse);
		
		galaxyLibrary.changeLibraryOwner(testLibrary, INVALID_EMAIL, ADMIN_EMAIL);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testChangeLibraryOwnerInvalidAdmin() throws CreateLibraryException
	{
		when(librariesClient.setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class))).
			thenReturn(okayResponse);
		
		galaxyLibrary.changeLibraryOwner(testLibrary, USER_EMAIL, INVALID_EMAIL);
	}
	
	@Test
	public void testChangeLibraryOwnerInvalidResponse() throws CreateLibraryException
	{
		when(librariesClient.setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class))).
			thenReturn(invalidResponse);
		
		Library library = galaxyLibrary.changeLibraryOwner(testLibrary, USER_EMAIL, ADMIN_EMAIL);
		verify(librariesClient).setLibraryPermissions(eq(LIBRARY_ID), any(LibraryPermissions.class));
		assertNull(library);
	}
	
	@Test
	public void testBuildEmptyLibrary()
	{		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(testLibrary);
		
		Library library = galaxyLibrary.buildEmptyLibrary("test");
		
		assertNotNull(library);
		assertEquals("test", library.getName());
		assertEquals(LIBRARY_ID, library.getId());
	}
	
	@Test(expected=RuntimeException.class)
	public void testFailBuildEmptyLibrary()
	{	
		when(librariesClient.createLibrary(any(Library.class))).thenThrow(new RuntimeException("error creating library"));
		
		galaxyLibrary.buildEmptyLibrary("test");
	}
}
