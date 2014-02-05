package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.LocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxySearch;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset.Source;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Integration tests for {@link GalaxyAPI}.  Will use a running instance of Galaxy to test against.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, LocalGalaxyConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyAPIIT {
	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private GalaxyAPI galaxyAPI;

	private List<Path> dataFilesSingle;
	private List<Path> dataFilesDouble;

	@Before
	public void setup() throws URISyntaxException {
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
	private Library findLibraryByName(UploadObjectName libraryName,
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

		String dataId = getIdForFileInHistory(filename,
				persistedHistory.getId(), galaxyInstance);
		assertNotNull(dataId);

		Dataset dataset;
		do {
			dataset = historiesClient.showDataset(persistedHistory.getId(),
					dataId);
			assertNotNull(dataset);
			Thread.sleep(2000);
		} while (!"ok".equals(dataset.getState()));

		URL url = new URL(dataset.getFullDownloadUrl());

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream stream = con.getInputStream();

		String galaxyFileContents = readFileContentsFromReader(new BufferedReader(
				new InputStreamReader(stream)));

		return galaxyFileContents;
	}

	/**
	 * Given a file within a Galaxy history, finds the id of that file.
	 * @param filename  The name of the file within a history.
	 * @param historyId  The id of the history.
	 * @param galaxyInstance  The GalaxyInstance to use for connections.
	 * @return  The id of the file in this history, or null if no such file.
	 */
	private String getIdForFileInHistory(String filename, String historyId,
			GalaxyInstance galaxyInstance) {
		String dataId = null;
		List<HistoryContents> historyContentsList = galaxyInstance
				.getHistoriesClient().showHistoryContents(historyId);

		for (HistoryContents contents : historyContentsList) {
			if (filename.equals(contents.getName())) {
				dataId = contents.getId();
				break;
			}
		}

		return dataId;
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
		GalaxyObjectName invalidLibraryName = new GalaxyObjectName(
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
		GalaxyObjectName invalidLibraryName = new GalaxyObjectName(
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
		new GalaxyAPI(localGalaxy.getGalaxyURL(),
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

		new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(),
				wrongAdminAPIKey);
	}

	/**
	 * Tests creating a library as an admin user in Galaxy.
	 * @throws UploadException
	 */
	@Test
	public void testCreateLibraryAdmin() throws UploadException {
		GalaxyObjectName libraryName = new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testCreateLibraryInvalidUser");

		galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getNonExistentGalaxyUserName());
	}

	/**
	 * Tests uploading samples as a regular user in Galaxy.
	 * @throws URISyntaxException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSampleRegularUser() throws URISyntaxException,
			UploadException {
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleRegularUser");

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
	 */
	@Test
	public void testUploadSampleAdminUser() throws URISyntaxException,
			UploadException {
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleAdminUser");

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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

		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleNoLink");

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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

		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleLink");

		Path dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFileTemp1);

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		new GalaxyAPI(localGalaxy.getInvalidGalaxyURL(),
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleWrongUser");
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_user");
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleInvalidSampleName");
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName("<invalidLibrary>");
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleMultipleFile");

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
	 */
	@Test
	public void testUploadSampleToExistingLibrary() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException {
		GalaxySearch galaxySearchAdmin = new GalaxySearch(
				localGalaxy.getGalaxyInstanceAdmin());
		GalaxySearch galaxySearchUser1 = new GalaxySearch(
				localGalaxy.getGalaxyInstanceUser1());
		GalaxyLibraryBuilder galaxyLibrary = new GalaxyLibraryBuilder(
				localGalaxy.getGalaxyInstanceAdmin(), galaxySearchAdmin);
		UploadResult expectedUploadResult;

		GalaxyObjectName libraryName = new GalaxyObjectName(
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
		Library library = galaxySearchUser1.findLibraryWithId(libraryId);
		assertNotNull(library);
		LibraryFolder illuminaFolder = galaxyLibrary.createLibraryFolder(
				library, new GalaxyObjectName("illumina_reads"));
		assertNotNull(illuminaFolder);
		LibraryFolder sampleFolder = galaxyLibrary.createLibraryFolder(library,
				illuminaFolder, new GalaxyObjectName("testData"));
		assertNotNull(sampleFolder);
		LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(
				library, new GalaxyObjectName("references"));
		assertNotNull(referencesFolder);

		// user 1 should have access to library
		List<Library> libraries = galaxySearchUser1
				.findLibraryWithName(libraryName);
		assertEquals("The number of libraries with name " + libraryName
				+ " is not one", 1, libraries.size());

		// admin should have access to library
		libraries = galaxySearchAdmin.findLibraryWithName(libraryName);
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
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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

		// no duplicate folders or libraries for user1
		libraries = galaxySearchUser1.findLibraryWithName(libraryName);
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
		libraries = galaxySearchAdmin.findLibraryWithName(libraryName);
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
		GalaxySearch galaxySearchAdmin = new GalaxySearch(
				localGalaxy.getGalaxyInstanceAdmin());
		GalaxySearch galaxySearchUser1 = new GalaxySearch(
				localGalaxy.getGalaxyInstanceUser1());
		GalaxySearch galaxySearchUser2 = new GalaxySearch(
				localGalaxy.getGalaxyInstanceUser2());

		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleToExistingLibraryDifferentUsers");

		// build data library structure with no data in it
		Library returnedLibrary = galaxyAPI.buildGalaxyLibrary(libraryName,
				localGalaxy.getUser1Name());
		String libraryId = returnedLibrary.getId();
		assertNotNull(libraryId);

		// library should be visible to user 1 and admin
		assertNotNull(galaxySearchUser1.findLibraryWithId(libraryId));
		assertEquals(1, galaxySearchUser1.findLibraryWithName(libraryName)
				.size());
		assertNotNull(galaxySearchAdmin.findLibraryWithId(libraryId));
		assertEquals(1, galaxySearchAdmin.findLibraryWithName(libraryName)
				.size());

		// library should not be visible to user 2
		assertNull(galaxySearchUser2.findLibraryWithId(libraryId));
		assertEquals(0, galaxySearchUser2.findLibraryWithName(libraryName)
				.size());

		// there should be nothing in this library
		List<LibraryContent> libraryContents = localGalaxy
				.getGalaxyInstanceUser1().getLibrariesClient()
				.getLibraryContents(libraryId);
		Map<String, LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(1, contentsMap.size());
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());

		// attempt to upload to this above data library as a different user
		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
				"testData"), dataFilesDouble);
		List<UploadSample> samples = new ArrayList<UploadSample>();
		samples.add(galaxySample);
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName,
				localGalaxy.getUser2Name()));

		// library should be visible to user 1 and admin
		assertNotNull(galaxySearchUser1.findLibraryWithId(libraryId));
		assertEquals(1, galaxySearchUser1.findLibraryWithName(libraryName)
				.size());
		assertNotNull(galaxySearchAdmin.findLibraryWithId(libraryId));
		assertEquals(1, galaxySearchAdmin.findLibraryWithName(libraryName)
				.size());

		// library should not be visible to user 2 (user 2 shared with user 1,
		// but did not gain access)
		assertNull(galaxySearchUser2.findLibraryWithId(libraryId));
		assertEquals(0, galaxySearchUser2.findLibraryWithName(libraryName)
				.size());

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
		GalaxyObjectName libraryName = new GalaxyObjectName(
				"testUploadSampleOneFileAlreadyExists");

		UploadSample galaxySample = new GalaxySample(new GalaxyObjectName(
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
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()),
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
		galaxySample = new GalaxySample(new GalaxyObjectName("testData"),
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
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()),
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
	 * Tests uploading multiple samples to Galaxy.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws ConstraintViolationException
	 * @throws UploadException
	 */
	@Test
	public void testUploadSamples() throws URISyntaxException,
			MalformedURLException, ConstraintViolationException,
			UploadException {
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSamples");
		String localGalaxyURL = localGalaxy
				.getGalaxyURL()
				.toString()
				.substring(0,
						localGalaxy.getGalaxyURL().toString().length() - 1); // remove
																				// trailing
																				// '/'

		List<UploadSample> samples = new ArrayList<UploadSample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyObjectName(
				"testData1"), dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(new GalaxyObjectName(
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
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()),
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
	}
}
