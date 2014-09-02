package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
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
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
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
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowsIT.class);
	
	@Autowired
	private LocalGalaxy localGalaxy;

	private Path dataFile1;
	private Path dataFile2;
	private Path dataFile3;
	private Path dataFile4;
	private Path dataFileNotExists;
	
	private GalaxyInstance galaxyAdminInstance;
	private HistoriesClient historiesClient;
	private ToolsClient toolsClient;
	private WorkflowsClient workflowsClient;
	private LibrariesClient librariesClient;
	private GalaxyWorkflowService galaxyWorkflowService;
	private GalaxyHistoriesService galaxyHistory;
	
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
		
		dataFile3 = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"testData3.fastq").toURI());
		dataFile4 = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"testData4.fastq").toURI());
		
		dataFileNotExists = Files.createTempFile("temp", ".temp");
		Files.delete(dataFileNotExists);
		assertFalse(Files.exists(dataFileNotExists));
		
		galaxyAdminInstance = localGalaxy.getGalaxyInstanceAdmin();
		toolsClient = galaxyAdminInstance.getToolsClient();
		workflowsClient = galaxyAdminInstance.getWorkflowsClient();
		historiesClient = galaxyAdminInstance.getHistoriesClient();
		librariesClient = galaxyAdminInstance.getLibrariesClient();
		
		GalaxyLibrariesService galaxyLibrariesService = new GalaxyLibrariesService(librariesClient);
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService);
		galaxyWorkflowService 
			= new GalaxyWorkflowService(historiesClient, workflowsClient,
					new StandardPasswordEncoder());
	}
	
	private void checkWorkflowIdValid(String workflowId) throws WorkflowException {
		if (!galaxyWorkflowService.isWorkflowIdValid(workflowId)) {
			throw new WorkflowException("Workflow id " + workflowId + " is not valid");
		}
	}
	
	/**
	 * Starts the execution of a workflow with a list of fastq files and the given workflow id.
	 * @param inputFilesForward  A list of forward read fastq files start the workflow.
	 * @param inputFilesReverse  A list of reverse read fastq files start the workflow.
	 * @param inputFileType The file type of the input files.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	private WorkflowOutputs runSingleCollectionWorkflow(List<Path> inputFilesForward, List<Path> inputFilesReverse,
			InputFileType inputFileType, String workflowId, String workflowInputLabel)
			throws ExecutionManagerException {
		checkNotNull(inputFilesForward, "inputFilesForward is null");
		checkNotNull(inputFilesReverse, "inputFilesReverse is null");
		checkArgument(inputFilesForward.size() == inputFilesReverse.size(),
				"inputFiles have different number of elements");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
		
		for (Path file : inputFilesForward) {
			checkArgument(Files.exists(file), "inputFileForward " + file + " does not exist");
		}
		
		for (Path file : inputFilesReverse) {
			checkArgument(Files.exists(file), "inputFilesReverse " + file + " does not exist");
		}
		
		checkWorkflowIdValid(workflowId);
				
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		List<Dataset> inputDatasetsForward = 
				galaxyHistory.uploadFilesListToHistory(inputFilesForward, inputFileType, workflowHistory);
		List<Dataset> inputDatasetsReverse = 
				galaxyHistory.uploadFilesListToHistory(inputFilesReverse, inputFileType, workflowHistory);
		assertEquals(inputFilesForward.size(), inputDatasetsForward.size());
		assertEquals(inputDatasetsForward.size(), inputDatasetsReverse.size());
		
		// construct list of datasets
		CollectionResponse collection = galaxyHistory.constructPairedFileCollection(inputDatasetsForward,
				inputDatasetsReverse, workflowHistory);
		logger.debug("Constructed dataset collection: id=" + collection.getId() + ", " + collection.getName());
		
		String workflowInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, workflowInputLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(collection.getId(),
				WorkflowInputs.InputSourceType.HDCA));
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		
		return output;
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
		dataFilesForward.add(dataFile2);
		
		List<Path> dataFilesReverse = new LinkedList<Path>();
		dataFilesReverse.add(dataFile3);
		dataFilesReverse.add(dataFile4);
		
		WorkflowOutputs workflowOutput = runSingleCollectionWorkflow(dataFilesForward,
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
		
		// output dataset should exist
		Dataset outputDataset = historiesClient.showDataset(workflowOutput.getHistoryId(), outputId);
		assertNotNull(outputDataset);
		
		// test get workflow status
		WorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertFalse(WorkflowState.UNKNOWN.equals(workflowStatus.getState()));
		float percentComplete = workflowStatus.getPercentComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Starts the execution of a workflow with a single fastq file and the given workflow id.
	 * @param inputFile  An input file to start the workflow.
	 * @param inputFileType The file type of the input file.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	public WorkflowOutputs runSingleFileWorkflow(Path inputFile, InputFileType inputFileType,
			String workflowId, String workflowInputLabel)
			throws ExecutionManagerException {
		checkNotNull(inputFile, "file is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
				
		checkArgument(Files.exists(inputFile), "inputFile " + inputFile + " does not exist");
		checkWorkflowIdValid(workflowId);
				
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		Dataset inputDataset = galaxyHistory.fileToHistory(inputFile, inputFileType, workflowHistory);
		assertNotNull(inputDataset);
		
		String workflowInputId = galaxyWorkflowService.
				getWorkflowInputId(workflowDetails, workflowInputLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(inputDataset.getId(), WorkflowInputs.InputSourceType.HDA));
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		
		return output;
	}
	
	/**
	 * Tests generating a checksum for a workflow successfully.
	 * @throws WorkflowException 
	 */
	@Test
	public void testGetWorkflowChecksumSuccess() throws WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		
		String checksum = galaxyWorkflowService.getWorkflowChecksum(workflowId);
		assertNotNull(checksum);
		
		logger.debug("generated checksum: " + checksum);
	}	
	
	/**
	 * Tests generating a checksum for a workflow and failing.
	 * @throws WorkflowException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowChecksumFail() throws WorkflowException {
		String workflowId = localGalaxy.getInvalidWorkflowId();

		galaxyWorkflowService.getWorkflowChecksum(workflowId);
	}
	
	/**
	 * Tests validating a workflow by a checksum successfully.
	 * @throws WorkflowException 
	 */
	@Test
	public void testValidateWorkflowByChecksumSuccess() throws WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowChecksum = localGalaxy.getSingleInputWorkflowChecksum();
		
		assertTrue(galaxyWorkflowService.validateWorkflowByChecksum(workflowChecksum, workflowId));
	}
	
	/**
	 * Tests validating a workflow by a checksum failure.
	 * @throws WorkflowException 
	 */
	@Test
	public void testValidateWorkflowByChecksumFail() throws WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowChecksum = localGalaxy.getSingleInputWorkflowChecksumInvalid();
		
		assertFalse(galaxyWorkflowService.validateWorkflowByChecksum(workflowChecksum, workflowId));
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
				runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
		assertNotNull(workflowOutput);
		assertNotNull(workflowOutput.getHistoryId());
		
		// history should exist
		HistoryDetails historyDetails = historiesClient.showHistory(workflowOutput.getHistoryId());
		assertNotNull(historyDetails);
		
		// outputs should exist
		assertNotNull(workflowOutput.getOutputIds());
		assertTrue(workflowOutput.getOutputIds().size() > 0);
		
		// each output dataset should exist
		for (String outputId : workflowOutput.getOutputIds()) {
			Dataset dataset = historiesClient.showDataset(workflowOutput.getHistoryId(), outputId);
			assertNotNull(dataset);
		}
		
		// test get workflow status
		WorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
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
				runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
		
		List<URL> outputURLs = galaxyWorkflowService.getWorkflowOutputDownloadURLs(workflowOutput);
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
				runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel);
		
		List<String> fakeOutputIds = new LinkedList<String>();
		fakeOutputIds.add(INVALID_HISTORY_ID);
		workflowOutput.setOutputIds(fakeOutputIds);
		
		galaxyWorkflowService.getWorkflowOutputDownloadURLs(workflowOutput);
	}
	
	/**
	 * Tests attempting to run a workflow that does not exist.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflow() throws ExecutionManagerException {
		String invalidWorkflowId = localGalaxy.getInvalidWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		runSingleFileWorkflow(dataFile1, FILE_TYPE, invalidWorkflowId, workflowInputLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input name.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testInvalidWorkflowInput() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String invalidWorkflowLabel = localGalaxy.getInvalidWorkflowLabel();
		runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, invalidWorkflowLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input file.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidWorkflowInputFile() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		runSingleFileWorkflow(dataFileNotExists, FILE_TYPE, workflowId, workflowInputLabel);
	}
	
	/**
	 * Tests attempting to run a workflow with an invalid input file type.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=NullPointerException.class)
	public void testInvalidWorkflowInputFileType() throws ExecutionManagerException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();
		runSingleFileWorkflow(dataFile1, INVALID_FILE_TYPE, workflowId, workflowInputLabel);
	}
}
