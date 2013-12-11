package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.integration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowRESTAPIGalaxy;

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

public class WorkflowRESTAPIGalaxyIT
{
	private static final int largestPort = 65535;
	
	private static final Logger logger = LoggerFactory.getLogger(WorkflowRESTAPIGalaxyIT.class);
	
	private static WorkflowRESTAPIGalaxy restAPIGalaxy;
	private static GalaxyInstance galaxyInstance;
	
	private static String adminAPIKey;
	private static int galaxyPort;
	private static String galaxyURL;
	
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
	    
	    String galaxyAdmin = "admin@localhost";
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
	    
	    final GalaxyData galaxyData = new GalaxyData();
	    final User adminUser = new User(galaxyAdmin);
	    galaxyData.getUsers().add(adminUser);
	    galaxyProperties.setAdminUser(galaxyAdmin);
	    adminAPIKey = adminUser.getApiKey();
	    
	    galaxyProperties.setAppProperty("allow_library_path_paste", "true");
	    
	    logger.info("About to run Galaxy on url: " + galaxyURL);
	    logger.debug("Galaxy admin user: " + galaxyAdmin + ", apiKey: " + adminAPIKey);
	    galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);
	    logger.info("Waiting for Galaxy to come up, log: " + galaxyLogFile.getAbsolutePath());
	    if (!galaxyDaemon.waitForUp())
	    {
	    	fail("Could not start Galaxy for tests");
	    }
	    logger.info("Galaxy running on url: " + galaxyURL);
	    
	    restAPIGalaxy = new WorkflowRESTAPIGalaxy(galaxyURL, adminAPIKey);
	    
	    galaxyInstance = GalaxyInstanceFactory.get(galaxyURL, adminAPIKey);
	    
	    // setup data files
		File dataFile1 = new File(WorkflowRESTAPIGalaxyIT.class.getResource("testData1.fastq").toURI());
		File dataFile2 = new File(WorkflowRESTAPIGalaxyIT.class.getResource("testData2.fastq").toURI());
		
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
	
	private Library findLibraryByID(String libraryID)
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
	
	private Library findLibraryByName(String libraryName)
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
	
	@Test
	public void testCreateLibrary()
	{
		String libraryName = "testCreateLibrary";
		
		String libraryID = restAPIGalaxy.buildGalaxyLibrary(libraryName);
		
		Library actualLibrary = findLibraryByID(libraryID);
		
		assertNotNull(actualLibrary);
		assertEquals(libraryName, actualLibrary.getName());
	}
	
	@Test
	public void testUploadSample() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSample";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName));
		
		Library actualLibrary = findLibraryByName(libraryName);
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = galaxyInstance.getLibrariesClient().getLibraryContents(actualLibrary.getId());
		Map<String,LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(3, contentsMap.size());
		
		assertTrue(contentsMap.containsKey("/"));
		assertEquals("folder", contentsMap.get("/").getType());
		assertTrue(contentsMap.containsKey("/testData"));
		assertEquals("folder", contentsMap.get("/testData").getType());
		assertTrue(contentsMap.containsKey("/testData/testData1.fastq"));
		assertEquals("file", contentsMap.get("/testData/testData1.fastq").getType());
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testUploadSampleWrongGalaxyAddress() throws URISyntaxException, LibraryUploadException
	{
		// set port to something Galaxy is not running on
		int wrongPort = (galaxyPort + 1);
		if (wrongPort > largestPort)
		{
			wrongPort = galaxyPort - 1;
		}
		
		String wrongGalaxyURL = "http://localhost:" + wrongPort + "/";
		
		WorkflowRESTAPIGalaxy restAPIGalaxy = new WorkflowRESTAPIGalaxy(wrongGalaxyURL, adminAPIKey);
		
		String libraryName = "testUploadSampleWrongGalaxyAddress";
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		restAPIGalaxy.uploadSamples(samples, libraryName);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testUploadSampleWrongAPIKey() throws URISyntaxException, LibraryUploadException
	{
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(adminAPIKey)) // what are the chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}
		
		WorkflowRESTAPIGalaxy restAPIGalaxy = new WorkflowRESTAPIGalaxy(galaxyURL, wrongAdminAPIKey);
		
		String libraryName = "testUploadSampleWrongGalaxyAddress";
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		restAPIGalaxy.uploadSamples(samples, libraryName);
	}
	
	@Test
	public void testUploadSampleMultipleFile() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleMultipleFile";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName));
		
		Library actualLibrary = findLibraryByName(libraryName);
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = galaxyInstance.getLibrariesClient().getLibraryContents(actualLibrary.getId());
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
		
		assertTrue(restAPIGalaxy.uploadSamples(samples, libraryName));
		
		Library actualLibrary = findLibraryByName(libraryName);
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = galaxyInstance.getLibrariesClient().getLibraryContents(actualLibrary.getId());
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
