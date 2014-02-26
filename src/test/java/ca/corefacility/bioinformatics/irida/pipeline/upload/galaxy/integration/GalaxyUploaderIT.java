package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

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
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.LocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadConnectionException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for the GalaxyUploader with a running instance of Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, LocalGalaxyConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyUploaderIT {
	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private GalaxyUploader galaxyUploader;

	private List<Path> dataFilesSingle;

	@Before
	public void setup() throws URISyntaxException {
		setupDataFiles();
	}

	private void setupDataFiles() throws URISyntaxException {
		Path dataFile1 = Paths.get(GalaxyUploaderIT.class.getResource(
				"testData1.fastq").toURI());

		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);
	}

	/**
	 * Test the case of no connection to a running instance of Galaxy.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = UploadException.class)
	public void testNoGalaxyConnectionUpload()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader unconnectedGalaxyUploader = new GalaxyUploader();

		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploader_testNoGalaxyConnection");

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		samples.add(galaxySample1);

		unconnectedGalaxyUploader.uploadSamples(samples, libraryName,
				localGalaxy.getAdminName());
	}

	/**
	 * Test the case of setting up the Galaxy API with a an email that does not exist in Galaxy.
	 * @throws ConstraintViolationException
	 * @throws GalaxyConnectException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testSetupNonExistentEmail()
			throws ConstraintViolationException, GalaxyConnectException {
		GalaxyUploader newGalaxyUploder = new GalaxyUploader();
		newGalaxyUploder.setupGalaxyAPI(localGalaxy.getGalaxyURL(),
				localGalaxy.getNonExistentGalaxyAdminName(),
				localGalaxy.getAdminAPIKey());
	}

	/**
	 * Tests the case of Galaxy being shutdown while the archive is running.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 * @throws MalformedURLException
	 */
	@Test(expected = UploadConnectionException.class)
	public void testGalaxyShutdownRandomly()
			throws ConstraintViolationException, UploadException,
			MalformedURLException {
		// I need to bring up a new version of Galaxy so I can connect to it,
		// then shut it down
		// without affecting other tests
		LocalGalaxyConfig galaxyConfig = new LocalGalaxyConfig();
		LocalGalaxy newLocalGalaxy = galaxyConfig.localGalaxy();

		// connect to running Galaxy first, so it passes all initial checks
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(newLocalGalaxy.getGalaxyURL(),
				newLocalGalaxy.getAdminName(), newLocalGalaxy.getAdminAPIKey());

		// I should be able to upload a file initially
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploader_testGalaxyShutdownRandomly");

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		samples.add(galaxySample1);

		assertNotNull(galaxyUploader.uploadSamples(samples, libraryName,
				newLocalGalaxy.getAdminName()));

		// shutdown running Galaxy
		newLocalGalaxy.shutdownGalaxy();

		// try uploading again, this should fail and throw an exception
		galaxyUploader.uploadSamples(samples, libraryName,
				newLocalGalaxy.getAdminName());
	}

	/**
	 * Tests out the GalaxyUploader.isConnected() method.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testCheckGalaxyConnection()
			throws ConstraintViolationException, UploadException {
		GalaxyUploader unconnectedGalaxyUploader = new GalaxyUploader();

		assertTrue(galaxyUploader.isConnected());
		assertFalse(unconnectedGalaxyUploader.isConnected());
	}

	/**
	 * Tests uploading some samples to Galaxy.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSamples() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"Galaxy_Uploader testUploadSamples");
		String localGalaxyURL = localGalaxy
				.getGalaxyURL()
				.toString()
				.substring(0,
						localGalaxy.getGalaxyURL().toString().length() - 1); // remove trailing '/'

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		samples.add(galaxySample1);

		UploadResult actualUploadResult = galaxyUploader.uploadSamples(samples,
				libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName, actualUploadResult.getLocationName());
		assertEquals(new URL(localGalaxyURL + "/library"),
				actualUploadResult.getDataLocation());
	}

	/**
	 * Tests uploading a sample with an invalid user name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidUserName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_user");
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyUploader.uploadSamples(samples, libraryName, userEmail);
	}

	/**
	 * Tests uploading a sample with an invalid name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidSampleName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleInvalidSampleName");
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"<invalidSample>"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyUploader.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name());
	}

	/**
	 * Tests uploading a sample with an invalid library name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidLibraryName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName("<invalidLibrary>");
		GalaxySample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyUploader.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name());
	}
}
