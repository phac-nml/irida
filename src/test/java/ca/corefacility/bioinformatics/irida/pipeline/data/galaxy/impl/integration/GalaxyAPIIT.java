package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.integration;

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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.model.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.model.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySearch;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyUploadResult;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class, LocalGalaxyConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GalaxyAPIIT
{		
	@Autowired
	private LocalGalaxy localGalaxy;
	
	@Autowired
	private GalaxyAPI galaxyAPI;
	
	private List<Path> dataFilesSingle;
	private List<Path> dataFilesDouble;
	
	@Before
	public void setup() throws URISyntaxException
	{
		galaxyAPI.setLinkUploadedFiles(false);
		
	    setupDataFiles();
	}
	
	private void setupDataFiles() throws URISyntaxException
	{
		Path dataFile1 = Paths.get(GalaxyAPIIT.class.getResource("testData1.fastq").toURI());
		Path dataFile2 = Paths.get(GalaxyAPIIT.class.getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<Path>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
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
	
	private Library findLibraryByName(GalaxyObjectName libraryName, GalaxyInstance galaxyInstance)
	{
		Library actualLibrary = null;
		List<Library> libraries = galaxyInstance.getLibrariesClient().getLibraries();
		for (Library curr : libraries)
		{
			if (libraryName.getName().equals(curr.getName()))
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
	
	private Path createTemporaryDataFile() throws IOException, URISyntaxException
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
		
		return dataFileTemp.toPath();
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testCreateLibraryInvalidName() throws CreateLibraryException
	{
		GalaxyObjectName invalidLibraryName = new GalaxyObjectName("<a href='http://google.com'>invalid name</a>");
	    galaxyAPI.buildGalaxyLibrary(invalidLibraryName, localGalaxy.getUser1Name());
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testCreateLibraryInvalidUserName() throws CreateLibraryException
	{
		GalaxyObjectName invalidLibraryName = new GalaxyObjectName("testCreateLibraryInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_email");
	    galaxyAPI.buildGalaxyLibrary(invalidLibraryName, userEmail);
	}
	
	@Test(expected=RuntimeException.class)
	public void testCreateGalaxyAPIInvalidAdmin()
	{
	    new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getNonExistentGalaxyAdminName(),
	    		localGalaxy.getAdminAPIKey());
	}
	
	@Test(expected=RuntimeException.class)
	public void testInvalidAPIKey() throws URISyntaxException, LibraryUploadException
	{
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(localGalaxy.getAdminAPIKey())) // what are the chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}
		
		new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(), wrongAdminAPIKey);
	}
	
	@Test
	public void testCreateLibraryAdmin() throws CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testCreateLibraryAdmin");
		
		Library library = galaxyAPI.buildGalaxyLibrary(libraryName, localGalaxy.getAdminName());
		String libraryID = library.getId();
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceAdmin());	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName.getName(), actualLibraryAdmin.getName());
		
		// make sure regular user cannot see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser1());
		assertNull(actualLibraryRegularUser);
	}
	
	@Test
	public void testCreateLibraryRegularUser() throws CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testCreateLibraryRegularUser");
		
		Library library = galaxyAPI.buildGalaxyLibrary(libraryName, localGalaxy.getUser1Name());
		String libraryID = library.getId();
		
		// make sure regular user can see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		assertEquals(libraryName.getName(), actualLibraryRegularUser.getName());
		
		// make sure 2nd regular user cannot see library
		Library actualLibraryRegularUser2 = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibraryRegularUser2);
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceAdmin());	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName.getName(), actualLibraryAdmin.getName());
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testCreateLibraryNonExistentUser() throws CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testCreateLibraryInvalidUser");
		
		galaxyAPI.buildGalaxyLibrary(libraryName, localGalaxy.getNonExistentGalaxyUserName());
	}
	
	@Test
	public void testUploadSampleRegularUser() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleRegularUser");
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceUser1().getLibrariesClient().
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
		Library actualLibraryAdmin = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibraryAdmin);
		
		libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
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
		Library actualLibraryRegularUser2 = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibraryRegularUser2);
		
		try
		{
			libraryContents = localGalaxy.getGalaxyInstanceUser2().getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}		
	}
	
	@Test
	public void testUploadSampleAdminUser() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleAdminUser");
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName()));
		
		// admin user should have access to files
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
		String libraryId = actualLibrary.getId();
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
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
		actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser1());
		assertNull(actualLibrary);
		
		try
		{
			libraryContents = localGalaxy.getGalaxyInstanceUser1().getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}
		
		// 2nd regular user should not have access to files
		actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibrary);
		
		try
		{
			libraryContents = localGalaxy.getGalaxyInstanceUser2().getLibrariesClient().
					getLibraryContents(libraryId);
			fail("Did not throw RuntimeException");
		} catch (RuntimeException e){}
	}
	
	@Test
	public void testUploadSampleNoLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException, CreateLibraryException
	{
		galaxyAPI.setLinkUploadedFiles(false);
		
		Path dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFileTemp1);
		
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleNoLink");
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceUser1().getLibrariesClient().
				getLibraryContents(libraryId);
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(libraryName.getName(), "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		
		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(
				Files.newBufferedReader(dataFileTemp1, Charset.defaultCharset()));
		
		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);
		
		// delete original file
		assertTrue(dataFileTemp1.toFile().delete());
		
		// file contents should be the same (no link)
		galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		assertEquals(fileSystemFileContents, galaxyFileContents);
	}
	
	@Test
	public void testUploadSampleLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException, CreateLibraryException
	{
		galaxyAPI.setLinkUploadedFiles(true);
		
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleLink");
		
		Path dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<Path>();
		dataFilesSingle.add(dataFileTemp1);
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
		// regular user should have access to files
		Library actualLibraryRegularUser = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		
		String libraryId = actualLibraryRegularUser.getId();
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceUser1().getLibrariesClient().
				getLibraryContents(libraryId);
		Map<String,LibraryContent> contentsMapRegularUser = fileToLibraryContentMap(libraryContents);
		
		assertTrue(contentsMapRegularUser.containsKey("/illumina_reads/testData/testData1.fastq"));
		assertEquals("file", contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getType());
		
		// download file from Galaxy
		String galaxyFileContents = getGalaxyFileContents(libraryName.getName(), "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		
		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(
				Files.newBufferedReader(dataFileTemp1, Charset.defaultCharset()));
		
		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);
		
		// delete original file
		assertTrue(dataFileTemp1.toFile().delete());
		
		// should get an error when attempting to download file
		try
		{
			galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
					contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
			fail("No exception when attempting to download");
		} catch (Exception e){}
	}
	
	@Test(expected=RuntimeException.class)
	public void testUploadSampleWrongGalaxyAddress() throws URISyntaxException, LibraryUploadException
	{		
		new GalaxyAPI(localGalaxy.getInvalidGalaxyURL(),
				localGalaxy.getAdminName(), localGalaxy.getAdminAPIKey());
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testUploadSampleWrongUser() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{	
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleWrongUser");
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getNonExistentGalaxyUserName());
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testUploadSampleInvalidUserName() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{	
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleInvalidUserName");
		GalaxyAccountEmail userEmail = new GalaxyAccountEmail("invalid_user");
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		galaxyAPI.uploadSamples(samples, libraryName, userEmail);
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testUploadSampleInvalidSampleName() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{	
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleInvalidSampleName");
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("<invalidSample>"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getUser1Name());
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testUploadSampleInvalidLibraryName() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{	
		GalaxyObjectName libraryName = new GalaxyObjectName("<invalidLibrary>");
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getUser1Name());
	}
	
	@Test
	public void testUploadSampleMultipleFile() throws URISyntaxException, LibraryUploadException, CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleMultipleFile");
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName()));
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
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
	
	private int countNumberOfFolderPaths(List<LibraryContent> contents, String folderPaths)
	{
		int count = 0;
		for (LibraryContent c : contents)
		{
			if (folderPaths.equals(c.getName()))
			{
				count++;
			}
		}
		
		return count;
	}
	
	@Test
	public void testUploadSampleToExistingLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		GalaxySearch galaxySearch = new GalaxySearch(localGalaxy.getGalaxyInstanceAdmin());
		GalaxyLibraryBuilder galaxyLibrary = new GalaxyLibraryBuilder(localGalaxy.getGalaxyInstanceAdmin(), galaxySearch);
		GalaxyUploadResult expectedUploadResult;
		
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleToExistingSampleFolder");
		
		// build data library structure with no data in it
		Library returnedLibrary = galaxyAPI.buildGalaxyLibrary(libraryName, localGalaxy.getAdminName());
		String libraryId = returnedLibrary.getId();
		assertNotNull(libraryId);
		expectedUploadResult = new GalaxyUploadResult(returnedLibrary, localGalaxy.getGalaxyURL());
				
		Library library = galaxySearch.findLibraryWithId(libraryId);
		assertNotNull(library);
		
		LibraryFolder illuminaFolder = galaxyLibrary.createLibraryFolder(library, new GalaxyObjectName("illumina_reads"));
		assertNotNull(illuminaFolder);
		
		LibraryFolder sampleFolder = galaxyLibrary.createLibraryFolder(library, illuminaFolder, new GalaxyObjectName("testData"));
		assertNotNull(sampleFolder);
		
		LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(library, new GalaxyObjectName("references"));
		assertNotNull(referencesFolder);
		
		List<Library> libraries = galaxySearch.findLibraryWithName(libraryName);
		assertEquals("The number of libraries with name " + libraryName + " is not one", 1, libraries.size());
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(library.getId());
		
		int sampleFolderCount = countNumberOfFolderPaths(libraryContents, "/illumina_reads/testData");
		assertEquals("The number of testData folders is not one", 1, sampleFolderCount);
		
		int illuminaReadsFolderCount = countNumberOfFolderPaths(libraryContents, "/illumina_reads");
		assertEquals("The number of illumina_reads folders is not one", 1, illuminaReadsFolderCount);
		
		int referencesFolderCount = countNumberOfFolderPaths(libraryContents, "/references");
		assertEquals("The number of references folders is not one", 1, referencesFolderCount);
		
		// attempt to upload to this above data library, should not create duplicate library nor duplicate sample folder
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		GalaxyUploadResult actualUploadResult = galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertEquals(expectedUploadResult, actualUploadResult);
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
		libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
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
		
		libraries = galaxySearch.findLibraryWithName(libraryName);
		assertEquals("The number of libraries with name " + libraryName + " is not one", 1, libraries.size());
		
		sampleFolderCount = countNumberOfFolderPaths(libraryContents, "/illumina_reads/testData");
		assertEquals("The number of testData folders is not one", 1, sampleFolderCount);
		
		illuminaReadsFolderCount = countNumberOfFolderPaths(libraryContents, "/illumina_reads");
		assertEquals("The number of illumina_reads folders is not one", 1, illuminaReadsFolderCount);
		
		referencesFolderCount = countNumberOfFolderPaths(libraryContents, "/references");
		assertEquals("The number of references folders is not one", 1, referencesFolderCount);
	}
	
	@Test
	public void testUploadSampleOneFileAlreadyExists() throws URISyntaxException, LibraryUploadException, MalformedURLException, CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSampleOneFileAlreadyExists");
		
		GalaxySample galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String localGalaxyURL = localGalaxy.getGalaxyURL().substring(0,localGalaxy.getGalaxyURL().length()-1); // remove trailing '/'
		
		GalaxyUploadResult actualUploadResult = galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName.getName(), actualUploadResult.getLibraryName());
		assertEquals(new URL(localGalaxyURL + "/library"), actualUploadResult.getDataLocation());
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()), 
				actualUploadResult.getLibraryAPIURL());
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
		Map<String,LibraryContent> contentsMap = fileToLibraryContentMap(libraryContents);
		assertEquals(5, contentsMap.size());
		
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
		
		// now attempt to upload dataFilesDouble with two files, only one file should upload		
		galaxySample = new GalaxySample(new GalaxyObjectName("testData"), dataFilesDouble);
		samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		// make sure both libraries are the same
		actualUploadResult = galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName.getName(), actualUploadResult.getLibraryName());
		assertEquals(new URL(localGalaxyURL + "/library"), actualUploadResult.getDataLocation());
		
		actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()),
				actualUploadResult.getLibraryAPIURL());
		
		libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
		contentsMap = fileToLibraryContentMap(libraryContents);
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
		
		// make sure only 1 instance of testData2 exists in library
		int countTestData2 = countNumberOfFolderPaths(libraryContents, "/illumina_reads/testData/testData2.fastq");
		assertEquals("More than one copy of testData2.fastq was uploaded", 1, countTestData2);
		
		// make sure only 1 instance of testData sample folder exists in library
		int countTestDataFolder = countNumberOfFolderPaths(libraryContents, "/illumina_reads/testData");
		assertEquals("More than one copy of /illumina_reads/testData was created", 1, countTestDataFolder);
		
		// make sure only 1 instance of illumina_reads folder exists in library
		int countIlluminaReadsFolder = countNumberOfFolderPaths(libraryContents, "/illumina_reads");
		assertEquals("More than one copy of /illumina_reads was created", 1, countIlluminaReadsFolder);
		
		// make sure only 1 instance of references folder exists in library
		int countReferencesFolder = countNumberOfFolderPaths(libraryContents, "/illumina_reads");
		assertEquals("More than one copy of /references was created", 1, countReferencesFolder);
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException, MalformedURLException, CreateLibraryException
	{
		GalaxyObjectName libraryName = new GalaxyObjectName("testUploadSamples");
		String localGalaxyURL = localGalaxy.getGalaxyURL().substring(0,localGalaxy.getGalaxyURL().length()-1); // remove trailing '/'
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample1 = new GalaxySample(new GalaxyObjectName("testData1"), dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample(new GalaxyObjectName("testData2"), dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		GalaxyUploadResult actualUploadResult =
				galaxyAPI.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(actualUploadResult);
		assertEquals(libraryName.getName(), actualUploadResult.getLibraryName());
		assertEquals(new URL(localGalaxyURL + "/library"), actualUploadResult.getDataLocation());
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		assertEquals(actualLibrary.getId(), actualUploadResult.getLibraryId());
		assertEquals(new URL(localGalaxyURL + actualLibrary.getUrl()), 
				actualUploadResult.getLibraryAPIURL());
		
		List<LibraryContent> libraryContents = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient().
				getLibraryContents(actualLibrary.getId());
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
