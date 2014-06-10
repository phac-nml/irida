package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
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
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistory;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowManager;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for managing workflows in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
	IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
	IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyWorkflowManagerIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;

	private Path dataFile;
	private Path dataFileNotExists;
	
	private GalaxyInstance galaxyAdminInstance;
	private HistoriesClient historiesClient;
	private GalaxySearch galaxySearch;
	private GalaxyWorkflowManager galaxyWorkflowManager;
	
	private static final String INVALID_HISTORY_ID = "1";

	/**
	 * Sets up files and objects for workflow tests.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		dataFile = Paths.get(GalaxyWorkflowManagerIT.class.getResource(
				"testData1.fastq").toURI());
		
		File tempFile = File.createTempFile("temp", ".temp");
		tempFile.delete();
		assertFalse(tempFile.exists());
		dataFileNotExists = tempFile.toPath();
		
		galaxyAdminInstance = localGalaxy.getGalaxyInstanceAdmin();
		historiesClient = galaxyAdminInstance.getHistoriesClient();
		galaxySearch = new GalaxySearch(galaxyAdminInstance);
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyAdminInstance, galaxySearch);
		galaxyWorkflowManager = new GalaxyWorkflowManager(galaxyAdminInstance, galaxyHistory);
	}
	
	/**
	 * Tests executing a single workflow in Galaxy.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 * @throws NoGalaxyHistoryException
	 * @throws URISyntaxException 
	 */
	@Test
	public void testExecuteWorkflow() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException, NoGalaxyHistoryException, URISyntaxException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile, workflowId, workflowInputLabel);
		assertNotNull(workflowOutput);
		assertNotNull(workflowOutput.getHistoryId());
		
		// history should exist
		HistoryDetails historyDetails = historiesClient.showHistory(workflowOutput.getHistoryId());
		assertNotNull(historyDetails);
		
		// outputs should exist
		assertNotNull(workflowOutput.getOutputIds());
		assertTrue(workflowOutput.getOutputIds().size() > 0);
		
		// each datasets should exist
		for (String outputId : workflowOutput.getOutputIds()) {
			Dataset dataset = historiesClient.showDataset(workflowOutput.getHistoryId(), outputId);
			assertNotNull(dataset);
		}
		
		// test get workflow status
		WorkflowStatus workflowStatus = 
				galaxyWorkflowManager.getStatusFor(workflowOutput.getHistoryId());
		assertFalse(WorkflowState.UNKNOWN.equals(workflowStatus.getState()));
		float percentComplete = workflowStatus.getPercentComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Tests getting download URLs for workflow outputs. 
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 * @throws NoGalaxyHistoryException
	 * @throws URISyntaxException 
	 */
	@Test
	public void testGetWorkflowOutputFiles() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException, NoGalaxyHistoryException, URISyntaxException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile, workflowId, workflowInputLabel);
		
		List<URL> outputURLs = galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutput);
		assertNotNull(outputURLs);
		assertEquals(1, outputURLs.size());
		
		URL singleOutputURL = outputURLs.get(0);
		assertNotNull(singleOutputURL);
	}
	
	/**
	 * Tests getting download URLs for invalid workflow outputs.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 * @throws NoGalaxyHistoryException
	 * @throws URISyntaxException 
	 */
	@Test(expected=GalaxyOutputsForWorkflowException.class)
	public void testGetWorkflowNoOutputFiles() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException, NoGalaxyHistoryException, URISyntaxException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile, workflowId, workflowInputLabel);
		
		List<String> fakeOutputIds = new LinkedList<String>();
		fakeOutputIds.add(INVALID_HISTORY_ID);
		workflowOutput.seOutputIds(fakeOutputIds);
		
		galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutput);
	}
	
	/**
	 * Tests attempting to run a workflow that does not exist.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflow() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		String invalidWorkflowId = localGalaxy.getInvalidWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile, invalidWorkflowId, workflowInputLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input name.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflowInput() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String invalidWorkflowLabel = localGalaxy.getInvalidWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile, workflowId, invalidWorkflowLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input file.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidWorkflowInputFile() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFileNotExists, workflowId, workflowInputLabel);
	}
}
