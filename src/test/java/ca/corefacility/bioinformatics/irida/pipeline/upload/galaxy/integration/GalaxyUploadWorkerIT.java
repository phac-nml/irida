package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadResultUtils.UploadExceptionRunnerTest;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadResultUtils.UploadFinishedRunnerTest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for the GalaxyUploadWorker with a running instance of Galaxy.
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
public class GalaxyUploadWorkerIT {

	@Autowired
	private LocalGalaxy localGalaxy;

	private List<Path> dataFilesSingle;

	@Before
	public void setup() throws URISyntaxException {
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
	 */
	@Test
	public void testUploadSampleSuccess() throws URISyntaxException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploadWorkerIT-testUploadSampleSuccess");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		
		GalaxyAPI galaxyAPI = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getAdminName());
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		worker.start();
		worker.join();

		assertNotNull(finishedRunnerTest.getFinishedResult());
		assertEquals(worker.getUploadResult(), finishedRunnerTest.getFinishedResult());
		assertNull(exceptionRunnerTest.getException());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
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
		
		GalaxyAPI galaxyAPI = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getNonExistentGalaxyAdminName());
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		worker.start();
		worker.join();

		assertNotNull(exceptionRunnerTest.getException());
		assertNotNull(worker.getUploadException());
		assertTrue(worker.exceptionOccured());
		assertNull(worker.getUploadResult());
		assertNull(finishedRunnerTest.getFinishedResult());
	}
}
