package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy libraries.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
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
	 * Builds a Galaxy library with the given name and content.
	 * @param libraryName  The name of the library to build.
	 * @param folderName  The folder name in the library to build.
	 * @return The combined Library and LibraryContent objects of the library.
	 */
	private Library buildLibrary(GalaxyProjectName libraryName, GalaxyFolderName folderName) {
		Library library = new Library();
		library.setName(libraryName.getName());
		
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		Library createdLibrary = librariesClient.createLibrary(library);
		assertNotNull(createdLibrary);
		
		LibraryContent rootContent = librariesClient.getRootFolder(createdLibrary.getId());
		
		LibraryFolder folder = new LibraryFolder();
		folder.setFolderId(rootContent.getId());
		folder.setName(folderName.getName());
		librariesClient.createFolder(createdLibrary.getId(), folder);
		
		List<LibraryContent> contents = librariesClient.getLibraryContents(createdLibrary.getId());
		assertEquals(2, contents.size());
		
		return createdLibrary;
	}
	
	/**
	 * Converts GalaxyFolderName (no leading '/') to a path (leading '/')
	 * @param name  The name to convert.
	 * @return  The same name, but with a leading '/'.
	 */
	private GalaxyFolderPath folderNameToPath(GalaxyFolderName name) {
		return new GalaxyFolderPath("/" + name.getName());
	}
	
	/**
	 * Tests that a Galaxy library exists.
	 */
	@Test
	public void testGalaxyLibraryExists() {
		GalaxyProjectName libraryName = new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryExists");
		buildLibrary(libraryName);
		assertTrue(galaxyLibrarySearch.libraryExists(libraryName));
	}
	
	/**
	 * Tests that a Galaxy library does not exist.
	 */
	@Test
	public void testGalaxyLibraryNotExists() {
		GalaxyProjectName libraryName = new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryNotExists");
		assertFalse(galaxyLibrarySearch.libraryExists(libraryName));
	}
	
	/**
	 * Tests finding a library success.
	 * @throws NoLibraryFoundException
	 */
	@Test
	public void testFindGalaxyLibraryByNameSuccess() throws NoLibraryFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByNameSuccess");
		buildLibrary(libraryName);
		List<Library> librariesFound = galaxyLibrarySearch.findLibraryWithName(libraryName);
		assertEquals(1, librariesFound.size());
		assertEquals(libraryName.getName(), librariesFound.get(0).getName());
	}
	
	/**
	 * Tests finding a library fail.
	 * @throws NoLibraryFoundException
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testFindGalaxyLibraryByNameFail() throws NoLibraryFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByNameFail");
		galaxyLibrarySearch.findLibraryWithName(libraryName);
	}
	
	/**
	 * Tests finding a library by id success.
	 * @throws NoLibraryFoundException
	 */
	@Test
	public void testFindGalaxyLibraryByIdSuccess() throws NoLibraryFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testFindGalaxyLibraryByIdSuccess");
		Library library = buildLibrary(libraryName);
		Library libraryFound = galaxyLibrarySearch.findLibraryWithId(library.getId());
		assertNotNull(libraryFound);
		assertEquals(library.getName(), libraryFound.getName());
	}
	
	
	/**
	 * Tests finding a library by id fail.
	 * @throws NoLibraryFoundException
	 */
	@Test(expected=NoLibraryFoundException.class)
	public void testFindGalaxyLibraryByIdFail() throws NoLibraryFoundException {
		galaxyLibrarySearch.findLibraryWithId("invalid_id");
	}
	
	/**
	 * Tests library content exists success.
	 * @throws NoLibraryFoundException
	 */
	@Test
	public void testGalaxyLibraryContentExists() {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryContentExists");
		GalaxyFolderName folderName = new GalaxyFolderName("folder");
		Library createdLibrary = buildLibrary(libraryName, folderName);
		assertTrue(galaxyLibrarySearch.libraryContentExists(createdLibrary.getId(),
				folderNameToPath(folderName)));
	}
	
	/**
	 * Tests library content not exists.
	 * @throws NoLibraryFoundException
	 */
	@Test
	public void testGalaxyLibraryContentNotExists() {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryContentNotExists");
		GalaxyFolderName folderName = new GalaxyFolderName("folder");
		Library createdLibrary = buildLibrary(libraryName, folderName);
		assertFalse(galaxyLibrarySearch.libraryContentExists(createdLibrary.getId(),
				new GalaxyFolderPath("/invalid_folder")));
	}
	
	/**
	 * Tests find library content success.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test
	public void testGalaxyFindLibraryContentSuccess() throws NoGalaxyContentFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyFindLibraryContentSuccess");
		GalaxyFolderName folderName = new GalaxyFolderName("folder");
		Library createdLibrary = buildLibrary(libraryName, folderName);
		LibraryContent foundContent =galaxyLibrarySearch.findLibraryContentWithId(createdLibrary.getId(),
				folderNameToPath(folderName));
		assertNotNull(foundContent);
		assertEquals(folderNameToPath(folderName).getName(), foundContent.getName());	
	}
	
	/**
	 * Tests find library content fail.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testGalaxyFindLibraryContentFail() throws NoGalaxyContentFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyFindLibraryContentFail");
		GalaxyFolderName folderName = new GalaxyFolderName("folder");
		Library createdLibrary = buildLibrary(libraryName, folderName);
		galaxyLibrarySearch.findLibraryContentWithId(createdLibrary.getId(),
				new GalaxyFolderPath("/invalid_name"));
	}
	
	/**
	 * Tests getting library content as a map success.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test
	public void testGalaxyLibraryContentAsMapSuccess() throws NoGalaxyContentFoundException {
		GalaxyProjectName libraryName =
				new GalaxyProjectName("GalaxyLibrarySearchIT_testGalaxyLibraryContentAsMapSuccess");
		GalaxyFolderName folderName = new GalaxyFolderName("folder");
		Library createdLibrary = buildLibrary(libraryName, folderName);
		Map<String, LibraryContent> foundContent = 
				galaxyLibrarySearch.libraryContentAsMap(createdLibrary.getId());
		assertNotNull(foundContent);
		assertEquals(2, foundContent.size());
		assertTrue(foundContent.containsKey(folderNameToPath(folderName).getName()));
		assertTrue(foundContent.containsKey("/"));
	}
	
	/**
	 * Tests getting library content as a map fail.
	 * @throws NoGalaxyContentFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testGalaxyLibraryContentAsMapFail() throws NoGalaxyContentFoundException {
		galaxyLibrarySearch.libraryContentAsMap("1");
	}
}
