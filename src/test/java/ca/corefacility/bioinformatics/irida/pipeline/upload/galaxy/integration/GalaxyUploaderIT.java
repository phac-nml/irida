package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assume;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for the GalaxyUploader with a running instance of Galaxy.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyUploaderIT {
	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader;

	private List<Path> dataFilesSingle;

	/**
	 * Sets up files for upload tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		setupDataFiles();
	}

	/**
	 * Setup data files for tests.
	 * @throws URISyntaxException
	 */
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
	 * @throws InterruptedException 
	 */
	@Test(expected=RuntimeException.class)
	public void testNoGalaxyConnectionUpload()
			throws ConstraintViolationException, UploadException, InterruptedException {
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
	 * Tests the case of Galaxy being shutdown while the archive is running.
	 * @throws Exception 
	 */
	@Test(expected=RuntimeException.class)
	public void testGalaxyShutdownRandomly()
			throws Exception {
		// I need to bring up a new version of Galaxy so I can connect to it,
		// then shut it down
		// without affecting other tests
		NonWindowsLocalGalaxyConfig galaxyConfig = new NonWindowsLocalGalaxyConfig();
		LocalGalaxy newLocalGalaxy = galaxyConfig.localGalaxy();

		// connect to running Galaxy first, so it passes all initial checks
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		GalaxyConnector galaxyConnector = new GalaxyConnector(newLocalGalaxy.getGalaxyURL(),
				newLocalGalaxy.getAdminName(), newLocalGalaxy.getAdminAPIKey());
		galaxyUploader.connectToGalaxy(galaxyConnector);
		
		assertTrue(galaxyUploader.isDataLocationAttached());
		assertTrue(galaxyUploader.isDataLocationConnected());

		// I should be able to upload a file initially
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploader_testGalaxyShutdownRandomly");

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		samples.add(galaxySample1);

		UploadWorker uploadWorker = galaxyUploader.uploadSamples(samples, libraryName,
				newLocalGalaxy.getAdminName());
		uploadWorker.run();
		assertNotNull(uploadWorker.getUploadResult());
		
		// shutdown running Galaxy
		newLocalGalaxy.shutdownGalaxy();
		
		// should not be connected
		assertTrue(galaxyUploader.isDataLocationAttached());
		assertFalse(galaxyUploader.isDataLocationConnected());

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

		assertTrue(galaxyUploader.isDataLocationAttached());
		assertFalse(unconnectedGalaxyUploader.isDataLocationAttached());
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

		UploadWorker uploadWorker = galaxyUploader.uploadSamples(samples,
				libraryName, localGalaxy.getAdminName());
		uploadWorker.run();
		UploadResult actualUploadResult = uploadWorker.getUploadResult();
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
