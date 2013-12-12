package ca.corefacility.bioinformatics.irida.pipeline.data.impl.integration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.pipeline.data.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxyAPI;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.DownloadProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.GalaxyData.User;
import com.sun.jersey.api.client.UniformInterfaceException;

public class GalaxyAPIIT
{
	private static final int largestPort = 65535;
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyAPIIT.class);
	
	private static GalaxyAPI restAPIGalaxy;
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
	private static String invalidGalaxyAPIKey = "0";
	
	private static GalaxyDaemon galaxyDaemon;
	private static BootStrapper bootStrapper;
	
	private static List<File> dataFilesSingle;
	private static List<File> dataFilesDouble;
		
	@BeforeClass
	public static void setup() throws IOException, URISyntaxException
	{
		File galaxyLogFile;
		DownloadProperties downloadProperties =
				new DownloadProperties(DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL, DownloadProperties.BRANCH_STABLE, null);
	    bootStrapper = new BootStrapper(downloadProperties);
	    
	    galaxyAdmin = "admin@localhost";
	    File galaxyCache = new File(System.getProperty("user.home"), ".galaxy-bootstrap");
	    
	    logger.info("About to download Galaxy from url: " + DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL + ", branch:" +
	    		DownloadProperties.BRANCH_STABLE);
	    logger.info("Galaxy will be downloaded to cache at: " + galaxyCache.getAbsolutePath()
	    		+ ", and copied to: " + bootStrapper.getPath());
	    bootStrapper.setupGalaxy();
	    logger.info("Finished downloading Galaxy");
	    
	    galaxyLogFile = new File(bootStrapper.getPath() + File.separator + "paster.log");
	    
	    GalaxyProperties galaxyProperties = new GalaxyProperties().assignFreePort().configureNestedShedTools();
	    galaxyProperties.prepopulateSqliteDatabase();
	    
	    galaxyPort = galaxyProperties.getPort();
	    galaxyURL = "http://localhost:" + galaxyPort + "/";
	    
	    GalaxyData galaxyData = new GalaxyData();
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
	    
	    galaxyProperties.setAppProperty("allow_library_path_paste", "true");
	    
	    logger.info("About to run Galaxy on url: " + galaxyURL);
	    logger.debug("Galaxy admin user: " + galaxyAdmin + ", password: " + adminPassword +
	    		", apiKey: " + adminAPIKey);
	    logger.debug("Galaxy regular user: " + regularUserName + ", password: " + regularUserPassword +
	    		", apiKey: " + regularUserAPIKey);
	    logger.debug("Galaxy regular user2: " + regularUserName2 + ", password: " + regularUserPassword2 +
	    		", apiKey: " + regularUserAPIKey2);
	    galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);
	    logger.info("Waiting for Galaxy to come up, log: " + galaxyLogFile.getAbsolutePath());
	    if (!galaxyDaemon.waitForUp())
	    {
	    	fail("Could not start Galaxy for tests");
	    }
	    logger.info("Galaxy running on url: " + galaxyURL);
	    
	    restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, adminAPIKey);
	    
	    galaxyInstanceAdmin = GalaxyInstanceFactory.get(galaxyURL, adminAPIKey);
	    galaxyInstanceRegularUser = GalaxyInstanceFactory.get(galaxyURL, regularUserAPIKey);
	    galaxyInstanceRegularUser2 = GalaxyInstanceFactory.get(galaxyURL, regularUserAPIKey2);
	    
	    // setup data files
		File dataFile1 = new File(GalaxyAPIIT.class.getResource("testData1.fastq").toURI());
		File dataFile2 = new File(GalaxyAPIIT.class.getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
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
	
	@Test(expected=RuntimeException.class)
	public void testCreateGalaxyAPIInvalidAdmin()
	{
		restAPIGalaxy = new GalaxyAPI(galaxyURL, invalidGalaxyAdmin, adminAPIKey);
	}
	
	@Test(expected=RuntimeException.class)
	public void testInvalidAPIKey() throws URISyntaxException, LibraryUploadException
	{
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(adminAPIKey)) // what are the chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}
		
		restAPIGalaxy = new GalaxyAPI(galaxyURL, galaxyAdmin, wrongAdminAPIKey);
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
		assertEquals(3, contentsMapRegularUser.size());
		
		assertTrue(contentsMapRegularUser.containsKey("/"));
		assertEquals("folder", contentsMapRegularUser.get("/").getType());
		assertTrue(contentsMapRegularUser.containsKey("/testData"));
		assertEquals("folder", contentsMapRegularUser.get("/testData").getType());
		assertTrue(contentsMapRegularUser.containsKey("/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/testData/testData1.fastq").getType());
		
		// admin should have access to files
		Library actualLibraryAdmin = findLibraryByName(libraryName, galaxyInstanceAdmin);
		assertNotNull(actualLibraryAdmin);
		
		libraryContents = galaxyInstanceAdmin.getLibrariesClient().
				getLibraryContents(libraryId);
		assertNotNull(libraryContents);
		Map<String,LibraryContent> contentsMapAdminUser = fileToLibraryContentMap(libraryContents);
		assertEquals(3, contentsMapAdminUser.size());
		
		assertTrue(contentsMapAdminUser.containsKey("/"));
		assertEquals("folder", contentsMapAdminUser.get("/").getType());
		assertTrue(contentsMapAdminUser.containsKey("/testData"));
		assertEquals("folder", contentsMapAdminUser.get("/testData").getType());
		assertTrue(contentsMapAdminUser.containsKey("/testData/testData1.fastq"));
		assertEquals("file", contentsMapAdminUser.get("/testData/testData1.fastq").getType());
		
		// 2nd regular user should not have access to library or files
		Library actualLibraryRegularUser2 = findLibraryByName(libraryName, galaxyInstanceRegularUser2);
		assertNull(actualLibraryRegularUser2);
		
		boolean exception = false;
		try
		{
			libraryContents = galaxyInstanceRegularUser2.getLibrariesClient().
					getLibraryContents(libraryId);
		}
		catch (RuntimeException e)
		{
			exception = true;
		}
		
		assertTrue(exception);
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
		assertEquals(3, contentsMapRegularUser.size());
		
		assertTrue(contentsMapRegularUser.containsKey("/"));
		assertEquals("folder", contentsMapRegularUser.get("/").getType());
		assertTrue(contentsMapRegularUser.containsKey("/testData"));
		assertEquals("folder", contentsMapRegularUser.get("/testData").getType());
		assertTrue(contentsMapRegularUser.containsKey("/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/testData/testData1.fastq").getType());
		
		// regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName, galaxyInstanceRegularUser);
		assertNull(actualLibrary);
		
		boolean exception = false;
		try
		{
			libraryContents = galaxyInstanceRegularUser.getLibrariesClient().
					getLibraryContents(libraryId);
		}
		catch (RuntimeException e)
		{
			exception = true;
		}
		
		assertTrue(exception);
		
		// 2nd regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName, galaxyInstanceRegularUser2);
		assertNull(actualLibrary);
		
		exception = false;
		try
		{
			libraryContents = galaxyInstanceRegularUser2.getLibrariesClient().
					getLibraryContents(libraryId);
		}
		catch (RuntimeException e)
		{
			exception = true;
		}
		
		assertTrue(exception);
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
		
		restAPIGalaxy = new GalaxyAPI(wrongGalaxyURL, galaxyAdmin, adminAPIKey);
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
		assertEquals(4, contentsMap.size());
		
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/testData"));
		assertEquals("folder", contentsMap.get("/testData").getType());
		assertTrue(contentsMap.containsKey("/testData/testData1.fastq"));
		assertEquals("file", contentsMap.get("/testData/testData1.fastq").getType());
		assertTrue(contentsMap.containsKey("/testData/testData2.fastq"));
		assertEquals("file", contentsMap.get("/testData/testData2.fastq").getType());
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
		assertEquals(5, contentsMap.size());
		
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/testData1"));
		assertEquals("folder", contentsMap.get("/testData1").getType());
		assertTrue(contentsMap.containsKey("/testData1/testData1.fastq"));
		assertEquals("file", contentsMap.get("/testData1/testData1.fastq").getType());
		assertTrue(contentsMap.containsKey("/testData2"));
		assertEquals("folder", contentsMap.get("/testData2").getType());
		assertTrue(contentsMap.containsKey("/testData2/testData1.fastq"));
		assertEquals("file", contentsMap.get("/testData2/testData1.fastq").getType());
	}
}
