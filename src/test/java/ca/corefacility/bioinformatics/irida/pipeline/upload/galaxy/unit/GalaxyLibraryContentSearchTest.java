package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.LibraryContentId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryContentSearch;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.google.common.collect.Lists;

public class GalaxyLibraryContentSearchTest {
	
	private static final String LIBRARY_ID = "1";
	private static final String INVALID_LIBRARY_ID = "0";
	private static final GalaxyProjectName LIBRARY_NAME = new GalaxyProjectName(
			"Test");
	
	private static final String LIBRARY_ID_MULTIPLE_CONTENTS = "10";
	private static final GalaxyFolderPath FOLDER_PATH = new GalaxyFolderPath(
			"test_folder");
	private static final GalaxyFolderPath ILLUMINA_FOLDER_NAME = new GalaxyFolderPath(
			"test_folder/illumina");
	private static final GalaxyFolderPath INVALID_FOLDER_NAME = new GalaxyFolderPath(
			"invalid_folder");
	
	private static final LibraryContentId CONTENT_FOLDER_ID =
			new LibraryContentId(LIBRARY_ID, FOLDER_PATH);
	private static final LibraryContentId INVALID_LIBRARY_CONTENT_FOLDER_ID =
			new LibraryContentId(INVALID_LIBRARY_ID, FOLDER_PATH);
	private static final LibraryContentId LIBRARY_INVALID_FOLDER_ID =
			new LibraryContentId(LIBRARY_ID, INVALID_FOLDER_NAME);
	
	@Mock private LibrariesClient librariesClient;
	
	private GalaxyLibraryContentSearch galaxyLibraryContentSearch;
	
	private URL galaxyURL;

	private List<Library> allLibrariesList;
	private Map<String, List<LibraryContent>> singleLibraryContentsAsMap;
	private Map<String, List<LibraryContent>> multipleLibraryContentsAsMap;
	
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
		setupLibraryContentTest();
		
		galaxyURL = new URL("http://localhost");
		galaxyLibraryContentSearch = new GalaxyLibraryContentSearch(librariesClient, galaxyURL);		
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

		singleLibraryContentsAsMap = new HashMap<>();
		singleLibraryContentsAsMap.put(FOLDER_PATH.getName(), Lists.newArrayList(validFolder));

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

		multipleLibraryContentsAsMap = new HashMap<>();
		multipleLibraryContentsAsMap.put(FOLDER_PATH.getName(), Lists.newArrayList(validFolder1));
		multipleLibraryContentsAsMap.put(ILLUMINA_FOLDER_NAME.getName(),
				Lists.newArrayList(validFolder2));

		when(librariesClient.getLibraryContents(INVALID_LIBRARY_ID))
				.thenReturn(null);
	}
	
	/**
	 * Tests finding library contents by id (exists).
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testfindByIdExists() throws ExecutionManagerObjectNotFoundException {
		assertNotNull(galaxyLibraryContentSearch.findById(CONTENT_FOLDER_ID));
	}
	
	/**
	 * Tests finding library contents by id (invalid library, valid folder).
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testfindByIdNotExistsLibrary() throws ExecutionManagerObjectNotFoundException {
		galaxyLibraryContentSearch.findById(INVALID_LIBRARY_CONTENT_FOLDER_ID);
	}
	
	/**
	 * Tests finding library contents by id (valid library, invalid folder)).
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testFindByIdNotExistsFolder() throws ExecutionManagerObjectNotFoundException {
		galaxyLibraryContentSearch.findById(LIBRARY_INVALID_FOLDER_ID);
	}
	
	/**
	 * Tests exists library contents by id (exists).
	 */
	@Test
	public void testLibraryContentFindByIdExists() {
		assertTrue(galaxyLibraryContentSearch.exists(CONTENT_FOLDER_ID));
	}
	
	/**
	 * Tests exists library contents by id (valid library, invalid folder).
	 */
	@Test
	public void testLibraryContentFindByIdNotExistsLibrary() {
		assertFalse(galaxyLibraryContentSearch.exists(INVALID_LIBRARY_CONTENT_FOLDER_ID));
	}
	
	/**
	 * Tests exists library contents by id (invalid library, valid folder).
	 */
	@Test
	public void testLibraryContentFindByIdNotExistsFolder() {
		assertFalse(galaxyLibraryContentSearch.exists(LIBRARY_INVALID_FOLDER_ID));
	}

	/**
	 * Tests getting library content as a map.
	 * @throws ExecutionManagerObjectNotFoundException
	 */
	@Test
	public void testLibraryContentAsMap() throws ExecutionManagerObjectNotFoundException {
		Map<String, List<LibraryContent>> validFolder = galaxyLibraryContentSearch
				.libraryContentAsMap(LIBRARY_ID);
		assertEquals(singleLibraryContentsAsMap, validFolder);

		validFolder = galaxyLibraryContentSearch
				.libraryContentAsMap(LIBRARY_ID_MULTIPLE_CONTENTS);
		assertEquals(multipleLibraryContentsAsMap, validFolder);
	}

	/**
	 * Tests finding no library content with id.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoGalaxyContentFoundException.class)
	public void testLibraryContentAsMapNoContent() throws ExecutionManagerObjectNotFoundException {
		galaxyLibraryContentSearch.libraryContentAsMap(INVALID_LIBRARY_ID);
	}
}
