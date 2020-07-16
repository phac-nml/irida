package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Tests for {@link GalaxyLibrariesService}.
 *
 */
public class GalaxyLibrariesServiceTest {
	
	@Mock
	private LibrariesClient librariesClient;
	
	private final static String LIBRARY_ID = "1";
	
	private Library testLibrary;

	private IridaFileStorageUtility iridaFileStorageUtility;

	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		setupLibrariesTest();
	}
	
	/**
	 * Setup libraries for test.
	 */
	private void setupLibrariesTest() {
		testLibrary = new Library();
		testLibrary.setName("test");
		testLibrary.setId(LIBRARY_ID);
	}


	/**
	 * Tests failing when passing a zero polling time.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testZeroPollingTime() {
		new GalaxyLibrariesService(librariesClient, 0, 1, 1, iridaFileStorageUtility);
	}
	
	/**
	 * Tests failing when passing a zero upload timeout.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testZeroUploadTimeout() {
		new GalaxyLibrariesService(librariesClient, 1, 0, 1, iridaFileStorageUtility);
	}
	
	/**
	 * Tests failing when passing a upload timeout equal to the polling time.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEqualPollingTimeUploadTimeout() {
		new GalaxyLibrariesService(librariesClient, 1, 1, 1, iridaFileStorageUtility);
	}
	
	/**
	 * Tests using successfull timeout values.
	 */
	@Test
	public void testSuccessfullTimeoutValues() {
		new GalaxyLibrariesService(librariesClient, 1, 2, 1, iridaFileStorageUtility);
	}
	
	/**
	 * Tests using unsuccessful thread value.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFailThreadValue() {
		new GalaxyLibrariesService(librariesClient, 1, 2, 0, iridaFileStorageUtility);
	}
	
	/**
	 * Tests create empty library.
	 * @throws CreateLibraryException
	 */
	@Test
	public void testBuildEmptyLibrary() throws CreateLibraryException {
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(
				testLibrary);

		Library library = new GalaxyLibrariesService(librariesClient, 1, 2, 1, iridaFileStorageUtility).buildEmptyLibrary(new GalaxyProjectName(
				"test"));

		assertNotNull(library);
		assertEquals("test", library.getName());
		assertEquals(LIBRARY_ID, library.getId());
	}

	/**
	 * Tests create empty library.
	 * 
	 * @throws CreateLibraryException
	 */
	@Test(expected = CreateLibraryException.class)
	public void testBuildEmptyLibraryFail() throws CreateLibraryException {
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(null);

		new GalaxyLibrariesService(librariesClient, 1, 2, 1, iridaFileStorageUtility).buildEmptyLibrary(new GalaxyProjectName("test"));
	}
}
