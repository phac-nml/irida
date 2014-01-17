package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyLibrary;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxySearch;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.utils.test.IridaIntegrationTest;

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

public class GalaxyAPIIT extends IridaIntegrationTest
{		
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
	
	private GalaxyAPI restAPIGalaxy;
	
	@Before
	public void setup() throws URISyntaxException
	{
	    restAPIGalaxy = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(),
	    		localGalaxy.getAdminAPIKey(), false);
	    
	    setupDataFiles();
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
	    restAPIGalaxy = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getInvalidGalaxyAdminName(),
	    		localGalaxy.getAdminAPIKey(), false);
	}
	
	@Test(expected=RuntimeException.class)
	public void testInvalidAPIKey() throws URISyntaxException, LibraryUploadException
	{
		String wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbad";
		if (wrongAdminAPIKey.equals(localGalaxy.getAdminAPIKey())) // what are the chances?
		{
			wrongAdminAPIKey = "badbadbadbadbadbadbadbadbadbadbaa";
		}
		
		restAPIGalaxy = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(), wrongAdminAPIKey, false);
	}
	
	@Test
	public void testCreateLibraryAdmin() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryAdmin";
		
		Library library = restAPIGalaxy.buildGalaxyLibrary(libraryName, localGalaxy.getAdminName());
		String libraryID = library.getId();
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceAdmin());	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName, actualLibraryAdmin.getName());
		
		// make sure regular user cannot see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser1());
		assertNull(actualLibraryRegularUser);
	}
	
	@Test
	public void testCreateLibraryRegularUser() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryRegularUser";
		
		Library library = restAPIGalaxy.buildGalaxyLibrary(libraryName, localGalaxy.getUser1Name());
		String libraryID = library.getId();
		
		// make sure regular user can see library
		Library actualLibraryRegularUser = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser1());
		assertNotNull(actualLibraryRegularUser);
		assertEquals(libraryName, actualLibraryRegularUser.getName());
		
		// make sure 2nd regular user cannot see library
		Library actualLibraryRegularUser2 = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceUser2());
		assertNull(actualLibraryRegularUser2);
		
		// make sure admin can see library
		Library actualLibraryAdmin = findLibraryByID(libraryID, localGalaxy.getGalaxyInstanceAdmin());	
		assertNotNull(actualLibraryAdmin);
		assertEquals(libraryName, actualLibraryAdmin.getName());
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testCreateLibraryInvalidUser() throws CreateLibraryException
	{
		String libraryName = "testCreateLibraryInvalidUser";
		
		restAPIGalaxy.buildGalaxyLibrary(libraryName, localGalaxy.getInvalidGalaxyUserName());
	}
	
	@Test
	public void testUploadSampleRegularUser() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleRegularUser";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
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
	public void testUploadSampleAdminUser() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleAdminUser";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName()));
		
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
	public void testUploadSampleNoLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException
	{
		restAPIGalaxy = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(),
				localGalaxy.getAdminAPIKey(), false);
		
		File dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFileTemp1);
		
		String libraryName = "testUploadSampleNoLink";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
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
		String galaxyFileContents = getGalaxyFileContents(libraryName, "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		
		// load file from filesystem
		String fileSystemFileContents = readFileContentsFromReader(
				new BufferedReader(new FileReader(dataFileTemp1.getAbsolutePath())));
		
		// make sure files are the same
		assertEquals(fileSystemFileContents, galaxyFileContents);
		
		// delete original file
		assertTrue(dataFileTemp1.delete());
		
		// file contents should be the same (no link)
		galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
				contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
		assertEquals(fileSystemFileContents, galaxyFileContents);
	}
	
	@Test
	public void testUploadSampleLink() throws URISyntaxException, LibraryUploadException, InterruptedException, IOException
	{
		restAPIGalaxy = new GalaxyAPI(localGalaxy.getGalaxyURL(), localGalaxy.getAdminName(),
				localGalaxy.getAdminAPIKey(), true);
		
		String libraryName = "testUploadSampleLink";
		
		File dataFileTemp1 = createTemporaryDataFile();
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFileTemp1);
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getUser1Name()));
		
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
		String galaxyFileContents = getGalaxyFileContents(libraryName, "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
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
			galaxyFileContents = getGalaxyFileContents(libraryName + "Deleted", "testData1.fastq", localGalaxy.getGalaxyInstanceUser1(),
					contentsMapRegularUser.get("/illumina_reads/testData/testData1.fastq").getId());
			fail("No exception when attempting to download");
		} catch (Exception e){}
	}
	
	@Test(expected=RuntimeException.class)
	public void testUploadSampleWrongGalaxyAddress() throws URISyntaxException, LibraryUploadException
	{		
		restAPIGalaxy = new GalaxyAPI(localGalaxy.getInvalidGalaxyURL(),
				localGalaxy.getAdminName(), localGalaxy.getAdminAPIKey(), false);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testUploadSampleWrongUser() throws URISyntaxException, LibraryUploadException
	{	
		String libraryName = "testUploadSampleWrongUser";
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getInvalidGalaxyUserName());
	}
	
	@Test
	public void testUploadSampleMultipleFile() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleMultipleFile";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName()));
		
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
	
	private URL libraryToFullURL(Library library) throws MalformedURLException
	{
		String urlPath = library.getUrl();
		String domainPath = restAPIGalaxy.getGalaxyUrl();
		
		if (domainPath.endsWith("/"))
		{
			domainPath = domainPath.substring(0, domainPath.length() -1);
		}
		
		if (urlPath.startsWith("/"))
		{
			urlPath = urlPath.substring(1);
		}
		
		return new URL(domainPath + "/" + urlPath);
	}
	
	@Test
	public void testUploadSampleToExistingLibrary() throws URISyntaxException, LibraryUploadException, CreateLibraryException, MalformedURLException
	{
		GalaxySearch galaxySearch = new GalaxySearch(localGalaxy.getGalaxyInstanceAdmin());
		GalaxyLibrary galaxyLibrary = new GalaxyLibrary(localGalaxy.getGalaxyInstanceAdmin(), galaxySearch);
		
		String libraryName = "testUploadSampleToExistingSampleFolder";
		
		// build data library structure with no data in it
		Library returnedLibrary = restAPIGalaxy.buildGalaxyLibrary(libraryName, localGalaxy.getAdminName());
		String libraryId = returnedLibrary.getId();
		assertNotNull(libraryId);
		
		URL returnedLibraryURL = libraryToFullURL(returnedLibrary);
		
		Library library = galaxySearch.findLibraryWithId(libraryId);
		assertNotNull(library);
		
		LibraryFolder illuminaFolder = galaxyLibrary.createLibraryFolder(library, "illumina_reads");
		assertNotNull(illuminaFolder);
		
		LibraryFolder sampleFolder = galaxyLibrary.createLibraryFolder(library, illuminaFolder, "testData");
		assertNotNull(sampleFolder);
		
		LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(library, "references");
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
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		URL returnedLibrary2URL = restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(returnedLibrary2URL);
		assertEquals(returnedLibraryURL, returnedLibrary2URL);
		
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
	public void testUploadSampleOneFileAlreadyExists() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSampleOneFileAlreadyExists";
		
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		URL returnedLibraryURL = restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(returnedLibraryURL);
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
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
		galaxySample = new GalaxySample("testData", dataFilesDouble);
		samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		// make sure both libraries are the same
		URL returnedLibraryURL2 = restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName());
		assertNotNull(returnedLibraryURL2);
		assertEquals(returnedLibraryURL, returnedLibraryURL2);
		
		actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
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
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException
	{
		String libraryName = "testUploadSamples";
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		assertNotNull(restAPIGalaxy.uploadSamples(samples, libraryName, localGalaxy.getAdminName()));
		
		Library actualLibrary = findLibraryByName(libraryName, localGalaxy.getGalaxyInstanceAdmin());
		assertNotNull(actualLibrary);
		
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
