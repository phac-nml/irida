package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sun.jersey.api.client.ClientHandlerException;

import static org.mockito.Mockito.*;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadConnectionException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
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
	}

	/**
	 * Test connection failure to Galaxy on upload.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = UploadConnectionException.class)
	public void testUploadGalaxyConnectionFail()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader(galaxyAPI);

		when(
				galaxyAPI.uploadSamples(any(ArrayList.class),
						any(GalaxyProjectName.class),
						any(GalaxyAccountEmail.class))).thenThrow(
				new ClientHandlerException("error connecting"));

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
	 * Tests setup of Galaxy with invalid admin name.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = UploadException.class)
	public void testUploadWithInvalidAccountName()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader(galaxyAPI);

		UploaderAccountName invalidNameType = new UploaderAccountName() {
			@Override
			public String getName() {
				return "test";
			}
		};
		galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				new GalaxyProjectName("lib"), invalidNameType);
	}

	/**
	 * Tests upload invalid data library type.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = UploadException.class)
	public void testUploadWithInvalidDataLibraryObject()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader galaxyUploader = new GalaxyUploader(galaxyAPI);

		UploadProjectName invalidLibraryType = new UploadProjectName() {
			@Override
			public String getName() {
				return "test";
			}
		};
		galaxyUploader.uploadSamples(new ArrayList<UploadSample>(),
				invalidLibraryType, accountEmail);
	}
}
