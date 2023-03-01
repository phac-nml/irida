package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link GalaxyLibrariesService}.
 */
public class GalaxyLibrariesServiceTest {

	@Mock
	private LibrariesClient librariesClient;

	private static final String LIBRARY_ID = "1";

	private Library testLibrary;

	private IridaFileStorageUtility iridaFileStorageUtility;

	/**
	 * Setup for tests.
	 */
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
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
	@Test
	public void testZeroPollingTime() {
		assertThrows(IllegalArgumentException.class, () -> {
			new GalaxyLibrariesService(librariesClient, 0, 1, 1);
		});
	}

	/**
	 * Tests failing when passing a zero upload timeout.
	 */
	@Test
	public void testZeroUploadTimeout() {
		assertThrows(IllegalArgumentException.class, () -> {
			new GalaxyLibrariesService(librariesClient, 1, 0, 1);
		});
	}

	/**
	 * Tests failing when passing a upload timeout equal to the polling time.
	 */
	@Test
	public void testEqualPollingTimeUploadTimeout() {
		assertThrows(IllegalArgumentException.class, () -> {
			new GalaxyLibrariesService(librariesClient, 1, 1, 1);
		});
	}

	/**
	 * Tests using successfull timeout values.
	 */
	@Test
	public void testSuccessfullTimeoutValues() {
		new GalaxyLibrariesService(librariesClient, 1, 2, 1);
	}

	/**
	 * Tests using unsuccessful thread value.
	 */
	@Test
	public void testFailThreadValue() {
		assertThrows(IllegalArgumentException.class, () -> {
			new GalaxyLibrariesService(librariesClient, 1, 2, 0);
		});
	}

	/**
	 * Tests create empty library.
	 * @throws CreateLibraryException
	 */
	@Test
	public void testBuildEmptyLibrary() throws CreateLibraryException {
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(
				testLibrary);

		Library library = new GalaxyLibrariesService(librariesClient, 1, 2, 1).buildEmptyLibrary(new GalaxyProjectName(
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
	@Test
	public void testBuildEmptyLibraryFail() throws CreateLibraryException {
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(null);

		assertThrows(CreateLibraryException.class, () -> {
			new GalaxyLibrariesService(librariesClient, 1, 2, 1).buildEmptyLibrary(new GalaxyProjectName("test"));
		});
	}
}
