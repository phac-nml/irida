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
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.ClientResponse;

import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistory;

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
		
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance);
		assertEquals(newHistory, galaxyHistory.newHistoryForWorkflow());
	}
	
	@Test
	public void testLibraryDatasetToHistory() {
		HistoryDetails historyDetails = new HistoryDetails();
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(historiesClient.createHistoryDataset(any(String.class),
				any(HistoryDataset.class))).thenReturn(historyDetails);
		
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance);
		assertNotNull(galaxyHistory.libraryDatasetToHistory(libraryFileId, createdHistory));
	}
	
	@Test
	public void testFileToHistorySuccess() {
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance);
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.fileUploadRequest(historyId, "fastqsanger",
				null, dataFile.toFile())).thenReturn(okayResponse);
		
		assertEquals(okayResponse, galaxyHistory.fileToHistory(dataFile, createdHistory));
	}
	
	@Test
	public void testFileToHistoryFail() {
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance);
		History createdHistory = new History();
		createdHistory.setId(historyId);
		
		when(toolsClient.fileUploadRequest(historyId, "fastqsanger",
				null, dataFile.toFile())).thenReturn(invalidResponse);
		
		assertEquals(invalidResponse, galaxyHistory.fileToHistory(dataFile, createdHistory));
	}
}
