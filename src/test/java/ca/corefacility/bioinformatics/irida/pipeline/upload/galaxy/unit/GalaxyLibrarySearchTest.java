package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;

/**
 * Unit tests for GalaxyLibrarySearch.
 *
 */
public class GalaxyLibrarySearchTest {
	@Mock private LibrariesClient librariesClient;

	private GalaxyLibrarySearch galaxySearch;

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
	
	private URL galaxyURL;

	private List<Library> allLibrariesList;

	/**
	 * Setup objects for test.
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 * @throws MalformedURLException 
	 */
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException, MalformedURLException {
		MockitoAnnotations.initMocks(this);
		
		setupLibraryTest();
		
		galaxyURL = new URL("http://localhost");
		galaxySearch = new GalaxyLibrarySearch(librariesClient, galaxyURL);		
	}

	/**
	 * Setup libraries.
	 */
	private void setupLibraryTest() {
		Library library = new Library(LIBRARY_NAME.getName());
		library.setId(LIBRARY_ID);

		allLibrariesList = new ArrayList<Library>();
		allLibrariesList.add(library);

		when(librariesClient.getLibraries()).thenReturn(allLibrariesList);
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
	 * @throws ExecutionManagerObjectNotFoundException
	 */
	@Test
	public void testFindLibraryById() throws ExecutionManagerObjectNotFoundException {
		Library library = galaxySearch.findById(LIBRARY_ID);
		assertNotNull(library);
		assertEquals(LIBRARY_ID, library.getId());
		assertEquals(LIBRARY_NAME.getName(), library.getName());
	}

	/**
	 * Tests not finding a library.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testNoFindLibraryById() throws ExecutionManagerObjectNotFoundException {
		galaxySearch.findById(INVALID_LIBRARY_ID);
	}

	/**
	 * Tests finding a library by name.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindLibraryByName() throws ExecutionManagerObjectNotFoundException {
		List<Library> libraries = galaxySearch
				.findByName(LIBRARY_NAME);
		List<Library> returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(allLibrariesList, returnedLibraries);

		// add new library to all libraries list with same name
		Library newLibrary = new Library(LIBRARY_NAME.getName());
		newLibrary.setId(LIBRARY_ID_2);
		allLibrariesList.add(newLibrary);

		libraries = galaxySearch.findByName(LIBRARY_NAME);
		returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(allLibrariesList, returnedLibraries);

		// add new library to all libraries with different name
		// expected list (equal to above returnedLibraries list)
		List<Library> expectedList = returnedLibraries;
		newLibrary = new Library(LIBRARY_NAME_3.getName());
		newLibrary.setId(LIBRARY_ID_3);
		allLibrariesList.add(newLibrary);

		libraries = galaxySearch.findByName(LIBRARY_NAME);
		returnedLibraries = convertLibraryToArrayList(libraries);
		assertEquals(expectedList, returnedLibraries);
	}

	/**
	 * Tests finding invalid library.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testInvalidFindLibraryByName() throws ExecutionManagerObjectNotFoundException {
		galaxySearch.findByName(INVALID_LIBRARY_NAME);
	}
	
	/**
	 * Tests checking existence of library.
	 */
	@Test
	public void testLibraryExistsByName() {
		assertTrue(galaxySearch.existsByName(LIBRARY_NAME));
	}
	
	/**
	 * Tests checking non-existence of library.
	 */
	@Test
	public void testNoLibraryExistsByName() {
		assertFalse(galaxySearch.existsByName(INVALID_LIBRARY_NAME));
	}
	
	/**
	 * Tests checking existence of library by id.
	 */
	@Test
	public void testLibraryExistsById() {
		assertTrue(galaxySearch.exists(LIBRARY_ID));
	}
	
	/**
	 * Tests checking non-existence of library by id.
	 */
	@Test
	public void testNoLibraryExistsById() {
		assertFalse(galaxySearch.exists(INVALID_LIBRARY_ID));
	}
}
