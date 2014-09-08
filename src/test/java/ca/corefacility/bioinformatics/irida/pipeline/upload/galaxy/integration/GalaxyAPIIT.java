package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrarySearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.ProgressUpdate;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.UploadEventListenerTracker;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset.Source;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for {@link GalaxyUploaderAPI}.  Will use a running instance of Galaxy to test against.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
	IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
	IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
	WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceTestConfig.class,
	RemoteWorkflowServiceTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyAPIIT {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyAPIIT.class);
	
	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private GalaxyUploaderAPI galaxyAPI;
	
	private List<Path> dataFilesSingle;
	private List<Path> dataFilesDouble;

	/**
	 * Sets up variables and files for Galaxy API tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);
		
		setupDataFiles();
	}

	/**
	 * Sets up data files for uploading into Galaxy.
	 * @throws URISyntaxException
	 */
	private void setupDataFiles() throws URISyntaxException {
		Path dataFile1 = Paths.get(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());
		Path dataFile2 = Paths.get(GalaxyAPIIT.class.getResource(
				"testData2.fastq").toURI());

		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);

		dataFilesDouble = new ArrayList<Path>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
	}

	/**
	 * Finds a library by it's id using the given GalaxyInstance
	 * @param libraryID  The library id to search for.
	 * @param galaxyInstance  The GalaxyInstance to user for connections.
	 * @return  A Library object for this id, or null if not found.
	 */
	private Library findLibraryByID(String libraryID,
			GalaxyInstance galaxyInstance) {
		Library actualLibrary = null;
		List<Library> libraries = galaxyInstance.getLibrariesClient()
				.getLibraries();
		for (Library curr : libraries) {
			if (libraryID.equals(curr.getId())) {
				actualLibrary = curr;
			}
		}

		return actualLibrary;
	}

	/**
	 * Finds a library by name with the given GalaxyInstance.
	 * @param libraryName  The name of the library to search for.
	 * @param galaxyInstance  The GalaxyInstance object to use to connect to Galaxy.
	 * @return  A library described by this name.
	 */
	private Library findLibraryByName(UploadProjectName libraryName,
			GalaxyInstance galaxyInstance) {
		Library actualLibrary = null;
		List<Library> libraries = galaxyInstance.getLibrariesClient()
				.getLibraries();
		for (Library curr : libraries) {
			if (libraryName.getName().equals(curr.getName())) {
				actualLibrary = curr;
			}
		}

		return actualLibrary;
	}

	/**
	 * Builds a map of the name of these contents to the objects describing them.
	 * @param libraryContents  The library contents to construct as a map.
	 * @return  A Map describing these library contents.
	 */
	private Map<String, LibraryContent> fileToLibraryContentMap(
			List<LibraryContent> libraryContents) {
		Map<String, LibraryContent> map = new HashMap<String, LibraryContent>();
		for (LibraryContent content : libraryContents) {
			map.put(content.getName(), content);
		}

		return map;
	}

	/**
	 * Given a file library ID, loads a file into a Galaxy history and then
	 * loads the contents of this file into a string.
	 * 
	 * @param testName  The name of the test we are running.
	 * @param filename  The name of the file to load.
	 * @param galaxyInstance  The GalaxyInstance to connect to galaxy.
	 * @param libraryFileId  The id of the library the file is located within.
	 * @return The String with the file contents.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private String getGalaxyFileContents(String testName, String filename,
			GalaxyInstance galaxyInstance, String libraryFileId)
			throws InterruptedException, IOException {

		Dataset dataset = moveLibraryDataToHistoryDataset(testName, filename, galaxyInstance, libraryFileId);
		
		URL url = new URL(dataset.getFullDownloadUrl());

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream stream = con.getInputStream();

		String galaxyFileContents = readFileContentsFromReader(new BufferedReader(
				new InputStreamReader(stream)));

		return galaxyFileContents;
	}
	
	/**
	 * Moves a library data file into a history (so we can check the file type of the data file).
	 * @param testName  The name of the test.
	 * @param filename  The name of the file.
	 * @param galaxyInstance  The connector to Galaxy.
	 * @param libraryFileId  The library id.
	 * @return  A Dataset for the moved file.
	 * @throws InterruptedException
	 */
	private Dataset moveLibraryDataToHistoryDataset(String testName, String filename,
			GalaxyInstance galaxyInstance, String libraryFileId) throws InterruptedException {
		final int totalSecondsWait = 1*60; // 1 minute
		
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();

		History history = new History();
		history.setName(testName);
		History persistedHistory = historiesClient.create(history);
		assertNotNull(persistedHistory);

		HistoryDataset historyDataset = new HistoryDataset();
		historyDataset.setSource(Source.LIBRARY);
		historyDataset.setContent(libraryFileId);
		HistoryDetails historyDetails = historiesClient.createHistoryDataset(
				persistedHistory.getId(), historyDataset);
		assertNotNull(historyDetails);

		String dataId = Util.getIdForFileInHistory(filename,
				persistedHistory.getId(), galaxyInstance);
		assertNotNull(dataId);

		Dataset dataset;
		long timeBefore = System.currentTimeMillis();
		do {
			dataset = historiesClient.showDataset(persistedHistory.getId(),
					dataId);
			assertNotNull(dataset);
			
			long timeAfter = System.currentTimeMillis();
			double deltaSeconds = (timeAfter - timeBefore)/1000.0;
			if (deltaSeconds <= totalSecondsWait) {
				Thread.sleep(2000);
			} else {
				fail("Could not load dataset from file=" + filename + " into history, timeout: "
						+ deltaSeconds + "s > " + totalSecondsWait + "s");
			}
		} while (!"ok".equals(dataset.getState()));
		
		long timeAfter = System.currentTimeMillis();
		logger.debug("Took " + (timeAfter - timeBefore)/1000.0 + "s to load file=" + filename +
				" into Galaxy history");
		
		return dataset;
	}

	/**
	 * Given a reader, reads in the contents of a file to a string.
	 * @param reader  The reader to read from.
	 * @return  The contents of the file within a string.
	 * @throws IOException
	 */
	private String readFileContentsFromReader(BufferedReader reader)
			throws IOException {
		String line;
		String contents = "";
		while ((line = reader.readLine()) != null) {
			contents += line;
		}

		return contents;
	}

	/**
	 * Creates a file to store temporary data.
	 * @return  A Path describing a file to store temporary data.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private Path createTemporaryDataFile() throws IOException,
			URISyntaxException {
		File dataFile1 = new File(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());

		// create temp file so I can delete it afterwards for testing the "link"
		// (dataStorage) option in Galaxy
		File tempDir = File.createTempFile("testData1", "folder");
		tempDir.delete();
		tempDir.mkdir();
		tempDir.deleteOnExit();

		Path dataPathTemp = Paths.get(tempDir.getAbsolutePath(),
				"testData1.fastq");
		File dataFileTemp = dataPathTemp.toFile();
		Files.copy(Paths.get(dataFile1.getAbsolutePath()), dataPathTemp,
				StandardCopyOption.REPLACE_EXISTING);

		return dataFileTemp.toPath();
	}

	/**
	 * Tests creating a library with an invalid name.
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testCreateLibraryInvalidName() throws UploadException {
		GalaxyProjectName invalidLibraryName = new GalaxyProjectName(
				"<a href='http://google.com'>invalid name</a>");
		galaxyAPI.buildGalaxyLibrary(invalidLibraryName,
				localGalaxy.getUser1Name());
	}

	/**
	 * Tests creating a library with an invalid user name.
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testCreateLibraryInvalidUserName() throws UploadException {
		GalaxyProjectName invalidLibraryName = new GalaxyProjectName(
				"testCreateLibraryInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_email");
		galaxyAPI.buildGalaxyLibrary(invalidLibraryName, userEmail);
	}

	/**
	 * Tests creating a Galaxy connection with an invalid admin name.
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testCreateGalaxyAPIInvalidAdmin()
			throws ConstraintViolationException, UploadException {
		new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(),
				localGalaxy.getNonExistentGalaxyAdminName(),
				localGalaxy.getAdminAPIKey());
	}

	/**
	 * Tests connecting to Galaxy with the wrong API key.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testInvalidAPIKey() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(localGalaxy.getAdminAPIKey())) // what are
																	// the
																	// chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}

		new GalaxyUploaderAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(),
				wrongAdminAPIKey);
	}

	/**
	 * Tests creating a library as an admin user in Galaxy.
	 * @throws UploadException
	 */
	@Test
	public void testCreateLibraryAdmin() throws UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testCreateLibraryAdmin");

		Library library = galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getAdminName());
		String libraryID = library.getId();

		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName.getName(), actualLibraryAdmin.getName());

		// make sure regular user cannot see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID,
				localGalaxy.getGalaxyInstanceUser1());
		assertNull(actualLibraryRegularUser);
	}

	/**
	 * Tests creating a library as a regular user in Galaxy.
	 * @throws UploadException
	 */
	@Test
	public void testCreateLibraryRegularUser() throws UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testCreateLibraryRegularUser");

		Library library = galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getUser1Name());
		String libraryID = library.getId();

		// make sure regular user can see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID,
				localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		assertEquals(libraryName.getName(), actualLibraryRegularUser.getName());

		// make sure 2nd regular user cannot see library
		Library actualLibraryRegularUser2 = findLibraryByID(libraryID,
				localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibraryRegularUser2);

		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName.getName(), actualLibraryAdmin.getName());
	}

	/**
	 * Tests creating a library with a user that does not exist in Galaxy.
	 * @throws UploadException
	 */
	@Test(expected = GalaxyUserNotFoundException.class)
	public void testCreateLibraryNonExistentUser() throws UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testCreateLibraryInvalidUser");

		galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getNonExistentGalaxyUserName());
	}

	/**
	 * Tests uploading samples as a regular user in Galaxy.
	 * @throws URISyntaxException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadSampleRegularUser() throws URISyntaxException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleRegularUser");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		GalaxyUploadResult actualUploadResult = galaxyAPI.uploadSamples(
				samples, libraryName, localGalaxy.getUser1Name());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName, actualUploadResult.getLocationName());
		assertEquals(localGalaxy.getUser1Name(),
				actualUploadResult.ownerOfNewLocation());
		assertTrue(actualUploadResult.newLocationCreated());

		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);

		String libraryId = actualLibraryRegularUser.getId();

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceUser1().getLibrariesClient()
				.getLibraryContents(libraryId);
		Map<String, LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapRegularUser.size());

		assertTrue(contentsMapRegularUser.containsKey("/"));
		assertEquals("folder", contentsMapRegularUser.get("/").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapRegularUser.get("/illumina_reads")
				.getType());
		assertTrue(contentsMapRegularUser.containsKey("/references"));
		assertEquals("folder", contentsMapRegularUser.get("/references")
				.getType());
		assertTrue(contentsMapRegularUser
				.containsKey("/illumina_reads/testData"));
		assertEquals("folder",
				contentsMapRegularUser.get("/illumina_reads/testData")
						.getType());
		assertTrue(contentsMapRegularUser
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals(
				"file",
				contentsMapRegularUser.get(
						"/illumina_reads/testData/testData1.fastq").getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMapRegularUser
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());

		// admin should have access to files
		Library actualLibraryAdmin = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibraryAdmin);

		libraryContents = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient().getLibraryContents(libraryId);
		assertNotNull(libraryContents);
		Map<String, LibraryContent> contentsMapAdminUser = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapAdminUser.size());

		assertTrue(contentsMapAdminUser.containsKey("/"));
		assertEquals("folder", contentsMapAdminUser.get("/").getType());
		assertTrue(contentsMapAdminUser.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapAdminUser.get("/illumina_reads")
				.getType());
		assertTrue(contentsMapAdminUser.containsKey("/references"));
		assertEquals("folder", contentsMapAdminUser.get("/references")
				.getType());
		assertTrue(contentsMapAdminUser.containsKey("/illumina_reads/testData"));
		assertEquals("folder",
				contentsMapAdminUser.get("/illumina_reads/testData").getType());
		assertTrue(contentsMapAdminUser
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals(
				"file",
				contentsMapAdminUser.get(
						"/illumina_reads/testData/testData1.fastq").getType());

		// 2nd regular user should not have access to library or files
		Library actualLibraryRegularUser2 = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibraryRegularUser2);

		try {
			libraryContents = localGalaxy.getGalaxyInstanceUser2()
					.getLibrariesClient().getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e) {
		}
	}

	/**
	 * Tests uploading samples as an admin user in Galaxy.
	 * @throws URISyntaxException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadSampleAdminUser() throws URISyntaxException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleAdminUser");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getAdminName()));

		// admin user should have access to files
		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);

		String libraryId = actualLibrary.getId();

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceAdmin().getLibrariesClient()
				.getLibraryContents(actualLibrary.getId());
		Map<String, LibraryContent> contentsMapAdmin = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapAdmin.size());

		assertTrue(contentsMapAdmin.containsKey("/"));
		assertEquals("folder", contentsMapAdmin.get("/").getType());
		assertTrue(contentsMapAdmin.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapAdmin.get("/illumina_reads")
				.getType());
		assertTrue(contentsMapAdmin.containsKey("/references"));
		assertEquals("folder", contentsMapAdmin.get("/references")
				.getType());
		assertTrue(contentsMapAdmin
				.containsKey("/illumina_reads/testData"));
		assertEquals("folder",
				contentsMapAdmin.get("/illumina_reads/testData")
						.getType());
		assertTrue(contentsMapAdmin
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals(
				"file",
				contentsMapAdmin.get(
						"/illumina_reads/testData/testData1.fastq").getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceAdmin(), contentsMapAdmin
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());

		// regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser1());
		assertNull(actualLibrary);

		try {
			libraryContents = localGalaxy.getGalaxyInstanceUser1()
					.getLibrariesClient().getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e) {
		}

		// 2nd regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibrary);

		try {
			libraryContents = localGalaxy.getGalaxyInstanceUser2()
					.getLibrariesClient().getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e) {
		}
	}

	/**
	 * Tests uploading samples to a remote Galaxy instance (no linking of files on the filesystem).
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleNoLink() throws URISyntaxException,
			InterruptedException, IOException, UploadException {
		galaxyAPI.setDataStorage(Uploader.DataStorage.REMOTE);

		Path dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFileTemp1);

		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleNoLink");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name()));

		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);

		String libraryId = actualLibraryRegularUser.getId();

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceUser1().getLibrariesClient()
				.getLibraryContents(libraryId);
		Map<String, LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);

		assertTrue(contentsMapRegularUser
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals(
				"file",
				contentsMapRegularUser.get(
						"/illumina_reads/testData/testData1.fastq").getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMapRegularUser
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());

		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(
				libraryName.getName(), "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMapRegularUser
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());

		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(Files
				.newBufferedReader(dataFileTemp1, Charset.defaultCharset()));

		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);

		// delete original file
		assertTrue(dataFileTemp1.toFile().delete());

		// file contents should be the same (no link)
		galaxyFileContents = getGalaxyFileContents(
				libraryName + "Deleted",
				"testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get(
						"/illumina_reads/testData/testData1.fastq").getId());
		assertEquals(fileSystemFileContents, galaxyFileContents);
	}

	/**
	 * Tests uploading samples to a local Galaxy instance (linking files on the same filesystem).
	 * @throws URISyntaxException
	 * @throws LibraryUploadException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleLink() throws URISyntaxException,
			LibraryUploadException, InterruptedException, IOException,
			UploadException {
		galaxyAPI.setDataStorage(Uploader.DataStorage.LOCAL);

		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleLink");

		Path dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFileTemp1);

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name()));

		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);

		String libraryId = actualLibraryRegularUser.getId();

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceUser1().getLibrariesClient()
				.getLibraryContents(libraryId);
		Map<String, LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);

		assertTrue(contentsMapRegularUser
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals(
				"file",
				contentsMapRegularUser.get(
						"/illumina_reads/testData/testData1.fastq").getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMapRegularUser
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());

		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(
				libraryName.getName(), "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMapRegularUser
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());

		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(Files
				.newBufferedReader(dataFileTemp1, Charset.defaultCharset()));

		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);

		// delete original file
		assertTrue(dataFileTemp1.toFile().delete());

		// should get an error when attempting to download file
		try {
			galaxyFileContents = getGalaxyFileContents(
					libraryName + "Deleted",
					"testData1.fastq",
					localGalaxy.getGalaxyInstanceUser1(),
					contentsMapRegularUser.get(
							"/illumina_reads/testData/testData1.fastq").getId());
			fail("No exception when attempting to download");
		} catch (Exception e) {
		}
	}

	/**
	 * Tests connecting to Galaxy with the wrong URL.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testGalaxyWrongAddress() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		new GalaxyUploaderAPI(localGalaxy.getInvalidGalaxyURL(),
				localGalaxy.getAdminName(), localGalaxy.getAdminAPIKey());
	}

	/**
	 * Tests uploading files to Galaxy with a non existent user.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = GalaxyUserNotFoundException.class)
	public void testUploadSampleWrongUser() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleWrongUser");
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getNonExistentGalaxyUserName());
	}

	/**
	 * Tests uploading files to a user with an incorrectly formatted name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidUserName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_user");
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyAPI.uploadSamples(samples, libraryName, userEmail);
	}

	/**
	 * Tests uploading a sample with an incorrectly formatted name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidSampleName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleInvalidSampleName");
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"<invalidSample>"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name());
	}

	/**
	 * Tests uploading sample to a library with an incorrectly formatted name.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUploadSampleInvalidLibraryName() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName("<invalidLibrary>");
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser1Name());
	}

	/**
	 * Tests uploading a sample with multiple files.
	 * @throws URISyntaxException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleMultipleFile() throws URISyntaxException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleMultipleFile");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesDouble);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getAdminName()));

		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceAdmin().getLibrariesClient()
				.getLibraryContents(actualLibrary.getId());
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(6, contentsMap.size());

		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData1.fastq")
						.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData2.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData2.fastq")
						.getType());
	}

	/**
	 * Given a list of contents in a library, counts the occurance of the passed path.
	 * @param contents
	 * @param folderPaths
	 * @return
	 */
	private int countNumberOfFolderPaths(List<LibraryContent> contents,
			String folderPaths) {
		int count = 0;
		for (LibraryContent c : contents) {
			if (folderPaths.equals(c.getName())) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Tests uploading samples to an already existing library.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadSampleToExistingLibrary() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException, InterruptedException {
		GalaxyLibrarySearch galaxySearchAdmin = new GalaxyLibrarySearch(
				localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyRoleSearch galaxyRoleSearchAdmin = new GalaxyRoleSearch(
				localGalaxy.getGalaxyInstanceAdmin().getRolesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyLibrarySearch galaxySearchUser1 = new GalaxyLibrarySearch(
				localGalaxy.getGalaxyInstanceUser1().getLibrariesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyLibraryBuilder galaxyLibrary = new GalaxyLibraryBuilder(
				localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient(),
				galaxyRoleSearchAdmin, localGalaxy.getGalaxyURL());
		UploadResult expectedUploadResult;

		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleToExistingSampleFolder");

		// build data library structure with no data in it
		Library returnedLibrary = galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getUser1Name());
		String libraryId = returnedLibrary.getId();
		assertNotNull(libraryId);

		// build expected upload result
		expectedUploadResult = new GalaxyUploadResult(returnedLibrary,
				libraryName, null, localGalaxy.getGalaxyURL().toString());

		// build initial folders within library
		Library library = galaxySearchUser1.findById(libraryId);
		assertNotNull(library);
		LibraryFolder illuminaFolder = galaxyLibrary.createLibraryFolder(
				library, new GalaxyFolderName("illumina_reads"));
		assertNotNull(illuminaFolder);
		LibraryFolder sampleFolder = galaxyLibrary.createLibraryFolder(library,
				illuminaFolder, new GalaxyFolderName("testData"));
		assertNotNull(sampleFolder);
		LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(
				library, new GalaxyFolderName("references"));
		assertNotNull(referencesFolder);

		// user 1 should have access to library
		List<Library> libraries = galaxySearchUser1
				.findByName(libraryName);
		assertEquals("The number of libraries with name " + libraryName
				+ " is not one", 1, libraries.size());

		// admin should have access to library
		libraries = galaxySearchAdmin.findByName(libraryName);
		assertEquals("The number of libraries with name " + libraryName
				+ " is not one", 1, libraries.size());

		// all folders should have been created for library
		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceAdmin().getLibrariesClient()
				.getLibraryContents(library.getId());
		int sampleFolderCount = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads/testData");
		assertEquals("The number of testData folders is not one", 1,
				sampleFolderCount);
		int illuminaReadsFolderCount = countNumberOfFolderPaths(
				libraryContents, "/illumina_reads");
		assertEquals("The number of illumina_reads folders is not one", 1,
				illuminaReadsFolderCount);
		int referencesFolderCount = countNumberOfFolderPaths(libraryContents,
				"/references");
		assertEquals("The number of references folders is not one", 1,
				referencesFolderCount);

		// attempt to upload to this above data library, should not create
		// duplicate library nor duplicate sample folder
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesDouble);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		UploadResult actualUploadResult = galaxyAPI.uploadSamples(samples,
				libraryName, localGalaxy.getUser1Name());
		assertEquals(expectedUploadResult, actualUploadResult);
		assertFalse(actualUploadResult.newLocationCreated());

		// user 1 should have access to library
		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibrary);

		// library contents should be updated
		libraryContents = localGalaxy.getGalaxyInstanceUser1()
				.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(6, contentsMap.size());
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData1.fastq")
						.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData2.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData2.fastq")
						.getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMap
						.get("/illumina_reads/testData/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());
		
		Dataset datasetData2 = moveLibraryDataToHistoryDataset(libraryName.getName() + "2", "testData2.fastq",
				localGalaxy.getGalaxyInstanceUser1(), contentsMap
						.get("/illumina_reads/testData/testData2.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData2.getDataType());

		// no duplicate folders or libraries for user1
		libraries = galaxySearchUser1.findByName(libraryName);
		assertEquals("The number of libraries with name " + libraryName
				+ " is not one", 1, libraries.size());
		sampleFolderCount = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads/testData");
		assertEquals("The number of testData folders is not one", 1,
				sampleFolderCount);
		illuminaReadsFolderCount = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads");
		assertEquals("The number of illumina_reads folders is not one", 1,
				illuminaReadsFolderCount);
		referencesFolderCount = countNumberOfFolderPaths(libraryContents,
				"/references");
		assertEquals("The number of references folders is not one", 1,
				referencesFolderCount);

		// no duplicate libraries for admin
		libraries = galaxySearchAdmin.findByName(libraryName);
		assertEquals("The number of libraries with name " + libraryName
				+ " is not one", 1, libraries.size());
	}

	/**
	 * Tests uploading samples to an existing library as different regular Galaxy users.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleToExistingLibraryDifferentUsers()
			throws URISyntaxException, MalformedURLException,
			ConstraintViolationException, UploadException {
		GalaxyLibrarySearch galaxySearchAdmin = new GalaxyLibrarySearch(
				localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyLibrarySearch galaxySearchUser1 = new GalaxyLibrarySearch(
				localGalaxy.getGalaxyInstanceUser1().getLibrariesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyLibrarySearch galaxySearchUser2 = new GalaxyLibrarySearch(
				localGalaxy.getGalaxyInstanceUser2().getLibrariesClient(),
				localGalaxy.getGalaxyURL());

		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleToExistingLibraryDifferentUsers");

		// build data library structure with no data in it
		Library returnedLibrary = galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getUser1Name());
		String libraryId = returnedLibrary.getId();
		assertNotNull(libraryId);

		// library should be visible to user 1 and admin
		assertNotNull(galaxySearchUser1.findById(libraryId));
		assertEquals(1, galaxySearchUser1.findByName(libraryName)
				.size());
		assertNotNull(galaxySearchAdmin.findById(libraryId));
		assertEquals(1, galaxySearchAdmin.findByName(libraryName)
				.size());

		// library should not be visible to user 2
		try {
			galaxySearchUser2.findById(libraryId);
			fail("Library found for user 2");
		} catch (NoLibraryFoundException e) {}
		
		try {
			galaxySearchUser2.findByName(libraryName);
			fail("Library found for user 2");
		} catch (NoLibraryFoundException e) {}

		// there should be nothing in this library
		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceUser1().getLibrariesClient()
				.getLibraryContents(libraryId);
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(1, contentsMap.size());
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());

		// attempt to upload to this above data library as a different user
		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesDouble);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser2Name()));

		// library should be visible to user 1 and admin
		assertNotNull(galaxySearchUser1.findById(libraryId));
		assertEquals(1, galaxySearchUser1.findByName(libraryName)
				.size());
		assertNotNull(galaxySearchAdmin.findById(libraryId));
		assertEquals(1, galaxySearchAdmin.findByName(libraryName)
				.size());

		// library should not be visible to user 2 (user 2 shared with user 1,
		// but did not gain access)
		try {
			galaxySearchUser2.findById(libraryId);
			fail("Library found for user 2");
		} catch (NoLibraryFoundException e) {}
		
		try {
			galaxySearchUser2.findByName(libraryName);
			fail("Library found for user 2");
		} catch (NoLibraryFoundException e) {}

		// library contents should be updated
		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		libraryContents = localGalaxy.getGalaxyInstanceUser1()
				.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(6, contentsMap.size());
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData1.fastq")
						.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData2.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData2.fastq")
						.getType());
	}

	/**
	 * Tests uploading a sample where one file already is uploaded in Galaxy.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleOneFileAlreadyExists()
			throws URISyntaxException, MalformedURLException,
			ConstraintViolationException, UploadException {
		GalaxyProjectName libraryName = new GalaxyProjectName(
				"testUploadSampleOneFileAlreadyExists");

		UploadSample galaxySample = new GalaxySample(new GalaxyFolderName(
				"testData"), dataFilesSingle);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		String localGalaxyURL = localGalaxy
				.getGalaxyURL()
				.toString()
				.substring(0,
						localGalaxy.getGalaxyURL().toString().length() - 1); // remove
																				// trailing
																				// '/'

		GalaxyUploadResult actualUploadResult = galaxyAPI.uploadSamples(
				samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName, actualUploadResult.getLocationName());
		assertEquals(new URL(localGalaxyURL + "/library"),
				actualUploadResult.getDataLocation());
		assertTrue(actualUploadResult.newLocationCreated());

		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + "/" + GalaxyUploadResult.LIBRARY_API_BASE + actualUploadResult.getLibraryId()),
				actualUploadResult.getLibraryAPIURL());

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceAdmin().getLibrariesClient()
				.getLibraryContents(actualLibrary.getId());
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMap.size());

		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData1.fastq")
						.getType());

		// now attempt to upload dataFilesDouble with two files, only one file
		// should upload
		galaxySample = new GalaxySample(new GalaxyFolderName("testData"),
				dataFilesDouble);
		samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);

		// make sure both libraries are the same
		actualUploadResult = galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName, actualUploadResult.getLocationName());
		assertEquals(new URL(localGalaxyURL + "/library"),
				actualUploadResult.getDataLocation());
		assertFalse(actualUploadResult.newLocationCreated());

		actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + "/" + GalaxyUploadResult.LIBRARY_API_BASE + actualUploadResult.getLibraryId()),
				actualUploadResult.getLibraryAPIURL());

		libraryContents = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(6, contentsMap.size());

		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData1.fastq")
						.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData/testData2.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData/testData2.fastq")
						.getType());

		// make sure only 1 instance of testData2 exists in library
		int countTestData2 = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads/testData/testData2.fastq");
		assertEquals("More than one copy of testData2.fastq was uploaded", 1,
				countTestData2);

		// make sure only 1 instance of testData sample folder exists in library
		int countTestDataFolder = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads/testData");
		assertEquals(
				"More than one copy of /illumina_reads/testData was created",
				1, countTestDataFolder);

		// make sure only 1 instance of illumina_reads folder exists in library
		int countIlluminaReadsFolder = countNumberOfFolderPaths(
				libraryContents, "/illumina_reads");
		assertEquals("More than one copy of /illumina_reads was created", 1,
				countIlluminaReadsFolder);

		// make sure only 1 instance of references folder exists in library
		int countReferencesFolder = countNumberOfFolderPaths(libraryContents,
				"/illumina_reads");
		assertEquals("More than one copy of /references was created", 1,
				countReferencesFolder);
	}
	
	/**
	 * Tests progress listener when uploading multiple samples to Galaxy.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadProgressListener() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName("testUploadProgressListener");
		
		GalaxyFolderName sample1Name = new GalaxyFolderName("testData1");
		GalaxyFolderName sample2Name = new GalaxyFolderName("testData2");

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(sample1Name, dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(sample2Name, dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);

		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		galaxyAPI.addUploadEventListener(eventListener);
		assertEquals(0, eventListener.getProgressUpdates().size());
		
		galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		
		assertEquals(2, eventListener.getProgressUpdates().size());
		assertTrue(eventListener.getProgressUpdates().contains(new ProgressUpdate(2,0,sample1Name)));
		assertTrue(eventListener.getProgressUpdates().contains(new ProgressUpdate(2,1,sample2Name)));
	}

	/**
	 * Tests uploading multiple samples to Galaxy.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 * @throws InterruptedException 
	 */
	@Test
	public void testUploadSamples() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException, InterruptedException {
		GalaxyProjectName libraryName = new GalaxyProjectName("testUploadSamples");
		String localGalaxyURL = localGalaxy
				.getGalaxyURL()
				.toString()
				.substring(0,
						localGalaxy.getGalaxyURL().toString().length() - 1); // remove trailing '/'

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyFolderName(
				"testData1"), dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(new GalaxyFolderName(
				"testData2"), dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);

		GalaxyUploadResult actualUploadResult = galaxyAPI.uploadSamples(
				samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName, actualUploadResult.getLocationName());
		assertEquals(new URL(localGalaxyURL + "/library"),
				actualUploadResult.getDataLocation());
		assertTrue(actualUploadResult.newLocationCreated());

		Library actualLibrary = findLibraryByName(libraryName,
				localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + "/" + GalaxyUploadResult.LIBRARY_API_BASE + actualUploadResult.getLibraryId()),
				actualUploadResult.getLibraryAPIURL());

		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceAdmin().getLibrariesClient()
				.getLibraryContents(actualLibrary.getId());
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(7, contentsMap.size());

		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData1"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData1")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData1/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData1/testData1.fastq")
						.getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData2"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData2")
				.getType());
		assertTrue(contentsMap
				.containsKey("/illumina_reads/testData2/testData1.fastq"));
		assertEquals("file",
				contentsMap.get("/illumina_reads/testData2/testData1.fastq")
						.getType());
		
		// test out correct file type
		Dataset datasetData1 = moveLibraryDataToHistoryDataset(libraryName.getName() + "1", "testData1.fastq",
				localGalaxy.getGalaxyInstanceAdmin(), contentsMap
						.get("/illumina_reads/testData1/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData1.getDataType());
		
		Dataset datasetData2 = moveLibraryDataToHistoryDataset(libraryName.getName() + "2", "testData1.fastq",
				localGalaxy.getGalaxyInstanceAdmin(), contentsMap
						.get("/illumina_reads/testData2/testData1.fastq")
						.getId());
		
		assertEquals("fastqsanger",datasetData2.getDataType());
	}
	
	/**
	 * Tests case of GalaxyAPI properly connected.
	 */
	@Test
	public void testIsConnected() {
		assertTrue(galaxyAPI.isConnected());
	}
	
	
	/**
	 * Builds a thread used to send http responses back to the user.
	 * @return  A thread which will send a specific http response back to the user.
	 * @throws IOException
	 */
	private Thread buildHttpServerInvalidHttpCode(URL invalidGalaxyURL) throws IOException {
		final int unusedPort = invalidGalaxyURL.getPort();
		
		Thread serverThread = new Thread(){
			
			private int NUMBER_CONNECTIONS = 2;
			
			@Override
			public void run() {
				super.run();
				
				ServerSocket server;
				try {
					server = new ServerSocket(unusedPort);
					for (int i = 0; i < NUMBER_CONNECTIONS; i++) {
						Socket connection = server.accept();
						
						String response = 
								
								"HTTP/1.1 403 Forbidden\r\n" +
								"\r\n";
						
						DataOutputStream output = new DataOutputStream(connection.getOutputStream());
						output.write(response.getBytes());
						output.close();
					}
					
					server.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		return serverThread;
	}
	
	/**
	 * Tests case of GalaxyAPI not connected and getting an invalid http status code.
	 * @throws GalaxyConnectException 
	 * @throws ConstraintViolationException 
	 * @throws IOException 
	 */
	@Test(expected=GalaxyConnectException.class)
	public void testNotConnectedInvalidHttpStatus() throws ConstraintViolationException, GalaxyConnectException, IOException {
		Thread serverThread = buildHttpServerInvalidHttpCode(localGalaxy.getTestGalaxyURL());
		serverThread.start();
		
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(
				localGalaxy.getTestGalaxyURL().toString(), "1");
		new GalaxyUploaderAPI(galaxyInstance, new GalaxyAccountEmail("a@b.c"));
	}
}
