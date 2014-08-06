package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
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
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
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
public class GalaxyWorkflowsIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;

	private Path dataFile1;
	private Path dataFile2;
	private Path dataFileNotExists;
	
	private GalaxyInstance galaxyAdminInstance;
	private HistoriesClient historiesClient;
	private ToolsClient toolsClient;
	private WorkflowsClient workflowsClient;
	private GalaxyWorkflowService galaxyWorkflowManager;
	
	private static final String INVALID_HISTORY_ID = "1";
	
	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;
	private static final InputFileType INVALID_FILE_TYPE = null;

	/**
	 * Sets up files and objects for workflow tests.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		dataFile1 = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"testData1.fastq").toURI());
		dataFile2 = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"testData2.fastq").toURI());
		
		dataFileNotExists = Files.createTempFile("temp", ".temp");
		Files.delete(dataFileNotExists);
		assertFalse(Files.exists(dataFileNotExists));
		
		galaxyAdminInstance = localGalaxy.getGalaxyInstanceAdmin();
		toolsClient = galaxyAdminInstance.getToolsClient();
		workflowsClient = galaxyAdminInstance.getWorkflowsClient();
		historiesClient = galaxyAdminInstance.getHistoriesClient();
		GalaxyHistoriesService galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient);
		galaxyWorkflowManager 
			= new GalaxyWorkflowService(historiesClient, workflowsClient, galaxyHistory);
	}
	
	/**
	 * Tests executing a collections paired list workflow.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteCollectionsPairedList() throws ExecutionManagerException {
		
		String workflowId = localGalaxy.getWorklowCollectionListId();
		String workflowInputLabel = localGalaxy.getWorkflowCollectionListLabel();
		
		List<Path> dataFilesForward = new LinkedList<Path>();
		dataFilesForward.add(dataFile1);
		dataFilesForward.add(dataFile1);
		
		List<Path> dataFilesReverse = new LinkedList<Path>();
		dataFilesReverse.add(dataFile2);
		dataFilesReverse.add(dataFile2);
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleCollectionWorkflow(dataFilesForward,
						dataFilesReverse, FILE_TYPE, workflowId, workflowInputLabel);
		assertNotNull(workflowOutput);
		assertNotNull(workflowOutput.getHistoryId());
		
		// history should exist
		HistoryDetails historyDetails = historiesClient.showHistory(workflowOutput.getHistoryId());
		assertNotNull(historyDetails);
		
		// outputs should exist
		assertNotNull(workflowOutput.getOutputIds());
		assertEquals(1, workflowOutput.getOutputIds().size());
		String outputId = workflowOutput.getOutputIds().get(0);
		
		Dataset outputDataset = historiesClient.showDataset(workflowOutput.getHistoryId(), outputId);
		assertNotNull(outputDataset);
	}
	
	/**
	 * Tests executing a single workflow in Galaxy.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteWorkflow() throws ExecutionManagerException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
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
				galaxyWorkflowManager.getStatusForHistory(workflowOutput.getHistoryId());
		assertFalse(WorkflowState.UNKNOWN.equals(workflowStatus.getState()));
		float percentComplete = workflowStatus.getPercentComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Tests getting download URLs for workflow outputs. 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowOutputFiles() throws ExecutionManagerException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
		
		List<URL> outputURLs = galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutput);
		assertNotNull(outputURLs);
		assertEquals(1, outputURLs.size());
		
		URL singleOutputURL = outputURLs.get(0);
		assertNotNull(singleOutputURL);
	}
	
	/**
	 * Tests getting download URLs for invalid workflow outputs.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=GalaxyOutputsForWorkflowException.class)
	public void testGetWorkflowNoOutputFiles() throws ExecutionManagerException {
		
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		
		WorkflowOutputs workflowOutput = 
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
		
		List<String> fakeOutputIds = new LinkedList<String>();
		fakeOutputIds.add(INVALID_HISTORY_ID);
		workflowOutput.setOutputIds(fakeOutputIds);
		
		galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutput);
	}
	
	/**
	 * Tests attempting to run a workflow that does not exist.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflow() throws ExecutionManagerException {
		String invalidWorkflowId = localGalaxy.getInvalidWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, FILE_TYPE, invalidWorkflowId, workflowInputLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input name.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflowInput() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String invalidWorkflowLabel = localGalaxy.getInvalidWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, invalidWorkflowLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input file.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidWorkflowInputFile() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFileNotExists, FILE_TYPE, workflowId, workflowInputLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input file type.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=NullPointerException.class)
	public void testInvalidWorkflowInputFileType() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile1, INVALID_FILE_TYPE, workflowId, workflowInputLabel);
	}
}
