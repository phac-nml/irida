package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.DeleteGalaxyObjectFailedException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.ToolParameter;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.ImmutableMap;

/**
 * Integration tests for managing workflows in Galaxy.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
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
	
	private Path workflowPath;
	private Path invalidWorkflowPath;
	
	private GalaxyInstance galaxyAdminInstance;
	private HistoriesClient historiesClient;
	private ToolsClient toolsClient;
	private WorkflowsClient workflowsClient;
	private LibrariesClient librariesClient;
	private GalaxyWorkflowService galaxyWorkflowService;
	private GalaxyHistoriesService galaxyHistory;
	private IridaFileStorageUtility iridaFileStorageUtility;

	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;
	private static final InputFileType INVALID_FILE_TYPE = null;
	
	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5 * 60;
	
	/**
	 * Polling time in seconds to poll a Galaxy library to check if
	 * datasets have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5;
	
	private static final String VALID_FILTER_PARAMETER = "c1==''";
	private static final String INVALID_FILTER_PARAMETER = "c2==''";
	
	
	private static final String FORWARD_PAIR_NAME = "forward";
	private static final String REVERSE_PAIR_NAME = "reverse";
	
	private static final String BASE_NAME = "file";
	

	private static final String COLLECTION_NAME = "collection";

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
		
		workflowPath = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"GalaxyWorkflowSingleInput.ga").toURI());
		invalidWorkflowPath = Paths.get(GalaxyWorkflowsIT.class.getResource(
				"InvalidGalaxyWorkflowSingleInput.ga").toURI());
		
		dataFileNotExists = Files.createTempFile("temp", ".temp");
		Files.delete(dataFileNotExists);
		assertFalse(Files.exists(dataFileNotExists));
		
		galaxyAdminInstance = localGalaxy.getGalaxyInstanceAdmin();
		toolsClient = galaxyAdminInstance.getToolsClient();
		workflowsClient = galaxyAdminInstance.getWorkflowsClient();
		historiesClient = galaxyAdminInstance.getHistoriesClient();
		librariesClient = galaxyAdminInstance.getLibrariesClient();
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();

		GalaxyLibrariesService galaxyLibrariesService = new GalaxyLibrariesService(librariesClient, LIBRARY_POLLING_TIME, LIBRARY_TIMEOUT, 1, iridaFileStorageUtility);
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService, iridaFileStorageUtility);
		galaxyWorkflowService 
			= new GalaxyWorkflowService(workflowsClient, StandardCharsets.UTF_8);
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
		
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		List<Dataset> inputDatasetsForward = 
				uploadFilesListToHistory(inputFilesForward, inputFileType, workflowHistory);
		List<Dataset> inputDatasetsReverse = 
				uploadFilesListToHistory(inputFilesReverse, inputFileType, workflowHistory);
		assertEquals(inputFilesForward.size(), inputDatasetsForward.size());
		assertEquals(inputDatasetsForward.size(), inputDatasetsReverse.size());
		
		// construct list of datasets
		CollectionResponse collection = constructPairedFileCollection(inputDatasetsForward,
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
	 * Constructs a collection containing a list of files from the given datasets.
	 * @param inputDatasetsForward  The forward datasets to construct a collection from.
	 * @param inputDatasetsReverse  The reverse datasets to construct a collection from.
	 * @param history  The history to construct the collection within.
	 * @return  A CollectionResponse describing the dataset collection.
	 * @throws ExecutionManagerException  If an exception occured constructing the collection.
	 */
	public CollectionResponse constructPairedFileCollection(List<Dataset> inputDatasetsForward,
			List<Dataset> inputDatasetsReverse, History history) throws ExecutionManagerException {
		checkNotNull(inputDatasetsForward, "inputDatasetsForward is null");
		checkNotNull(inputDatasetsReverse, "inputDatasetsReverse is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history does not have an associated id");
		checkArgument(inputDatasetsForward.size() == inputDatasetsReverse.size(),
				"inputDatasets do not have equal sizes");
		
		CollectionDescription collectionDescription = new CollectionDescription();
		collectionDescription.setCollectionType(DatasetCollectionType.LIST_PAIRED.toString());
		collectionDescription.setName(COLLECTION_NAME);
		
		for (int i = 0; i < inputDatasetsForward.size(); i++) {
			Dataset datasetForward = inputDatasetsForward.get(i);
			Dataset datasetReverse = inputDatasetsReverse.get(i);
			
			HistoryDatasetElement elementForward = new HistoryDatasetElement();
			elementForward.setId(datasetForward.getId());
			elementForward.setName(FORWARD_PAIR_NAME);
			
			HistoryDatasetElement elementReverse = new HistoryDatasetElement();
			elementReverse.setId(datasetReverse.getId());
			elementReverse.setName(REVERSE_PAIR_NAME);
			
		    // Create an object to link together the forward and reverse reads for file2
		    CollectionElement element = new CollectionElement();
		    element.setName(BASE_NAME + i);
		    element.setCollectionType(DatasetCollectionType.PAIRED.toString());
		    element.addCollectionElement(elementForward);
		    element.addCollectionElement(elementReverse);
			
			collectionDescription.addDatasetElement(element);
		}
		
		try {
			return historiesClient.createDatasetCollection(history.getId(), collectionDescription);
		} catch (RuntimeException e) {
			throw new ExecutionManagerException("Could not construct dataset collection", e);
		}
	}
	
	
	/**
	 * Uploads a list of files into the given history.
	 * @param dataFiles  The list of files to upload.
	 * @param inputFileType  The type of files to upload.
	 * @param history  The history to upload the files into.String
	 * @return  A list of Datasets describing each uploaded file.
	 * @throws UploadException  If an error occured uploading the file.
	 * @throws GalaxyDatasetException If there was an issue finding the corresponding dataset for
	 * 	the file in the history
	 */
	private List<Dataset> uploadFilesListToHistory(List<Path> dataFiles,
			InputFileType inputFileType, History history) throws UploadException, GalaxyDatasetException {
		checkNotNull(dataFiles, "dataFiles is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(history, "history is null");
		
		List<Dataset> inputDatasets = new LinkedList<Dataset>();
		
		for (Path file : dataFiles) {
			Dataset inputDataset = galaxyHistory.fileToHistory(file, inputFileType, history);
			inputDatasets.add(inputDataset);
		}
		
		return inputDatasets;
	}
	
	/**
	 * Tests out successfully uploading a workflow to Galaxy.
	 * @throws IOException 
	 * @throws WorkflowUploadException 
	 */
	@Test
	public void testUploadWorkflowSuccess() throws WorkflowUploadException, IOException {
		assertNotNull(galaxyWorkflowService.uploadGalaxyWorkflow(workflowPath));
	}
	
	/**
	 * Tests out failing to upload a workflow to Galaxy.
	 * @throws IOException 
	 * @throws WorkflowUploadException 
	 */
	@Test(expected=WorkflowUploadException.class)
	public void testUploadWorkflowFail() throws WorkflowUploadException, IOException {
		galaxyWorkflowService.uploadGalaxyWorkflow(invalidWorkflowPath);
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
		GalaxyWorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		float percentComplete = workflowStatus.getProportionComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 1.0f);
	}
	
	/**
	 * Starts the execution of a workflow with a single fastq file and the given workflow id.
	 * @param inputFile  An input file to start the workflow.
	 * @param inputFileType The file type of the input file.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	private WorkflowOutputs runSingleFileWorkflow(Path inputFile, InputFileType inputFileType,
			String workflowId, String workflowInputLabel) throws ExecutionManagerException {
		return runSingleFileWorkflow(inputFile, inputFileType, workflowId, workflowInputLabel, null);
	}
	
	/**
	 * Starts the execution of a workflow with a single fastq file and the given workflow id.
	 * @param inputFile  An input file to start the workflow.
	 * @param inputFileType The file type of the input file.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @param toolParameters  A map of tool parameters to set.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	private WorkflowOutputs runSingleFileWorkflow(Path inputFile, InputFileType inputFileType,
			String workflowId, String workflowInputLabel, Map<String, ToolParameter> toolParameters)
			throws ExecutionManagerException {
		checkNotNull(inputFile, "file is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
				
		checkArgument(Files.exists(inputFile), "inputFile " + inputFile + " does not exist");
				
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
		
		if (toolParameters != null) {
			for (String toolId : toolParameters.keySet()) {
				ToolParameter toolParameter = toolParameters.get(toolId);
				inputs.setToolParameter(toolId, toolParameter);
			}
		}
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		
		return output;
	}

	/**
	 * Runs a test filter workflow with the given input.
	 * 
	 * @param history
	 *            The history to run the workflow in.
	 * @param inputFile
	 *            The file to run the workflow on.
	 * @param filterParameters
	 *            The condition to run the workflow with.
	 * @return A {@link WorkflowOutputs} for this workflow.
	 * @throws ExecutionManagerException
	 */
	private WorkflowOutputs runSingleFileTabularWorkflowFilterTool(History history, Path inputFile,
			String filterParameters) throws ExecutionManagerException {
		String workflowId = localGalaxy.getWorkflowFilterId();
		String workflowInputLabel = localGalaxy.getWorkflowFilterLabel();

		ToolParameter toolParameter = new ToolParameter("cond", filterParameters);
		return runSingleFileTabularWorkflow(workflowId, workflowInputLabel, history, inputFile, "Filter1",
				toolParameter);
	}

	/**
	 * Runs a test sleep workflow with the given input.
	 * 
	 * @param history
	 *            The history to run the workflow in.
	 * @param inputFile
	 *            The file to run the workflow on.
	 * @param sleepTime
	 *            The sleep time.
	 * @return A {@link WorkflowOutputs} for this workflow.
	 * @throws ExecutionManagerException
	 */
	private WorkflowOutputs runSingleFileTabularWorkflowSleepTool(History history, Path inputFile, String sleepTime)
			throws ExecutionManagerException {
		String workflowId = localGalaxy.getWorkflowSleepId();
		String workflowInputLabel = localGalaxy.getWorkflowSleepLabel();

		ToolParameter toolParameter = new ToolParameter("time", sleepTime);
		return runSingleFileTabularWorkflow(workflowId, workflowInputLabel, history, inputFile, "sleep", toolParameter);
	}

	/**
	 * Runs a test workflow with the given parameters and input file.
	 * 
	 * @param workflowId
	 *            The id of the workflow to run.
	 * @param workflowInputLabel
	 *            The lable of the input for the workflow.
	 * @param history
	 *            The history to run the workflow in.
	 * @param inputFile
	 *            The file to run the workflow on.
	 * @param toolName
	 *            The toolName of a parameter to override.
	 * @param toolParameter
	 *            The overridden tool parameter.
	 * @return A {@link WorkflowOutputs} for this workflow.
	 * @throws ExecutionManagerException
	 */
	private WorkflowOutputs runSingleFileTabularWorkflow(String workflowId, String workflowInputLabel, History history,
			Path inputFile, String toolName, ToolParameter toolParameter) throws ExecutionManagerException {

		checkArgument(Files.exists(inputFile), "inputFile " + inputFile + " does not exist");

		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);

		// upload dataset to history
		Dataset inputDataset = fileToHistory(inputFile, "tabular", history.getId());
		assertNotNull(inputDataset);

		String workflowInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, workflowInputLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(history.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(inputDataset.getId(),
				WorkflowInputs.InputSourceType.HDA));

		inputs.setToolParameter(toolName, toolParameter);

		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());

		return output;
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
		GalaxyWorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		float percentComplete = workflowStatus.getProportionComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 1.0f);
	}
	
	/**
	 * Tests executing a single workflow in Galaxy and getting the status after completion.
	 * @throws ExecutionManagerException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGetWorkflowStatusComplete() throws ExecutionManagerException, TimeoutException, InterruptedException {	
		History history = galaxyHistory.newHistoryForWorkflow();
		
		WorkflowOutputs workflowOutput = 
				runSingleFileTabularWorkflowFilterTool(history, dataFile1, VALID_FILTER_PARAMETER);
		
		Util.waitUntilHistoryComplete(workflowOutput.getHistoryId(), galaxyHistory, 60);
		
		// test get workflow status
		GalaxyWorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertEquals("final workflow state is invalid", GalaxyWorkflowState.OK, workflowStatus.getState());
		assertTrue("final workflow state is invalid", workflowStatus.completedSuccessfully());
	}
	
	/**
	 * Tests executing a single workflow in Galaxy and getting an error status after completion.
	 * @throws ExecutionManagerException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGetWorkflowStatusError() throws ExecutionManagerException, TimeoutException, InterruptedException {	
		History history = galaxyHistory.newHistoryForWorkflow();
		
		// no column c2 for this input file, so should give an error
		WorkflowOutputs workflowOutput = 
				runSingleFileTabularWorkflowFilterTool(history, dataFile1, INVALID_FILTER_PARAMETER);
		
		Util.waitUntilHistoryComplete(workflowOutput.getHistoryId(), galaxyHistory, 60);
		
		// test get workflow status
		GalaxyWorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertEquals("final workflow state is invalid", GalaxyWorkflowState.ERROR, workflowStatus.getState());
		assertTrue("final workflow state is invalid", workflowStatus.errorOccurred());
	}
	
	/**
	 * Tests executing a single workflow in Galaxy and getting an error status if one of the tools is in error even while running.
	 * @throws ExecutionManagerException
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testGetWorkflowStatusErrorWhileRunning() throws ExecutionManagerException, TimeoutException, InterruptedException {
		final String SLEEP_TIME_SECONDS = "30";
		
		History history = galaxyHistory.newHistoryForWorkflow();
		
		WorkflowOutputs workflowOutput = runSingleFileTabularWorkflowFilterTool(history, dataFile1, INVALID_FILTER_PARAMETER);
		
		Util.waitUntilHistoryComplete(workflowOutput.getHistoryId(), galaxyHistory, 60);
		
		// test get workflow status, should be in error
		GalaxyWorkflowStatus workflowStatus = 
				galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertEquals("final workflow state is invalid", GalaxyWorkflowState.ERROR, workflowStatus.getState());
		
		// run a sleep workflow to keep busy
		runSingleFileTabularWorkflowSleepTool(history, dataFile2, SLEEP_TIME_SECONDS);
				
		// check status.  I'm assuming the tasks launched above are not complete.
		workflowStatus = galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertTrue("workflow should still be running", workflowStatus.isRunning());
		assertTrue("an error should have occured even while running", workflowStatus.errorOccurred());
		
		Util.waitUntilHistoryComplete(workflowOutput.getHistoryId(), galaxyHistory, 60);
		
		workflowStatus = galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		assertEquals("workflow state should be in error after completion", GalaxyWorkflowState.ERROR, workflowStatus.getState());
		assertTrue("workflow is not in error state", workflowStatus.errorOccurred());
	}

	private Dataset fileToHistory(Path path, String fileType, String historyId) throws GalaxyDatasetException {		
		FileUploadRequest uploadRequest = new FileUploadRequest(historyId, path.toFile());
		uploadRequest.setFileType(fileType.toString());
		
		toolsClient.uploadRequest(uploadRequest);
				
		return galaxyHistory.getDatasetForFileInHistory(path.toFile().getName(), historyId);
	}

	/**
	 * Tests executing a single workflow in Galaxy and changing a single tool
	 * parameter.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteWorkflowChangeToolParameter() throws ExecutionManagerException {
		String toolId = "Grep1";

		String workflowId = localGalaxy.getSingleInputWorkflowId();
		String workflowInputLabel = localGalaxy.getSingleInputWorkflowLabel();

		Map<String, ToolParameter> toolParameters = ImmutableMap.of(toolId, new ToolParameter("pattern", "^#"));
		WorkflowOutputs workflowOutput = runSingleFileWorkflow(dataFile1, FILE_TYPE, workflowId, workflowInputLabel,
				toolParameters);
		assertNotNull("workflowOutput should not be null", workflowOutput);
		assertNotNull("workflowOutput history id should not be null", workflowOutput.getHistoryId());

		// history should exist
		HistoryDetails historyDetails = historiesClient.showHistory(workflowOutput.getHistoryId());
		assertNotNull("historyDetails for the history for the workflow should not be null", historyDetails);

		// outputs should exist
		assertNotNull("outputIds for the workflow should not be null", workflowOutput.getOutputIds());
		assertTrue("there should exist output dataset ids for the workflow", workflowOutput.getOutputIds().size() > 0);

		// each output dataset should exist
		for (String outputId : workflowOutput.getOutputIds()) {
			Dataset dataset = historiesClient.showDataset(workflowOutput.getHistoryId(), outputId);
			assertNotNull("the output dataset should exist", dataset);

			HistoryContentsProvenance provenance = historiesClient.showProvenance(workflowOutput.getHistoryId(),
					dataset.getId());
			if (toolId.equals(provenance.getToolId())) {
				Map<String, Object> parametersMap = provenance.getParameters();
				assertEquals("pattern parameter is correct", "\"^#\"", parametersMap.get("pattern"));
			}
		}

		// test get workflow status
		GalaxyWorkflowStatus workflowStatus = galaxyHistory.getStatusForHistory(workflowOutput.getHistoryId());
		float proportionComplete = workflowStatus.getProportionComplete();
		assertTrue("the workflow proportion complete should be between 0 and 1", 0.0f <= proportionComplete
				&& proportionComplete <= 1.0f);
	}
	
	/**
	 * Tests getting workflow details.
	 * 
	 * @throws WorkflowException
	 */
	@Test
	public void testGetWorkflowDetailsSuccess() throws WorkflowException {
		String workflowId = localGalaxy.getSingleInputWorkflowId();
		assertNotNull(galaxyWorkflowService.getWorkflowDetails(workflowId));
	}

	/**
	 * Tests getting workflow details and failing.
	 * 
	 * @throws WorkflowException
	 */
	@Test(expected = WorkflowException.class)
	public void testGetWorkflowDetailsFail() throws WorkflowException {
		String workflowId = localGalaxy.getInvalidWorkflowId();
		assertNotNull(galaxyWorkflowService.getWorkflowDetails(workflowId));
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
	
	/**
	 * Tests deleting a workflow and succeeding.
	 * 
	 * @throws WorkflowUploadException
	 * @throws IOException
	 * @throws WorkflowException
	 * @throws DeleteGalaxyObjectFailedException
	 */
	@Test
	public void testDeleteWorkflowSuccess() throws WorkflowUploadException, IOException, WorkflowException,
			DeleteGalaxyObjectFailedException {
		String workflowId = galaxyWorkflowService.uploadGalaxyWorkflow(workflowPath);
		WorkflowDetails details = galaxyWorkflowService.getWorkflowDetails(workflowId);
		assertFalse(details.isDeleted());

		galaxyWorkflowService.deleteWorkflow(workflowId);

		details = galaxyWorkflowService.getWorkflowDetails(workflowId);
		assertTrue(details.isDeleted());
	}

	/**
	 * Tests deleting a workflow with an invalid id and failing.
	 * 
	 * @throws DeleteGalaxyObjectFailedException
	 */
	@Test(expected = DeleteGalaxyObjectFailedException.class)
	public void testDeleteWorkflowFail() throws DeleteGalaxyObjectFailedException {
		galaxyWorkflowService.deleteWorkflow("invalid");
	}
}
