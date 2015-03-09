package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;

import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;

/**
 * Tests for {@link GalaxyLibrariesService}.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyLibrariesServiceTest {
	
	@Mock
	private LibrariesClient librariesClient;
	
	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Tests failing when passing a zero polling time.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testZeroPollingTime() {
		new GalaxyLibrariesService(librariesClient, 0, 1);
	}
	
	/**
	 * Tests failing when passing a zero upload timeout.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testZeroUploadTimeout() {
		new GalaxyLibrariesService(librariesClient, 1, 0);
	}
	
	/**
	 * Tests failing when passing a upload timeout equal to the polling time.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEqualPollingTimeUploadTimeout() {
		new GalaxyLibrariesService(librariesClient, 1, 1);
	}
	
	/**
	 * Tests using successfull timeout values.
	 */
	@Test
	public void testSuccessfullTimeoutValues() {
		new GalaxyLibrariesService(librariesClient, 1, 2);
	}
}
