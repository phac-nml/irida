package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.ProgressUpdate;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.UploadEventListenerTracker;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for the GalaxyUploadWorker with a running instance of Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
	IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
	IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
	WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceTestConfig.class,
	RemoteWorkflowServiceTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyUploadWorkerIT {

	@Autowired
	private LocalGalaxy localGalaxy;

	private List<Path> dataFilesSingle;

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
	 */
	@Test
	public void testUploadSampleSuccess() throws URISyntaxException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"GalaxyUploadWorkerIT-testUploadSampleSuccess");
		
		GalaxyFolderName folderName = new GalaxyFolderName("testData");
		
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();

		UploadSample galaxySample = new GalaxySample(folderName, dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		
		GalaxyUploaderAPI galaxyAPI = new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getAdminName());
		worker.addUploadEventListener(eventListener);
		Thread t = new Thread(worker);
		t.start();
		t.join();

		assertEquals(1,eventListener.getResults().size());
		assertEquals(worker.getUploadResult(), eventListener.getResults().get(0));
		assertEquals(0,eventListener.getExceptions().size());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
		assertEquals(1,eventListener.getProgressUpdates().size());
		assertEquals(new ProgressUpdate(1,0,folderName),eventListener.getProgressUpdates().get(0));
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
		
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		
		GalaxyUploaderAPI galaxyAPI = new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(), localGalaxy
				.getAdminName(), localGalaxy.getAdminAPIKey());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI,
				samples, libraryName, localGalaxy.getNonExistentGalaxyAdminName());
		worker.addUploadEventListener(eventListener);
		Thread t = new Thread(worker);
		t.start();
		t.join();

		assertEquals(1, eventListener.getExceptions().size());
		assertEquals(eventListener.getExceptions().get(0),worker.getUploadException());
		assertTrue(worker.exceptionOccured());
		assertNull(worker.getUploadResult());
		assertEquals(0, eventListener.getResults().size());
	}
}
