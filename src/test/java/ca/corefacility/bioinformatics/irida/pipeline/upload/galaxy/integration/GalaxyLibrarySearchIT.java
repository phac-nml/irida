package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy libraries.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyLibrarySearchIT {

	@Autowired
	private LocalGalaxy localGalaxy;
	
	private GalaxyLibrarySearch galaxyLibrarySearch;
	
	/**
	 * Sets up objects for GalaxyLibrarySearch.
	 */
	@Before
	public void setup() {
		galaxyLibrarySearch = new GalaxyLibrarySearch(localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient(),
								localGalaxy.getGalaxyURL());
	}
	
	/**
	 * Builds a Galaxy library with the given name.
	 * @param libraryName  The name of the library to build.
	 * @return The Library object of the library.
	 */
	private Library buildLibrary(GalaxyProjectName libraryName) {
		Library library = new Library();
		library.setName(libraryName.getName());
		
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		Library createdLibrary = librariesClient.createLibrary(library);
		
		return createdLibrary;
	}
	
	/**
	 * Tests that a Galaxy library exists.
	 */
	@Test
	public void testGalaxyLibraryExists() {
		GalaxyProjectName libraryName = new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryExists");
		buildLibrary(libraryName);
		assertTrue(galaxyLibrarySearch.existsByName(libraryName));
	}
	
	/**
	 * Tests that a Galaxy library does not exist.
	 */
	@Test
	public void testGalaxyLibraryNotExists() {
		GalaxyProjectName libraryName = new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryNotExists");
		assertFalse(galaxyLibrarySearch.existsByName(libraryName));
	}
	
	/**
	 * Tests finding a library success.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindGalaxyLibraryByNameSuccess() throws ExecutionManagerObjectNotFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByNameSuccess");
		buildLibrary(libraryName);
		List<Library> librariesFound = galaxyLibrarySearch.findByName(libraryName);
		assertEquals(1, librariesFound.size());
		assertEquals(libraryName.getName(), librariesFound.get(0).getName());
	}
	
	/**
	 * Tests finding a library fail.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testFindGalaxyLibraryByNameFail() throws ExecutionManagerObjectNotFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByNameFail");
		galaxyLibrarySearch.findByName(libraryName);
	}
	
	/**
	 * Tests finding a library by id success.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindGalaxyLibraryByIdSuccess() throws ExecutionManagerObjectNotFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByIdSuccess");
		Library library = buildLibrary(libraryName);
		Library libraryFound = galaxyLibrarySearch.findById(library.getId());
		assertNotNull(libraryFound);
		assertEquals(library.getName(), libraryFound.getName());
	}
	
	/**
	 * Tests that a Galaxy library exists.
	 */
	@Test
	public void testGalaxyLibraryExistsById() {
		GalaxyProjectName libraryName = new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryExistsById");
		Library library = buildLibrary(libraryName);
		assertTrue(galaxyLibrarySearch.exists(library.getId()));
	}
	
	/**
	 * Tests that a Galaxy library does not exist.
	 */
	@Test
	public void testLibraryNotExistsById() {
		assertFalse(galaxyLibrarySearch.exists("invalid"));
	}
	
	/**
	 * Tests finding a library by id fail.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testFindGalaxyLibraryByIdFail() throws ExecutionManagerObjectNotFoundException {
		galaxyLibrarySearch.findById("invalid_id");
	}
}
