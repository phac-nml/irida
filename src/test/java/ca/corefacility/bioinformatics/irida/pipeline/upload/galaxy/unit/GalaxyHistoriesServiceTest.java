package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;

/**
 * Tests the GalaxyHistory class
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistoriesServiceTest {

	@Mock private HistoriesClient historiesClient;
	@Mock private ToolsClient toolsClient;
	@Mock private ClientResponse invalidResponse;
	@Mock private ClientResponse okayResponse;
	@Mock private GalaxyLibrarySearch galaxySearch;
	
	private GalaxyHistoriesService galaxyHistory;
	
	private final String libraryFileId = "1";
	
	private static final String FILE_TYPE = "fastqsanger";
	private static final String HISTORY_ID = "1";
	private static final String INVALID_HISTORY_ID = "2";
	
	private static final String FILENAME = "filename";
	private static final String DATA_ID = "2";
	
	private List<HistoryContents> datasetHistoryContents;
	private History history;
	
	private Path dataFile;
	
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
		
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient);
		
		dataFile = Paths.get(this.getClass().getResource("testData1.fastq").toURI());

		history = new History();
		history.setId(HISTORY_ID);

		datasetHistoryContents = buildHistoryContentsList(FILENAME, DATA_ID);
	}
	
	private List<HistoryContents> buildHistoryContentsList(String filename, String id) {
		HistoryContents datasetHistoryContent = new HistoryContents();
		datasetHistoryContent.setName(filename);
		datasetHistoryContent.setId(id);
		datasetHistoryContents = new ArrayList<HistoryContents>();
		datasetHistoryContents.add(datasetHistoryContent);
		
		return Arrays.asList(datasetHistoryContent);
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
	 * @throws GalaxyDatasetNotFoundException
	 * @throws UploadException
	 */
	@Test
	public void testFileToHistorySuccess() throws GalaxyDatasetNotFoundException, UploadException {
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		Dataset dataset = new Dataset();
		createdHistory.setId(HISTORY_ID);
		List<HistoryContents> historyContentsList = buildHistoryContentsList(filename, DATA_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).thenReturn(okayResponse);
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(historyContentsList);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID)).thenReturn(dataset);
		
		assertEquals(dataset, galaxyHistory.fileToHistory(dataFile, "fastqsanger", createdHistory));
	}
	
	/**
	 * Tests failing to upload a file to a history.
	 * @throws GalaxyDatasetNotFoundException
	 * @throws UploadException
	 */
	@Test(expected=UploadException.class)
	public void testFileToHistoryFailUpload() throws GalaxyDatasetNotFoundException, UploadException {
		History createdHistory = new History();
		createdHistory.setId(HISTORY_ID);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(invalidResponse);
		
		galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory);
	}
	
	/**
	 * Tests failing to find a Dataset object after uploading a file to a history.
	 * @throws GalaxyDatasetNotFoundException
	 * @throws UploadException
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testFileToHistoryFailFindDataset() throws GalaxyDatasetNotFoundException, UploadException {
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
	 * Tests getting a History.
	 * @throws NoGalaxyHistoryException 
	 */
	@Test
	public void testGetHistory() throws NoGalaxyHistoryException {
		List<History> historyList = new LinkedList<History>();
		historyList.add(history);
		
		when(historiesClient.getHistories()).thenReturn(historyList);
		
		History history = galaxyHistory.getGalaxyHistory(HISTORY_ID);
		assertNotNull(history);
		assertEquals(HISTORY_ID, history.getId());
	}
	
	/**
	 * Tests not getting a History.
	 * @throws NoGalaxyHistoryException 
	 */
	@Test(expected=NoGalaxyHistoryException.class)
	public void testGetNoHistory() throws NoGalaxyHistoryException {
		galaxyHistory.getGalaxyHistory(INVALID_HISTORY_ID);
	}
	
	
	/**
	 * Tests getting a valid history dataset given a file name and history.
	 * @throws GalaxyDatasetNotFoundException 
	 */
	@Test
	public void testGetDatasetForFileInHistory() throws GalaxyDatasetNotFoundException {
		Dataset dataset = new Dataset();
		
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(datasetHistoryContents);
		when(historiesClient.showDataset(HISTORY_ID, DATA_ID)).thenReturn(dataset);
		
		assertNotNull(galaxyHistory.getDatasetForFileInHistory(FILENAME, history));
	}
	
	/**
	 * Tests getting an invalid history dataset given a file name and history.
	 * @throws GalaxyDatasetNotFoundException 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testGetDatasetForFileInHistoryNoHistoryContents() throws GalaxyDatasetNotFoundException {		
		galaxyHistory.getDatasetForFileInHistory(FILENAME, history);
	}
	
	
	/**
	 * Tests getting an invalid history dataset given a file name and history.
	 * @throws GalaxyDatasetNotFoundException 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testGetDatasetForFileInHistoryNoDataset() throws GalaxyDatasetNotFoundException {
		when(historiesClient.showHistoryContents(HISTORY_ID)).thenReturn(datasetHistoryContents);
		
		galaxyHistory.getDatasetForFileInHistory(FILENAME, history);
	}
}
