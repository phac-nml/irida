package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

/**
 * Unit tests for GalaxyUploader.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploaderTest {
	private URL galaxyURL;
	private GalaxyAccountEmail accountEmail;
	private String adminApiKey;

	@Mock
	private GalaxyAPI galaxyAPI;
	
	@Mock
	private GalaxyUploadResult uploadResult;
	
	private GalaxyUploader galaxyUploader;

	/**
	 * Setup objects for test.
	 * @throws MalformedURLException
	 */
	@Before
	public void setup() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);

		galaxyURL = new URL("http://localhost");
		accountEmail = new GalaxyAccountEmail("admin@localhost");
		adminApiKey = "0";
		
		galaxyUploader = new GalaxyUploader(galaxyAPI);
	}
	
	@Test
	public void testGalaxyUploadWorkerSuccess() {
		when(galaxyAPI.isConnected()).thenReturn(true);
		
		assertNotNull(galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				new GalaxyProjectName("lib"), accountEmail));
	}
	
	@Test(expected=RuntimeException.class)
	public void testGalaxyUploadWorkerFail() {
		when(galaxyAPI.isConnected()).thenReturn(false);
		
		galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				new GalaxyProjectName("lib"), accountEmail);
	}

	/**
	 * Tests setup Galaxy with no url.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoURL() throws ConstraintViolationException,
			UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(null, accountEmail, adminApiKey);
	}

	/**
	 * Tests setup galaxy with no account email
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoAccountEmail()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(galaxyURL, null, adminApiKey);
	}

	/**
	 * Tests setup of Galaxy with no API key.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoApiKey() throws ConstraintViolationException,
			UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(galaxyURL, accountEmail, null);
	}
	
	/**
	 * Tests case of GalaxyUploader properly connected to an instance of Galaxy.
	 */
	@Test
	public void testIsDataLocationConnectedProperly() {
		when(galaxyAPI.isConnected()).thenReturn(true);
		
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
		when(galaxyAPI.isConnected()).thenReturn(false);
		
		assertFalse(galaxyUploader.isDataLocationConnected());
	}
}
