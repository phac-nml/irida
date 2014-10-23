package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for the GalaxyUploadWorker with a running instance of Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyUploadWorkerIT {

	@Autowired
	private LocalGalaxy localGalaxy;

	private List<Path> dataFilesSingle;
	
	private float delta = 0.00001f;

	/**
	 * Sets up files and objects for upload tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		Path dataFile1 = Paths.get(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());

		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);
	}
	
	/**
	 * Tests uploading samples to Galaxy (success).
	 * @throws URISyntaxException
	 * @throws UploadException
	 * @throws InterruptedException 
	 * @throws NoSuchValueException 
	 */
	@Test
	public void testUploadSampleSuccess() throws URISyntaxException,
			UploadException, InterruptedException, NoSuchValueException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploadWorkerIT-testUploadSampleSuccess");
		
		GalaxyFolderName folderName = new GalaxyFolderName("testData");
		
		UploadSample galaxySample = new GalaxySample(folderName, dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		
		GalaxyUploaderAPI galaxyAPI = new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getAdminName());
		assertFalse(worker.isFinished());
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		try {
			worker.getCurrentSample();
			fail("No " + NoSuchValueException.class.getSimpleName() + " thrown");
		} catch (NoSuchValueException e) {
		}
		
		try {
			worker.getSampleName();
			fail("No " + NoSuchValueException.class.getSimpleName() + " thrown");
		} catch (NoSuchValueException e) {
		}
		
		try {
			worker.getTotalSamples();
			fail("No " + NoSuchValueException.class.getSimpleName() + " thrown");
		} catch (NoSuchValueException e) {
		}
		
		Thread t = new Thread(worker);
		t.start();
		t.join();

		assertTrue(worker.isFinished());
		assertNotNull(worker.getUploadResult());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
		assertEquals(1.0f, worker.getProportionComplete(), delta);
		assertEquals(1, worker.getTotalSamples());
		assertEquals(0, worker.getCurrentSample());
		assertEquals(folderName, worker.getSampleName());
	}
	
	/**
	 * Tests uploading samples to Galaxy (failure).
	 * @throws URISyntaxException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadSampleFailure() throws URISyntaxException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploadWorkerIT-testUploadSampleFailure");
		
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		
		GalaxyUploaderAPI galaxyAPI = new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getNonExistentGalaxyAdminName());
		assertEquals(0.0f, worker.getProportionComplete(), delta);

		Thread t = new Thread(worker);
		t.start();
		t.join();

		assertTrue(worker.exceptionOccured());
		assertNotNull(worker.getUploadException());
		assertNull(worker.getUploadResult());
		assertTrue(worker.isFinished());
	}
}
