package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.net.URL;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;

/**
 * Tests for class describing a connection to Galaxy.
 *
 */
public class GalaxyConnectorTest {
	
	private URL galaxyURL;
	private GalaxyAccountEmail accountEmail;
	private String adminApiKey;

	/**
	 * Setup objects for test.
	 * @throws MalformedURLException
	 */
	@Before
	public void setup() throws MalformedURLException {
		galaxyURL = new URL("http://localhost");
		accountEmail = new GalaxyAccountEmail("admin@localhost");
		adminApiKey = "0";		
	}
	
	/**
	 * Tests setup Galaxy with no url.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoURL() throws ConstraintViolationException,
			UploadException {
		new GalaxyConnector(null, accountEmail, adminApiKey);
	}
	
	/**
	 * Tests setup galaxy with no account email
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoAccountEmail()
			throws ConstraintViolationException, UploadException {
		new GalaxyConnector(galaxyURL, null, adminApiKey);
	}
	

	/**
	 * Tests setup of Galaxy with no API key.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = NullPointerException.class)
	public void testSetupGalaxyNoApiKey() throws ConstraintViolationException,
			UploadException {
		new GalaxyConnector(galaxyURL, accountEmail, null);
	}
}
