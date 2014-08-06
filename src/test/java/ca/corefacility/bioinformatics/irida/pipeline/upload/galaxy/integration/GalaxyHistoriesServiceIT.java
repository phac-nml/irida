package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;

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
	private Path dataFile2;
	private Path dataFileInvalid;
	
	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;
	private static final InputFileType INVALID_FILE_TYPE = null;

	/**
	 * Sets up files for history tests.
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		setupDataFiles();
		
		GalaxyInstance galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		HistoriesClient historiesClient = galaxyInstanceAdmin.getHistoriesClient();
		ToolsClient toolsClient = galaxyInstanceAdmin.getToolsClient();
		galaxyHistory = new GalaxyHistoriesService(historiesClient, toolsClient);

	}
	
	/**
	 * Sets up data files for uploading into Galaxy.
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	private void setupDataFiles() throws URISyntaxException, IOException {
		dataFile = Paths.get(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());
		
		dataFile2 = Paths.get(GalaxyAPIIT.class.getResource(
				"testData2.fastq").toURI());
		
		File invalidFile = File.createTempFile("galaxy-test", ".fastq");
		invalidFile.delete();
		dataFileInvalid = invalidFile.toPath();
		
		assertFalse(dataFileInvalid.toFile().exists());
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
	 * Tests direct upload of a list of files to a Galaxy history.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test
	public void testUploadFilesListToHistory() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		String filename1 = dataFile.toFile().getName();
		String filename2 = dataFile2.toFile().getName();
		List<Path> dataFiles = new LinkedList<>();
		dataFiles.add(dataFile);
		dataFiles.add(dataFile2);
		
		List<Dataset> datasets = galaxyHistory.uploadFilesListToHistory(dataFiles, FILE_TYPE, history);
		assertNotNull(datasets);
		assertEquals(2, datasets.size());
		
		Dataset dataset1 = datasets.get(0);
		String dataId1 = Util.getIdForFileInHistory(filename1, history.getId(),
				localGalaxy.getGalaxyInstanceAdmin());
		assertEquals(dataId1, dataset1.getId());
		
		Dataset dataset2 = datasets.get(1);
		String dataId2 = Util.getIdForFileInHistory(filename2, history.getId(),
				localGalaxy.getGalaxyInstanceAdmin());
		assertEquals(dataId2, dataset2.getId());
	}
	
	/**
	 * Tests direct upload of a list of files to a Galaxy history (fail to upload).
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test(expected=IllegalStateException.class)
	public void testUploadFilesListToHistoryFail() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		List<Path> dataFiles = new LinkedList<>();
		dataFiles.add(dataFile);
		dataFiles.add(dataFileInvalid);
		
		galaxyHistory.uploadFilesListToHistory(dataFiles, FILE_TYPE, history);
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
	 * Tests direct upload of an invalid (not found) file to a Galaxy history.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test(expected=IllegalStateException.class)
	public void testInvalidFileToHistory() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		galaxyHistory.fileToHistory(dataFileInvalid, FILE_TYPE, history);
	}
	
	/**
	 * Tests failure to upload file to history due to invalid file type.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 */
	@Test(expected=NullPointerException.class)
	public void testFileToHistoryInvalidType() throws UploadException, GalaxyDatasetNotFoundException {
		History history = galaxyHistory.newHistoryForWorkflow();
		galaxyHistory.fileToHistory(dataFile, INVALID_FILE_TYPE, history);
	}
}
