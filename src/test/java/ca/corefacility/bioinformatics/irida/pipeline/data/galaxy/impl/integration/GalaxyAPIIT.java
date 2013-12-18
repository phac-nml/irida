package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.LibraryUploadException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset.Source;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.DownloadProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.GalaxyData.User;

public class GalaxyAPIIT
{
	private static final int largestPort = 65535;
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyAPIIT.class);
	
	private static GalaxyInstance galaxyInstanceAdmin;
	private static GalaxyInstance galaxyInstanceRegularUser;
	private static GalaxyInstance galaxyInstanceRegularUser2;
	
	private static String regularUserName = "regular@localhost";
	private static String regularUserPassword;
	private static String regularUserAPIKey;
	
	private static String regularUserName2 = "regular2@localhost";
	private static String regularUserPassword2;
	private static String regularUserAPIKey2;
	
	private static String adminAPIKey;
	private static String adminPassword;
	private static String galaxyAdmin;
	private static String invalidGalaxyUser = "invalid@localhost";
	private static int galaxyPort;
	private static String galaxyURL;
	
	private static String invalidGalaxyAdmin = "admin_invalid@localhost";
	
	private static GalaxyDaemon galaxyDaemon;
	private static BootStrapper bootStrapper;
	
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
	
	private GalaxyAPI restAPIGalaxy;
		
	@BeforeClass
	public static void setupStatic() throws IOException, URISyntaxException
	{
	    GalaxyData galaxyData = new GalaxyData();
	    bootStrapper = downloadGalaxy();
	    
	    GalaxyProperties galaxyProperties = setupGalaxyProperties();
	    	    
	    buildGalaxyUsers(galaxyProperties, galaxyData);
	    
	    runGalaxy(bootStrapper, galaxyProperties, galaxyData);
	}
	
	@Before
	public void setup() throws URISyntaxException
	{
	    restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, adminAPIKey, false);
	    
	    setupDataFiles();
	}
	
	private static BootStrapper downloadGalaxy()
	{		
		DownloadProperties downloadProperties =
				new DownloadProperties(DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL, DownloadProperties.BRANCH_STABLE, null);
	    BootStrapper bootStrapper = new BootStrapper(downloadProperties);
	    
	    galaxyAdmin = "admin@localhost";
	    File galaxyCache = new File(System.getProperty("user.home"), ".galaxy-bootstrap");
	    
	    logger.info("About to download Galaxy from url: " + DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL + ", branch:" +
	    		DownloadProperties.BRANCH_STABLE);
	    logger.info("Galaxy will be downloaded to cache at: " + galaxyCache.getAbsolutePath()
	    		+ ", and copied to: " + bootStrapper.getPath());
	    bootStrapper.setupGalaxy();
	    logger.info("Finished downloading Galaxy");
	    
	    return bootStrapper;
	}
	
	private void setupDataFiles() throws URISyntaxException
	{
		File dataFile1 = new File(GalaxyAPIIT.class.getResource("testData1.fastq").toURI());
		File dataFile2 = new File(GalaxyAPIIT.class.getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
	}
	
	private static GalaxyProperties setupGalaxyProperties()
	{
	    GalaxyProperties galaxyProperties = new GalaxyProperties().assignFreePort().configureNestedShedTools();
	    galaxyProperties.prepopulateSqliteDatabase();
	    galaxyProperties.setAppProperty("allow_library_path_paste", "true");
	    
	    galaxyPort = galaxyProperties.getPort();
	    galaxyURL = "http://localhost:" + galaxyPort + "/";
	    
	    return galaxyProperties;
	}
	
	private static void runGalaxy(BootStrapper bootStrapper, GalaxyProperties galaxyProperties, GalaxyData galaxyData)
	{
		File galaxyLogFile = new File(bootStrapper.getPath() + File.separator + "paster.log");
		
  		logger.info("Setting up Galaxy");
		logger.debug("Galaxy admin user: " + galaxyAdmin + ", password: " + adminPassword +
		   	", apiKey: " + adminAPIKey);
		logger.debug("Galaxy regular user: " + regularUserName + ", password: " + regularUserPassword +
			 ", apiKey: " + regularUserAPIKey);
		logger.debug("Galaxy regular user2: " + regularUserName2 + ", password: " + regularUserPassword2 +
			 ", apiKey: " + regularUserAPIKey2);
		   
		galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);
		   
		logger.info("Waiting for Galaxy to come up on url: " + galaxyURL + ", log: " +
			 galaxyLogFile.getAbsolutePath());
		
		if (!galaxyDaemon.waitForUp())
		{
			fail("Could not start Galaxy for tests");
		}
		logger.info("Galaxy running on url: " + galaxyURL);
	}
	
	private static void buildGalaxyUsers(GalaxyProperties galaxyProperties, GalaxyData galaxyData)
	{
	    User adminUser = new User(galaxyAdmin);
	    adminPassword = UUID.randomUUID().toString();
	    adminUser.setPassword(adminPassword);
	    
	    User regularUser = new User(regularUserName);
	    regularUserPassword = UUID.randomUUID().toString();
	    regularUser.setPassword(regularUserPassword);
	    
	    User regularUser2 = new User(regularUserName2);
	    regularUserPassword2 = UUID.randomUUID().toString();
	    regularUser2.setPassword(regularUserPassword2);
	    
	    galaxyData.getUsers().add(adminUser);
	    galaxyData.getUsers().add(regularUser);
	    galaxyData.getUsers().add(regularUser2);
	    
	    galaxyProperties.setAdminUser(galaxyAdmin);
	    adminAPIKey = adminUser.getApiKey();
	    
	    regularUserAPIKey = regularUser.getApiKey();
	    regularUserAPIKey2 = regularUser2.getApiKey();
	    
	    galaxyInstanceAdmin = GalaxyInstanceFactory.get(galaxyURL, adminAPIKey);
	    galaxyInstanceRegularUser = GalaxyInstanceFactory.get(galaxyURL, regularUserAPIKey);
	    galaxyInstanceRegularUser2 = GalaxyInstanceFactory.get(galaxyURL, regularUserAPIKey2);
	}
	
	@AfterClass
	public static void tearDown()
	{
		logger.info("Shutting down Galaxy on url=" + galaxyURL);
		galaxyDaemon.stop();
		galaxyDaemon.waitForDown();
		logger.info("Galaxy shutdown");
		logger.debug("Deleting Galaxy directory: " + bootStrapper.getPath());
		bootStrapper.deleteGalaxyRoot();
	}
	
	private Library findLibraryByID(String libraryID, GalaxyInstance galaxyInstance)
	{
		Library actualLibrary = null;
		List<Library> libraries = galaxyInstance.getLibrariesClient().getLibraries();
		for (Library curr : libraries)
		{
			if (libraryID.equals(curr.getId()))
			{
				actualLibrary = curr;
			}
		}
		
		return actualLibrary;
	}
	
	private Library findLibraryByName(String libraryName, GalaxyInstance galaxyInstance)
	{
		Library actualLibrary = null;
		List<Library> libraries = galaxyInstance.getLibrariesClient().getLibraries();
		for (Library curr : libraries)
		{
			if (libraryName.equals(curr.getName()))
			{
				actualLibrary = curr;
			}
		}
		
		return actualLibrary;
	}
	
	private Map<String,LibraryContent> fileToLibraryContentMap(List<LibraryContent> libraryContents)
	{
		Map<String,LibraryContent> map = new HashMap<String,LibraryContent>();
		for (LibraryContent content : libraryContents)
		{
			map.put(content.getName(), content);
		}
		
		return map;
	}
	
	/**
	 * Given a file library ID, loads a file into a Galaxy history and then loads the contents of this file into a string.
	 * @param testName
	 * @param filename
	 * @param galaxyInstance
	 * @param libraryFileId
	 * @return  The String with the file contents.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private String getGalaxyFileContents(String testName, String filename, GalaxyInstance galaxyInstance, String libraryFileId) throws InterruptedException, IOException
	{
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		
		History history = new History();
		history.setName(testName);
		History persistedHistory = historiesClient.create(history);
		assertNotNull(persistedHistory);
		
		HistoryDataset historyDataset = new HistoryDataset();
		historyDataset.setSource(Source.LIBRARY);
		historyDataset.setContent(libraryFileId);
		HistoryDetails historyDetails = historiesClient.createHistoryDataset(persistedHistory.getId(), historyDataset);
		assertNotNull(historyDetails);

		String dataId = getIdForFileInHistory(filename, persistedHistory.getId(), galaxyInstance);
		assertNotNull(dataId);
		
		Dataset dataset;
		do
		{
			dataset = historiesClient.showDataset(persistedHistory.getId(), dataId);
			assertNotNull(dataset);
			Thread.sleep(2000);
		} while (!"ok".equals(dataset.getState()));
		
		URL url = new URL(dataset.getFullDownloadUrl());
		
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		InputStream stream = con.getInputStream();
		
		String galaxyFileContents = readFileContentsFromReader(
				new BufferedReader(new InputStreamReader(stream)));
		
		return galaxyFileContents;
	}
	
	private String getIdForFileInHistory(String filename, String historyId, GalaxyInstance galaxyInstance)
	{
		String dataId = null;
		List<HistoryContents> historyContentsList = galaxyInstance.getHistoriesClient().
				showHistoryContents(historyId);
		
		for (HistoryContents contents : historyContentsList)
		{
			if (filename.equals(contents.getName()))
			{
				dataId = contents.getId();
				break;
			}
		}
		
		return dataId;
	}
	
	private String readFileContentsFromReader(BufferedReader reader) throws IOException
	{
		String line;
		String contents = "";
		while((line = reader.readLine()) != null)
		{
			contents += line;
		}
		
		return contents;
	}
	
	private File createTemporaryDataFile() throws IOException, URISyntaxException
	{
		File dataFile1 = new File(GalaxyAPIIT.class.getResource("testData1.fastq").toURI());
		
		// create temp file so I can delete it afterwards for testing the "link" option in Galaxy
		File tempDir = File.createTempFile("testData1", "folder");
		tempDir.delete();
		tempDir.mkdir();
		tempDir.deleteOnExit();
		
		Path dataPathTemp = Paths.get(tempDir.getAbsolutePath(), "testData1.fastq");
		File dataFileTemp = dataPathTemp.toFile();
		Files.copy(Paths.get(dataFile1.getAbsolutePath()), dataPathTemp,
				StandardCopyOption.REPLACE_EXISTING);
		
		return dataFileTemp;
	}
	
	@Test(expected=RuntimeException.class)
	public void testCreateGalaxyAPIInvalidAdmin()
	{
		restAPIGalaxy = new GalaxyAPI(galaxyURL, invalidGalaxyAdmin, adminAPIKey, false);
	}
	
	@Test(expected=RuntimeException.class)
	public void testInvalidAPIKey() throws URISyntaxException, LibraryUploadException
	{
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(adminAPIKey)) // what are the chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}
		
		restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, wrongAdminAPIKey, false);
	}
	
	@Test
	public void testCreateLibraryAdmin() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryAdmin";
		
		String libraryID = restAPIGalaxy.buildGalaxyLibrary(libraryName, galaxyAdmin);
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, galaxyInstanceAdmin);	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName, actualLibraryAdmin.getName());
		
		// make sure regular user cannot see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, galaxyInstanceRegularUser);
		assertNull(actualLibraryRegularUser);
	}
	
	@Test
	public void testCreateLibraryRegularUser() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryRegularUser";
		
		String libraryID = restAPIGalaxy.buildGalaxyLibrary(libraryName, regularUserName);
		
		// make sure regular user can see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, galaxyInstanceRegularUser);
		assertNotNull(actualLibraryRegularUser);
		assertEquals(libraryName, actualLibraryRegularUser.getName());
		
		// make sure 2nd regular user cannot see library
		Library actualLibraryRegularUser2 = findLibraryByID(libraryID, galaxyInstanceRegularUser2);
		assertNull(actualLibraryRegularUser2);
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, galaxyInstanceAdmin);	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName, actualLibraryAdmin.getName());
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testCreateLibraryInvalidUser() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryInvalidUser";
		
		restAPIGalaxy.buildGalaxyLibrary(libraryName, invalidGalaxyUser);
	}
	
	@Test
	public void testUploadSampleRegularUser() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleRegularUser";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, regularUserName));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, galaxyInstanceRegularUser);
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = galaxyInstanceRegularUser.getLibrariesClient().
				getLibraryContents(libraryId);
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapRegularUser.size());
		
		assertTrue(contentsMapRegularUser.containsKey("/"));
		assertEquals("folder", contentsMapRegularUser.get("/").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapRegularUser.get("/illumina_reads").getType());
		assertTrue(contentsMapRegularUser.containsKey("/references"));
		assertEquals("folder", contentsMapRegularUser.get("/references").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMapRegularUser.get("/illumina_reads/testData").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// admin should have access to files
		Library actualLibraryAdmin = findLibraryByName(libraryName, galaxyInstanceAdmin);
		assertNotNull(actualLibraryAdmin);
		
		libraryContents = galaxyInstanceAdmin.getLibrariesClient().
				getLibraryContents(libraryId);
		assertNotNull(libraryContents);
		Map<String,LibraryContent> contentsMapAdminUser = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapAdminUser.size());
		
		assertTrue(contentsMapAdminUser.containsKey("/"));
		assertEquals("folder", contentsMapAdminUser.get("/").getType());
		assertTrue(contentsMapAdminUser.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapAdminUser.get("/illumina_reads").getType());
		assertTrue(contentsMapAdminUser.containsKey("/references"));
		assertEquals("folder", contentsMapAdminUser.get("/references").getType());
		assertTrue(contentsMapAdminUser.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMapAdminUser.get("/illumina_reads/testData").getType());
		assertTrue(contentsMapAdminUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapAdminUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// 2nd regular user should not have access to library or files
		Library actualLibraryRegularUser2 = findLibraryByName(libraryName, galaxyInstanceRegularUser2);
		assertNull(actualLibraryRegularUser2);
		
		try
		{
			libraryContents = galaxyInstanceRegularUser2.getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}		
	}
	
	@Test
	public void testUploadSampleAdminUser() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleAdminUser";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, galaxyAdmin));
		
		// admin user should have access to files
		Library actualLibrary = findLibraryByName(libraryName, galaxyInstanceAdmin);
		assertNotNull(actualLibrary);
		
		String libraryId = actualLibrary.getId();
		
		List<LibraryContent> libraryContents = galaxyInstanceAdmin.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMapRegularUser.size());
		
		assertTrue(contentsMapRegularUser.containsKey("/"));
		assertEquals("folder", contentsMapRegularUser.get("/").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMapRegularUser.get("/illumina_reads").getType());
		assertTrue(contentsMapRegularUser.containsKey("/references"));
		assertEquals("folder", contentsMapRegularUser.get("/references").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMapRegularUser.get("/illumina_reads/testData").getType());
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName, galaxyInstanceRegularUser);
		assertNull(actualLibrary);
		
		try
		{
			libraryContents = galaxyInstanceRegularUser.getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}
		
		// 2nd regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName, galaxyInstanceRegularUser2);
		assertNull(actualLibrary);
		
		try
		{
			libraryContents = galaxyInstanceRegularUser2.getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}
	}
	
	@Test
	public void testUploadSampleNoLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException
	{
		restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, adminAPIKey, false);
		
		File dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFileTemp1);
		
		String libraryName = "testUploadSampleNoLink";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, regularUserName));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, galaxyInstanceRegularUser);
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = galaxyInstanceRegularUser.getLibrariesClient().
				getLibraryContents(libraryId);
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(libraryName, "testData1.fastq", galaxyInstanceRegularUser,
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		
		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(
				new BufferedReader(new FileReader(dataFileTemp1.getAbsolutePath())));
		
		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);
		
		// delete original file
		assertTrue(dataFileTemp1.delete());
		
		// file contents should be the same (no link)
		galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", galaxyInstanceRegularUser,
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		assertEquals(fileSystemFileContents, galaxyFileContents);
	}
	
	@Test
	public void testUploadSampleLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException
	{
		restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, adminAPIKey, true);
		
		String libraryName = "testUploadSampleLink";
		
		File dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFileTemp1);
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, regularUserName));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, galaxyInstanceRegularUser);
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = galaxyInstanceRegularUser.getLibrariesClient().
				getLibraryContents(libraryId);
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(libraryName, "testData1.fastq", galaxyInstanceRegularUser,
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		
		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(
				new BufferedReader(new FileReader(dataFileTemp1.getAbsolutePath())));
		
		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);
		
		// delete original file
		assertTrue(dataFileTemp1.delete());
		
		// should get an error when attempting to download file
		try
		{
			galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", galaxyInstanceRegularUser,
					contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
			fail("No exception when attempting to download");
		} catch (Exception e){}
	}
	
	@Test(expected=RuntimeException.class)
	public void testUploadSampleWrongGalaxyAddress() throws URISyntaxException, LibraryUploadException
	{
		// set port to something Galaxy is not running on
		int wrongPort = (galaxyPort + 1);
		if (wrongPort > largestPort)
		{
			wrongPort = galaxyPort - 1;
		}
		
		String wrongGalaxyURL = "http://localhost:" + wrongPort + "/";
		
		restAPIGalaxy = new GalaxyAPI(wrongGalaxyURL, galaxyAdmin, adminAPIKey, false);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testUploadSampleWrongUser() throws URISyntaxException, LibraryUploadException
	{	
		String libraryName = "testUploadSampleWrongUser";
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		restAPIGalaxy.uploadSamples(samples, libraryName, invalidGalaxyUser);
	}
	
	@Test
	public void testUploadSampleMultipleFile() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleMultipleFile";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, galaxyAdmin));
		
		Library actualLibrary = findLibraryByName(libraryName, galaxyInstanceAdmin);
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = galaxyInstanceAdmin.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		Map<String,LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(6, contentsMap.size());
		
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMap.get("/illumina_reads/testData/testData1.fastq").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData/testData2.fastq"));
		assertEquals("file", contentsMap.get("/illumina_reads/testData/testData2.fastq").getType());
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSamples";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName, galaxyAdmin));
		
		Library actualLibrary = findLibraryByName(libraryName, galaxyInstanceAdmin);
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = galaxyInstanceAdmin.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		Map<String,LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(7, contentsMap.size());
		
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads"));
		assertEquals("folder", contentsMap.get("/illumina_reads").getType());
		assertTrue(contentsMap.containsKey("/references"));
		assertEquals("folder", contentsMap.get("/references").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData1"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData1").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData1/testData1.fastq"));
		assertEquals("file", contentsMap.get("/illumina_reads/testData1/testData1.fastq").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData2"));
		assertEquals("folder", contentsMap.get("/illumina_reads/testData2").getType());
		assertTrue(contentsMap.containsKey("/illumina_reads/testData2/testData1.fastq"));
		assertEquals("file", contentsMap.get("/illumina_reads/testData2/testData1.fastq").getType());
	}
}
