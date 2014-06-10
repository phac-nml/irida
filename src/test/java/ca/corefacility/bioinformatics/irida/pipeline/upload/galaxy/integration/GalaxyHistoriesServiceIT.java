package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

/**
 * Tests for building Galaxy histories.
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
public class GalaxyHistoriesServiceIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private GalaxyHistoriesService galaxyHistory;
	
	private Path dataFile;
	
	private static final String FILE_TYPE = "fastqsanger";
	private static final String INVALID_FILE_TYPE = "invalid";

	/**
	 * Sets up files for history tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		setupDataFiles();
		
		GalaxyInstance galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		HistoriesClient historiesClient = galaxyInstanceAdmin.getHistoriesClient();
		ToolsClient toolsClient = galaxyInstanceAdmin.getToolsClient();
		GalaxySearch galaxySearch = new GalaxySearch(galaxyInstanceAdmin);
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient, galaxySearch);

	}
	
	/**
	 * Sets up data files for uploading into Galaxy.
	 * @throws URISyntaxException
	 */
	private void setupDataFiles() throws URISyntaxException {
		dataFile = Paths.get(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());
	}
	
	/**
	 * Tests constructing new history for a workflow.
	 */
	@Test
	public void testNewHistoryForWorkflow() {
		GalaxyInstance galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		HistoriesClient historiesClient = galaxyInstanceAdmin.getHistoriesClient();
		
		History actualHistory = galaxyHistory.newHistoryForWorkflow();
		assertNotNull(actualHistory);
		
		// make sure history is within Galaxy
		History foundHistory = null;
		for (History h : historiesClient.getHistories()) {
			if (h.getId().equals(actualHistory.getId())) {
				foundHistory = h;
			}
		}
		
		assertNotNull(foundHistory);
	}
	
	/**
	 * Tests direct upload of a file to a Galaxy history.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test
	public void testFileToHistory() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		String filename = dataFile.toFile().getName();
		Dataset actualDataset = galaxyHistory.fileToHistory(dataFile, FILE_TYPE, history);
		assertNotNull(actualDataset);
		
		String dataId = Util.getIdForFileInHistory(filename, history.getId(),
				localGalaxy.getGalaxyInstanceAdmin());
		assertEquals(dataId, actualDataset.getId());
	}
	
	/**
	 * Tests failure to upload file to history due to invalid file type.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test(expected=UploadException.class)
	public void testFileToHistoryInvalidType() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		galaxyHistory.fileToHistory(dataFile, INVALID_FILE_TYPE, history);
	}
}
