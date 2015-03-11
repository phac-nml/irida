package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

/**
 * Unit tests for GalaxyUploader.
 *
 */
public class GalaxyUploaderTest {
	private GalaxyAccountEmail accountEmail;

	@Mock
	private GalaxyConnector galaxyConnector;
	
	@Mock
	private GalaxyUploadResult uploadResult;
	
	@Mock
	private GalaxyUploaderAPI galaxyAPI;
	
	private GalaxyUploader galaxyUploader;

	/**
	 * Setup objects for test.
	 * @throws MalformedURLException
	 */
	@Before
	public void setup() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);

		accountEmail = new GalaxyAccountEmail("admin@localhost");
		
		galaxyUploader = new GalaxyUploader(galaxyConnector);
	}
	
	@Test
	public void testGalaxyUploadWorkerSuccess() throws ConstraintViolationException, GalaxyConnectException {
		when(galaxyConnector.isConnected()).thenReturn(true);
		when(galaxyConnector.createGalaxyConnection()).thenReturn(galaxyAPI);
		
		assertNotNull(galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				new GalaxyProjectName("lib"), accountEmail));
	}
	
	@Test(expected=RuntimeException.class)
	public void testGalaxyUploadWorkerFail() {
		when(galaxyConnector.isConnected()).thenReturn(false);
		
		galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				new GalaxyProjectName("lib"), accountEmail);
	}
	
	/**
	 * Tests case of GalaxyUploader properly connected to an instance of Galaxy.
	 */
	@Test
	public void testIsDataLocationConnectedProperly() {
		when(galaxyConnector.isConnected()).thenReturn(true);
		
		assertTrue(galaxyUploader.isDataLocationConnected());
	}
	
	/**
	 * Tests case of GalaxyUploader without a Galaxy connection.
	 */
	@Test
	public void testIsDataLocationConnectedNoGalaxySetup() {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		
		assertFalse(galaxyUploader.isDataLocationConnected());
	}
	
	/**
	 * Tests case of GalaxyUploader improperly connected to an instance of Galaxy.
	 */
	@Test
	public void testIsDataLocationConnectedNoGalaxyConnection() {
		when(galaxyConnector.isConnected()).thenReturn(false);
		
		assertFalse(galaxyUploader.isDataLocationConnected());
	}
}
