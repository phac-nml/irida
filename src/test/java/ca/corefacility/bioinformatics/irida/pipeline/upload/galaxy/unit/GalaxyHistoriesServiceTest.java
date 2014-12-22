package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Tests the GalaxyHistory class
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistoriesServiceTest {

	@Mock private HistoriesClient historiesClient;
	@Mock private HistoryDetails historyDetails;
	@Mock private ToolsClient toolsClient;
	@Mock private UniformInterfaceException uniformInterfaceException;
	@Mock private ClientResponse invalidResponse;
	@Mock private ClientResponse okayResponse;
	@Mock private GalaxyLibrariesService galaxyLibrariesService;
	
	private GalaxyHistoriesService galaxyHistory;
	
	private final String libraryFileId = "1";
	
	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;
	private static final String HISTORY_ID = "1";
	private static final String INVALID_HISTORY_ID = "2";
	
	private static final String FILENAME = "filename";
	private static final String DATA_ID = "2";
	private static final String DATA_ID_2 = "2";
	
	private static final String VALID_HISTORY_ID = "1";
	
	private static final float delta = 0.00001f;
	
	private List<HistoryContents> datasetHistoryContents;
	private History history;
	
	private Path dataFile;
	private Path dataFile2;
	
	private Dataset datasetForFile;
	
	/**
	 * Sets up objects for history tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.FORBIDDEN);
		
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient,
				galaxyLibrariesService);
		
		dataFile = Paths.get(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = Paths.get(this.getClass().getResource("testData2.fastq").toURI());

		history = new History();
		history.setId(HISTORY_ID);

		datasetHistoryContents = buildHistoryContentsList(FILENAME, DATA_ID);
		
		datasetForFile = new Dataset();
		datasetForFile.setName(FILENAME);
		datasetForFile.setId(DATA_ID);
		datasetForFile.setUrl("datasets/" + DATA_ID + "/display");
		datasetForFile.setGalaxyUrl("http://fakehost");
		datasetForFile.setApiKey("1");
	}
	
	private List<HistoryContents> buildHistoryContentsList(String filename, String id) {
		HistoryContents datasetHistoryContent = new HistoryContents();
		datasetHistoryContent.setName(filename);
		datasetHistoryContent.setId(id);
		List<HistoryContents> datasetHistoryContents = new ArrayList<HistoryContents>();
		datasetHistoryContents.add(datasetHistoryContent);
		
		return Arrays.asList(datasetHistoryContent);
	}
	
	private List<HistoryContents> buildHistoryContentsList(String filename, String id,
			String filename2, String id2) {
		
		HistoryContents datasetHistoryContent = new HistoryContents();
		datasetHistoryContent.setName(filename);
		datasetHistoryContent.setId(id);
		
		List<HistoryContents> datasetHistoryContents = new ArrayList<HistoryContents>();
		datasetHistoryContents.add(datasetHistoryContent);
		
		HistoryContents datasetHistoryContent2 = new HistoryContents();
		datasetHistoryContent2.setName(filename2);
		datasetHistoryContent2.setId(id2);
		
		return Arrays.asList(datasetHistoryContent, datasetHistoryContent2);
	}
	
	/**
	 * Tests getting status for a completed/ok workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusOkState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1", "2"));
		validStateIds.put("running", Arrays.asList());
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("ok");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		GalaxyWorkflowStatus status = galaxyHistory.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(GalaxyWorkflowState.OK, status.getState());
		assertEquals(100.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusRunningState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList());
		validStateIds.put("running", Arrays.asList("1", "2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		GalaxyWorkflowStatus status = galaxyHistory.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(GalaxyWorkflowState.RUNNING, status.getState());
		assertEquals(0.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusPartialCompleteState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1"));
		validStateIds.put("running", Arrays.asList("2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		GalaxyWorkflowStatus status = galaxyHistory.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(GalaxyWorkflowState.RUNNING, status.getState());
		assertEquals(50.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for an invalid history.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetStatusInvalidHistory() throws ExecutionManagerException {
		when(historiesClient.showHistory(INVALID_HISTORY_ID)).thenThrow(uniformInterfaceException);
		galaxyHistory.getStatusForHistory(INVALID_HISTORY_ID);
	}
	
	/**
	 * Tests building a new history.
	 */
	@Test
	public void testCreateNewHistory() {
		History newHistory = new History();
		
		when(historiesClient.create(any(History.class))).thenReturn(newHistory);
		
		assertEquals(newHistory, galaxyHistory.newHistoryForWorkflow());
	}
	
	/**
	 * Tests moving a library dataset to a history.
	 */
	@Test
	public void testLibraryDatasetToHistory() {
		HistoryDetails historyDetails = new HistoryDetails();
		History createdHistory = new History();
		createdHistory.setId(HISTORY_ID);
		
		when(historiesClient.createHistoryDataset(any(String.class),
				any(HistoryDataset.class))).thenReturn(historyDetails);
		
		assertNotNull(galaxyHistory.libraryDatasetToHistory(libraryFileId, createdHistory));
	}
	
	/**
	 * Tests uploading a file to a history.
	 * @throws UploadException
	 * @throws GalaxyDatasetException 
	 */
	@Test
	public void testFileToHistorySuccess() throws UploadException, GalaxyDatasetException {
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		Dataset dataset = new Dataset();
		createdHistory.setId(HISTORY_ID);
		List<HistoryContents> historyContentsList = buildHistoryContentsList(filename, DATA_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).thenReturn(okayResponse);
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(historyContentsList);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID)).thenReturn(dataset);
		
		assertEquals(dataset, galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory));
	}
	
	/**
	 * Tests failing to upload a file to a history.
	 * @throws UploadException
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=UploadException.class)
	public void testFileToHistoryFailUpload() throws UploadException, GalaxyDatasetException {
		History createdHistory = new History();
		createdHistory.setId(HISTORY_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(invalidResponse);
		
		galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory);
	}
	
	/**
	 * Tests failing to find a Dataset object after uploading a file to a history.
	 * @throws UploadException
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testFileToHistoryFailFindDataset() throws UploadException, GalaxyDatasetException {
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		createdHistory.setId(HISTORY_ID);
		List<HistoryContents> historyContentsList = buildHistoryContentsList(filename, DATA_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(okayResponse);
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(historyContentsList);
		
		galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory);
	}
	
	/**
	 * Tests uploading a list of files to a history.
	 * @throws UploadException
	 * @throws GalaxyDatasetException 
	 */
	@Test
	public void testFilesListToHistorySuccess() throws UploadException, GalaxyDatasetException {
		List<Path> files = new LinkedList<Path>();
		files.add(dataFile);
		files.add(dataFile2);
		
		List<Dataset> datasets = new LinkedList<Dataset>();
		
		String filename = dataFile.toFile().getName();
		String filename2 = dataFile2.toFile().getName();
		History createdHistory = new History();
		Dataset dataset = new Dataset();
		createdHistory.setId(HISTORY_ID);
		Dataset dataset2 = new Dataset();
		List<HistoryContents> historyContentsList = buildHistoryContentsList(filename, DATA_ID,
				filename2, DATA_ID_2);
		
		datasets.add(dataset);
		datasets.add(dataset2);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).thenReturn(okayResponse);
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(historyContentsList);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID)).thenReturn(dataset);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID_2)).thenReturn(dataset2);
		
		assertEquals(datasets, galaxyHistory.uploadFilesListToHistory(files, FILE_TYPE, createdHistory));
	}
	
	/**
	 * Tests failing to upload a file list to a history.
	 * @throws UploadException
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=UploadException.class)
	public void testFilesListToHistoryFailUpload() throws UploadException, GalaxyDatasetException {
		History createdHistory = new History();
		createdHistory.setId(HISTORY_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(invalidResponse);
		
		galaxyHistory.uploadFilesListToHistory(Arrays.asList(dataFile), FILE_TYPE, createdHistory);
	}
	
	/**
	 * Tests successfull execution of constructing a list of paired-end files dataset collection.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testConstructPairedFileCollectionSuccess() throws ExecutionManagerException {
		CollectionResponse collectionResponse = new CollectionResponse();
		
		History history = new History();
		history.setId(HISTORY_ID);
		
		Dataset datasetForward = new Dataset();
		datasetForward.setId(DATA_ID);
		List<Dataset> inputDatasetsForward = Arrays.asList(datasetForward);
		
		Dataset datasetReverse = new Dataset();
		datasetReverse.setId(DATA_ID_2);
		List<Dataset> inputDatasetsReverse = Arrays.asList(datasetReverse);
		
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenReturn(collectionResponse);
		
		assertEquals(collectionResponse,galaxyHistory.constructPairedFileCollection(
				inputDatasetsForward, inputDatasetsReverse, history));
	}
	
	/**
	 * Tests failure to construct a list of paired-end files dataset collection.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testConstructPairedFileCollectionFail() throws ExecutionManagerException {	
		History history = new History();
		history.setId(HISTORY_ID);
		
		Dataset datasetForward = new Dataset();
		datasetForward.setId(DATA_ID);
		List<Dataset> inputDatasetsForward = Arrays.asList(datasetForward);
		
		Dataset datasetReverse = new Dataset();
		datasetReverse.setId(DATA_ID_2);
		List<Dataset> inputDatasetsReverse = Arrays.asList(datasetReverse);
		
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenThrow(new RuntimeException());
		
		galaxyHistory.constructPairedFileCollection(inputDatasetsForward,
				inputDatasetsReverse, history);
	}
	
	/**
	 * Tests successfull construction of a dataset collection.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testConstructCollectionSuccess() throws ExecutionManagerException {
		CollectionResponse collectionResponse = new CollectionResponse();
		
		History history = new History();
		history.setId(HISTORY_ID);
		
		HistoryDatasetElement datasetElement = new HistoryDatasetElement();
		datasetElement.setId(DATA_ID);
		CollectionDescription description = new CollectionDescription();
		description.addDatasetElement(datasetElement);
				
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenReturn(collectionResponse);
		
		assertEquals(collectionResponse,galaxyHistory.constructCollection(
				description, history));
	}
	
	/**
	 * Tests failing to construct a dataset collection.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testConstructCollectionFail() throws ExecutionManagerException {		
		History history = new History();
		history.setId(HISTORY_ID);
		
		HistoryDatasetElement datasetElement = new HistoryDatasetElement();
		datasetElement.setId(DATA_ID);
		CollectionDescription description = new CollectionDescription();
		description.addDatasetElement(datasetElement);
				
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenThrow(new RuntimeException());
		
		galaxyHistory.constructCollection(description, history);
	}
	
	/**
	 * Tests successfull construction of a list of datasets.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testConstructCollectionListSuccess() throws ExecutionManagerException {
		CollectionResponse collectionResponse = new CollectionResponse();
		
		History history = new History();
		history.setId(HISTORY_ID);
		
		Dataset datasetForward = new Dataset();
		datasetForward.setId(DATA_ID);
		List<Dataset> datasets = Arrays.asList(datasetForward);
		
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenReturn(collectionResponse);
		
		assertEquals(collectionResponse,galaxyHistory.constructCollectionList(
				datasets, history));
	}
	
	/**
	 * Tests failure to construct of a list of datasets.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testConstructCollectionListFail() throws ExecutionManagerException {		
		History history = new History();
		history.setId(HISTORY_ID);
		
		Dataset datasetForward = new Dataset();
		datasetForward.setId(DATA_ID);
		List<Dataset> datasets = Arrays.asList(datasetForward);
		
		when(historiesClient.createDatasetCollection(eq(HISTORY_ID), any(CollectionDescription.class))).
			thenThrow(new RuntimeException());
		
		galaxyHistory.constructCollectionList(datasets, history);
	}
	
	/**
	 * Tests getting a History.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testGetHistory() throws ExecutionManagerObjectNotFoundException {
		List<History> historyList = new LinkedList<History>();
		historyList.add(history);
		
		when(historiesClient.getHistories()).thenReturn(historyList);
		
		History history = galaxyHistory.findById(HISTORY_ID);
		assertNotNull(history);
		assertEquals(HISTORY_ID, history.getId());
	}
	
	/**
	 * Tests not getting a History.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NoGalaxyHistoryException.class)
	public void testGetNoHistory() throws ExecutionManagerObjectNotFoundException {
		galaxyHistory.findById(INVALID_HISTORY_ID);
	}
	
	/**
	 * Tests checking for the existence of a history.
	 */
	@Test
	public void testHistoryExists() {
		List<History> historyList = new LinkedList<History>();
		historyList.add(history);
		
		when(historiesClient.getHistories()).thenReturn(historyList);
		
		assertTrue(galaxyHistory.exists(HISTORY_ID));
	}
	
	/**
	 * Tests checking for non-existence of a galaxy history.
	 */
	@Test
	public void testNoHistoryExists() {
		assertFalse(galaxyHistory.exists(INVALID_HISTORY_ID));
	}
	
	/**
	 * Tests getting a valid history dataset given a file name and history.
	 * @throws GalaxyDatasetException 
	 */
	@Test
	public void testGetDatasetForFileInHistory() throws GalaxyDatasetException {
		Dataset dataset = new Dataset();
		
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(datasetHistoryContents);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID)).thenReturn(dataset);
		
		assertNotNull(galaxyHistory.getDatasetForFileInHistory(FILENAME, HISTORY_ID));
	}
	
	/**
	 * Tests getting an invalid history dataset given a file name and history.
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testGetDatasetForFileInHistoryNoHistoryContents() throws GalaxyDatasetException {		
		galaxyHistory.getDatasetForFileInHistory(FILENAME, HISTORY_ID);
	}
		
	/**
	 * Tests getting a dataset from a history with multiple matching datasets.
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=GalaxyDatasetException.class)
	public void testGetDatasetForFileInHistoryMultipleDatasets() throws GalaxyDatasetException {
		String filename = dataFile.toFile().getName();
		
		List<HistoryContents> historyContentsList = buildHistoryContentsList(filename, DATA_ID,
				filename, DATA_ID_2);
		
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(historyContentsList);
		
		galaxyHistory.getDatasetForFileInHistory(FILENAME, HISTORY_ID);
	}
	
	/**
	 * Tests getting an invalid history dataset given a file name and history.
	 * @throws GalaxyDatasetException 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testGetDatasetForFileInHistoryNoDataset() throws GalaxyDatasetException {
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(datasetHistoryContents);
		
		galaxyHistory.getDatasetForFileInHistory(FILENAME, HISTORY_ID);
	}
}
