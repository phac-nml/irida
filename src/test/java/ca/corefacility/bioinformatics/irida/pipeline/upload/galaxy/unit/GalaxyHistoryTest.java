package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistory;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

/**
 * Tests the GalaxyHistory class
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistoryTest {

	@Mock private GalaxyInstance galaxyInstance;
	@Mock private HistoriesClient historiesClient;
	@Mock private ToolsClient toolsClient;
	@Mock private ClientResponse invalidResponse;
	@Mock private ClientResponse okayResponse;
	@Mock private GalaxySearch galaxySearch;
	
	private final String libraryFileId = "1";
	private final String historyId = "2";
	
	private Path dataFile;
	
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		when(galaxyInstance.getHistoriesClient()).thenReturn(historiesClient);
		when(galaxyInstance.getToolsClient()).thenReturn(toolsClient);
		
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
		
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		assertEquals(newHistory, galaxyHistory.newHistoryForWorkflow());
	}
	
	@Test
	public void testLibraryDatasetToHistory() {
		HistoryDetails historyDetails = new HistoryDetails();
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(historiesClient.createHistoryDataset(any(String.class),
				any(HistoryDataset.class))).thenReturn(historyDetails);
		
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		assertNotNull(galaxyHistory.libraryDatasetToHistory(libraryFileId, createdHistory));
	}
	
	@Test
	public void testFileToHistorySuccess() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		Dataset dataset = new Dataset();
		createdHistory.setId(historyId);
		
		when(toolsClient.fileUploadRequest(historyId, "fastqsanger",
				null, dataFile.toFile())).thenReturn(okayResponse);
		when(galaxySearch.getDatasetForFileInHistory(filename, createdHistory)).thenReturn(dataset);
		
		assertEquals(dataset, galaxyHistory.fileToHistory(dataFile, createdHistory));
	}
	
	@Test(expected=UploadException.class)
	public void testFileToHistoryFailUpload() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.fileUploadRequest(historyId, "fastqsanger",
				null, dataFile.toFile())).thenReturn(invalidResponse);
		
		galaxyHistory.fileToHistory(dataFile, createdHistory);
	}
	
	@Test(expected=GalaxyDatasetNotFoundException.class)
	public void testFileToHistoryFailFindDataset() throws GalaxyDatasetNotFoundException, UploadException {
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		String filename = dataFile.toFile().getName();
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.fileUploadRequest(historyId, "fastqsanger",
				null, dataFile.toFile())).thenReturn(okayResponse);
		when(galaxySearch.getDatasetForFileInHistory(filename, createdHistory)).thenThrow(new GalaxyDatasetNotFoundException());
		
		galaxyHistory.fileToHistory(dataFile, createdHistory);
	}
}
