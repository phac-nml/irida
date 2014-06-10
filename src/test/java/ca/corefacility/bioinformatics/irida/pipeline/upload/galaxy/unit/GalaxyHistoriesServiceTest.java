package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

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
	@Mock private GalaxySearch galaxySearch;
	
	private final String libraryFileId = "1";
	private final String historyId = "2";
	
	private static final String FILE_TYPE = "fastqsanger";
	
	private Path dataFile;
	
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(
				ClientResponse.Status.FORBIDDEN);
		
		dataFile = Paths.get(this.getClass().getResource("testData1.fastq").toURI());
	}
	
	@Test
	public void testCreateNewHistory() {
		History newHistory = new History();
		
		when(historiesClient.create(any(History.class))).thenReturn(newHistory);
		
		GalaxyHistoriesService galaxyHistory 
			= new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);
		assertEquals(newHistory, galaxyHistory.newHistoryForWorkflow());
	}
	
	@Test
	public void testLibraryDatasetToHistory() {
		HistoryDetails historyDetails = new HistoryDetails();
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(historiesClient.createHistoryDataset(any(String.class),
				any(HistoryDataset.class))).thenReturn(historyDetails);
		
		GalaxyHistoriesService galaxyHistory
			= new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);
		assertNotNull(galaxyHistory.libraryDatasetToHistory(libraryFileId, createdHistory));
	}
	
	@Test
	public void testFileToHistorySuccess() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistoriesService galaxyHistory
			= new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		Dataset dataset = new Dataset();
		createdHistory.setId(historyId);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).thenReturn(okayResponse);
		when(galaxySearch.getDatasetForFileInHistory(filename, createdHistory)).
			thenReturn(dataset);
		
		assertEquals(dataset, galaxyHistory.fileToHistory(dataFile, "fastqsanger", createdHistory));
	}
	
	@Test(expected=UploadException.class)
	public void testFileToHistoryFailUpload() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistoriesService galaxyHistory
			= new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(invalidResponse);
		
		galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory);
	}
	
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testFileToHistoryFailFindDataset() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistoriesService galaxyHistory
			= new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.uploadRequest(any(FileUploadRequest.class))).
			thenReturn(okayResponse);
		when(galaxySearch.getDatasetForFileInHistory(filename, createdHistory)).thenThrow(new GalaxyDatasetNotFoundException());
		
		galaxyHistory.fileToHistory(dataFile, FILE_TYPE, createdHistory);
	}
}
