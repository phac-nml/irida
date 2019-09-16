package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionElementResponse;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.ElementResponse;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Tests out preparing a workspace for execution of workflows in Galaxy.
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisCollectionServiceGalaxyIT {

	@Autowired
	private DatabaseSetupGalaxyITService databaseSetupGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SequencingObjectService sequencingObjectService;

	@Autowired
	private GzipFileProcessor gzipFileProcessor;

	@Autowired
	@Qualifier("rootTempDirectory")
	private Path rootTempDirectory;

	private String sequenceFileNameCompressedA = "testDataCompressed_1.fastq.gz";
	private String sequenceFileNameCompressedB = "testDataCompressed_2.fastq.gz";

	private Path sequenceFilePathA;
	private Path sequenceFilePathAInvalidName;
	private Path sequenceFilePath2A;
	private Path sequenceFilePathB;
	private Path sequenceFilePath2B;
	private Path sequenceFilePathCompressedA;
	private Path sequenceFilePathCompressedB;

	private List<Path> pairSequenceFiles1A;
	private List<Path> pairSequenceFiles1AInvalidName;
	private List<Path> pairSequenceFiles2A;

	private List<Path> pairSequenceFiles1AB;
	private List<Path> pairSequenceFiles2AB;

	private List<Path> pairSequenceFilesCompressedA;
	private List<Path> pairSequenceFilesCompressedB;

	private static final String INPUTS_SINGLE_NAME = "irida_sequence_files_single";
	private static final String INPUTS_PAIRED_NAME = "irida_sequence_files_paired";
	private static final String HISTORY_DATASET_NAME = "hda";
	private static final String FORWARD_NAME = "forward";
	private static final String REVERSE_NAME = "reverse";

	/**
	 * Sets up variables for testing.
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException, IridaWorkflowLoadException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());

		Path sequenceFilePathRealCompressed = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData3.fastq.gz").toURI());

		Path tempDir = Files.createTempDirectory(rootTempDirectory, "analysisCollectionTest");

		sequenceFilePathA = tempDir.resolve("testDataA_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePathA, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathAInvalidName = tempDir.resolve("testDataA_R_INVALID_1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePathAInvalidName, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePath2A = tempDir.resolve("testDataA_R2_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath2A, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathB = tempDir.resolve("testDataB_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePathB, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePath2B = tempDir.resolve("testDataB_R2_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath2B, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathCompressedA = tempDir.resolve(sequenceFileNameCompressedA);
		Files.copy(sequenceFilePathRealCompressed, sequenceFilePathCompressedA, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathCompressedB = tempDir.resolve(sequenceFileNameCompressedB);
		Files.copy(sequenceFilePathRealCompressed, sequenceFilePathCompressedB, StandardCopyOption.REPLACE_EXISTING);

		pairSequenceFiles1A = new ArrayList<>();
		pairSequenceFiles1A.add(sequenceFilePathA);
		pairSequenceFiles2A = new ArrayList<>();
		pairSequenceFiles2A.add(sequenceFilePath2A);

		pairSequenceFiles1AInvalidName = new ArrayList<>();
		pairSequenceFiles1AInvalidName.add(sequenceFilePathAInvalidName);

		pairSequenceFiles1AB = new ArrayList<>();
		pairSequenceFiles1AB.add(sequenceFilePathA);
		pairSequenceFiles1AB.add(sequenceFilePathB);
		pairSequenceFiles2AB = new ArrayList<>();
		pairSequenceFiles2AB.add(sequenceFilePath2A);
		pairSequenceFiles2AB.add(sequenceFilePath2B);

		pairSequenceFilesCompressedA = new ArrayList<>();
		pairSequenceFilesCompressedA.add(sequenceFilePathCompressedA);
		pairSequenceFilesCompressedB = new ArrayList<>();
		pairSequenceFilesCompressedB.add(sequenceFilePathCompressedB);

		gzipFileProcessor.setDisableFileProcessor(false);
	}

	/**
	 * Tests successfully getting a map of samples and sequence files (single).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFileSingleSamplesSuccess() throws DuplicateSampleException {
		Set<SequencingObject> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSequencingObjectInDatabase(1L, sequenceFilePathA));

		Sample sample = sampleRepository.findOne(1L);

		SequencingObject sequenceFile = sequenceFiles.iterator().next();

		Map<Sample, SequencingObject> sampleSequenceFiles = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(sequenceFiles);
		assertEquals("sampleSequenceFiles map has size != 1", 1, sampleSequenceFiles.size());
		assertEquals("sampleSequenceFiles map does not have sequenceFile " + sequenceFile + " corresponding to sample "
				+ sample, sequenceFile, sampleSequenceFiles.get(sample));
	}

	/**
	 * Tests failing to get a map of samples and sequence files (single).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test(expected = DuplicateSampleException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFileSingleSamplesFail() throws DuplicateSampleException {
		List<SingleEndSequenceFile> seqObjects = databaseSetupGalaxyITService
				.setupSequencingObjectInDatabase(1L, sequenceFilePathA, sequenceFilePath2A);

		sequencingObjectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(seqObjects));
	}

	/**
	 * Tests successfully getting a map of samples and sequence files (pair).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFilePairSamplesSuccess() throws DuplicateSampleException {
		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFiles1A, pairSequenceFiles2A));
		Sample sample = sampleRepository.findOne(1L);
		SequenceFilePair sequenceFilePair = sequenceFiles.iterator().next();

		Map<Sample, SequenceFilePair> sampleSequenceFilePairs = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(sequenceFiles);

		assertEquals("sampleSequenceFiles map has size != 1", 1, sampleSequenceFilePairs.size());
		assertEquals("sampleSequenceFiles map does not have sequenceFilePair " + sequenceFilePair
				+ " corresponding to sample " + sample, sequenceFilePair, sampleSequenceFilePairs.get(sample));
	}

	/**
	 * Tests failing to get a map of samples and sequence files (pair).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test(expected = DuplicateSampleException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFilePairSamplesFail() throws DuplicateSampleException {
		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFiles1AB, pairSequenceFiles2AB));
		sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles);
	}

	/**
	 * Tests successfully uploading a single end sequence file to Galaxy and
	 * constructing a collection.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSequenceFilesSingleSuccess() throws ExecutionManagerException, IOException {

		History history = new History();
		history.setName("testUploadSequenceFilesSingleSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		Library library = new Library();
		library.setName("testUploadSequenceFilesSingleSuccess");
		Library createdLibrary = librariesClient.createLibrary(library);

		Set<SingleEndSequenceFile> sequenceFiles = Sets
				.newHashSet(databaseSetupGalaxyITService.setupSequencingObjectInDatabase(1L, sequenceFilePathA));

		Map<Sample, IridaSingleEndSequenceFile> sampleSequenceFiles = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		Sample sample1 = sampleRepository.findOne(1L);

		CollectionResponse collectionResponse = analysisCollectionServiceGalaxy
				.uploadSequenceFilesSingleEnd(sampleSequenceFiles, createdHistory, createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals("historyContents should have size 2", 2, historyContents.size());
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue("sequenceFile should have been uploaded to history",
				contentsMap.containsKey(sequenceFilePathA.toFile().getName()));
		assertTrue("dataset collection with name " + INPUTS_SINGLE_NAME + " should have been created in history",
				contentsMap.containsKey(INPUTS_SINGLE_NAME));

		Dataset sequenceFileACompressedDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePathA.getFileName().toString()).getId());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER.toString(),
				sequenceFileACompressedDataset.getDataTypeExt());

		// verify correct collection has been created
		assertEquals(
				"constructed dataset collection should have been " + DatasetCollectionType.LIST + " but is instead "
						+ collectionResponse.getCollectionType(),
				DatasetCollectionType.LIST.toString(), collectionResponse.getCollectionType());
		List<CollectionElementResponse> collectionElements = collectionResponse.getElements();
		assertEquals("dataset collection should have only 1 element", 1, collectionElements.size());
		Map<String, CollectionElementResponse> collectionElementsMap = collectionElementsAsMap(collectionElements);
		assertTrue("dataset collection should have an element with the name " + sample1.getSampleName(),
				collectionElementsMap.containsKey(sample1.getSampleName()));
		CollectionElementResponse sample1Response = collectionElementsMap.get(sample1.getSampleName());
		assertEquals("invalid type for dataset element", HISTORY_DATASET_NAME, sample1Response.getElementType());
	}

	/**
	 * Tests successfully uploading a compressed single end sequence file to Galaxy and
	 * constructing a collection.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSequenceFilesSingleCompressedSuccess() throws ExecutionManagerException, IOException {
		gzipFileProcessor.setDisableFileProcessor(true);

		History history = new History();
		history.setName("testUploadSequenceFilesSingleCompressedSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		Library library = new Library();
		library.setName("testUploadSequenceFilesSingleCompressedSuccess");
		Library createdLibrary = librariesClient.createLibrary(library);

		Set<SingleEndSequenceFile> sequenceFiles = Sets.newHashSet(
				databaseSetupGalaxyITService.setupSequencingObjectInDatabase(1L, sequenceFilePathCompressedA));

		SingleEndSequenceFile singleEndSequenceFile = sequenceFiles.iterator().next();
		for (SequenceFile file : singleEndSequenceFile.getFiles()) {
			assertTrue("Sequence files were uncompressed", file.getFile().toString().endsWith(".gz"));
		}

		Map<Sample, IridaSingleEndSequenceFile> sampleSequenceFiles = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		analysisCollectionServiceGalaxy.uploadSequenceFilesSingleEnd(sampleSequenceFiles, createdHistory,
				createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals("historyContents should have size 2", 2, historyContents.size());
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue("sequenceFile should have been uploaded to history",
				contentsMap.containsKey(sequenceFileNameCompressedA));
		assertTrue("dataset collection with name " + INPUTS_SINGLE_NAME + " should have been created in history",
				contentsMap.containsKey(INPUTS_SINGLE_NAME));

		// verify correct file types
		Dataset sequenceFileACompressedDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedA).getId());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER_GZ.toString(),
				sequenceFileACompressedDataset.getDataTypeExt());
	}

	/**
	 * Tests successfully uploading a paired-end sequence file to Galaxy and
	 * constructing a collection.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSequenceFilesPairedSuccess() throws ExecutionManagerException, IOException {

		History history = new History();
		history.setName("testUploadSequenceFilesPaired");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		Library library = new Library();
		library.setName("testUploadSequenceFilesPaired");
		Library createdLibrary = librariesClient.createLibrary(library);

		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFiles1A, pairSequenceFiles2A));

		Map<Sample, IridaSequenceFilePair> sampleSequenceFilePairs = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		Sample sample1 = sampleRepository.findOne(1L);

		CollectionResponse collectionResponse = analysisCollectionServiceGalaxy
				.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory, createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals("history does not have correct number of files", 3, historyContents.size());
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue("the history should have a sequence file with name " + sequenceFilePathA.toFile().getName(),
				contentsMap.containsKey(sequenceFilePathA.toFile().getName()));
		assertTrue("the history should have a file with name " + sequenceFilePath2A.toFile().getName(),
				contentsMap.containsKey(sequenceFilePath2A.toFile().getName()));
		assertTrue("the history should have a dataset collection with name " + INPUTS_PAIRED_NAME,
				contentsMap.containsKey(INPUTS_PAIRED_NAME));

		// verify correct file types
		Dataset sequenceFileADataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePathA.toFile().getName()).getId());
		Dataset sequenceFileBDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePath2A.toFile().getName()).getId());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER.toString(), sequenceFileADataset.getDataTypeExt());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER.toString(), sequenceFileBDataset.getDataTypeExt());

		// verify correct collection has been created
		assertEquals("invalid type of dataset collection created", DatasetCollectionType.LIST_PAIRED.toString(),
				collectionResponse.getCollectionType());
		List<CollectionElementResponse> collectionElements = collectionResponse.getElements();
		assertEquals("invalid number of elements in the dataset collection", 1, collectionElements.size());
		Map<String, CollectionElementResponse> collectionElementsMap = collectionElementsAsMap(collectionElements);
		assertTrue("the dataset collection element should have name " + sample1.getSampleName(),
				collectionElementsMap.containsKey(sample1.getSampleName()));
		CollectionElementResponse sample1Response = collectionElementsMap.get(sample1.getSampleName());

		// verify collection has 2 files (paired end data)
		ElementResponse subElements = sample1Response.getResponseElement();
		assertEquals("invalid class for sub-element in dataset collection", CollectionResponse.class,
				subElements.getClass());
		CollectionResponse subElementsCollection = (CollectionResponse) subElements;
		assertEquals("invalid type for sub-element in dataset collection", DatasetCollectionType.PAIRED.toString(),
				subElementsCollection.getCollectionType());
		List<CollectionElementResponse> subCollectionElements = subElementsCollection.getElements();
		assertEquals("invalid number of files for paired dataset collection element", 2, subCollectionElements.size());
		Map<String, CollectionElementResponse> subCollectionElementsMap = collectionElementsAsMap(
				subCollectionElements);
		assertTrue("dataset collection should have a sub-element with name " + FORWARD_NAME,
				subCollectionElementsMap.containsKey(FORWARD_NAME));
		assertTrue("dataset collection should have a sub-element with name " + REVERSE_NAME,
				subCollectionElementsMap.containsKey(REVERSE_NAME));

		// verify paired-end files are correct type in collection
		CollectionElementResponse sequenceFile1 = subCollectionElementsMap.get(FORWARD_NAME);
		CollectionElementResponse sequenceFile2 = subCollectionElementsMap.get(REVERSE_NAME);
		assertEquals("the " + FORWARD_NAME + " sub-element should be a history dataset", HISTORY_DATASET_NAME,
				sequenceFile1.getElementType());
		assertEquals("the " + REVERSE_NAME + " sub-element should be a history dataset", HISTORY_DATASET_NAME,
				sequenceFile2.getElementType());

		// verify paired-end files are in correct order in collection
		ElementResponse sequenceFile1Response = sequenceFile1.getResponseElement();
		assertEquals("the " + FORWARD_NAME + " element is not of the correct type", Dataset.class,
				sequenceFile1Response.getClass());
		ElementResponse sequenceFile2Response = sequenceFile2.getResponseElement();
		assertEquals("the " + REVERSE_NAME + " element is not of the correct type", Dataset.class,
				sequenceFile2Response.getClass());
		Dataset sequenceFile1Dataset = (Dataset) sequenceFile1Response;
		assertEquals("forward file in Galaxy is named incorrectly", sequenceFilePathA.getFileName().toString(),
				sequenceFile1Dataset.getName());
		Dataset sequenceFile2Dataset = (Dataset) sequenceFile2Response;
		assertEquals("reverse file in Galaxy is named incorrectly", sequenceFilePath2A.getFileName().toString(),
				sequenceFile2Dataset.getName());
	}

	/**
	 * Tests successfully uploading a compressed paired-end sequence file to Galaxy
	 * and constructing a collection.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSequenceFilesPairedCompressedSuccess() throws ExecutionManagerException, IOException {
		gzipFileProcessor.setDisableFileProcessor(true);

		History history = new History();
		history.setName("testUploadSequenceFilesPairedCompressedSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		Library library = new Library();
		library.setName("testUploadSequenceFilesPairedCompressedSuccess");
		Library createdLibrary = librariesClient.createLibrary(library);

		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFilesCompressedA, pairSequenceFilesCompressedB));

		Map<Sample, IridaSequenceFilePair> sampleSequenceFilePairs = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory,
				createdLibrary);

		SequenceFilePair pairedSequenceFile = sequenceFiles.iterator().next();
		for (SequenceFile file : pairedSequenceFile.getFiles()) {
			assertTrue("Sequence files were uncompressed", file.getFile().toString().endsWith(".gz"));
		}

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals("history does not have correct number of files", 3, historyContents.size());
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(
				"the history should have a sequence file with name " + sequenceFileNameCompressedA,
				contentsMap.containsKey(sequenceFileNameCompressedA));
		assertTrue("the history should have a file with name " + sequenceFileNameCompressedB,
				contentsMap.containsKey(sequenceFileNameCompressedB));
		assertTrue("the history should have a dataset collection with name " + INPUTS_PAIRED_NAME,
				contentsMap.containsKey(INPUTS_PAIRED_NAME));

		// verify correct file types
		Dataset sequenceFileADataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedA).getId());
		Dataset sequenceFileBDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedB).getId());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER_GZ.toString(),
				sequenceFileADataset.getDataTypeExt());
		assertEquals("Invalid file type", InputFileType.FASTQ_SANGER_GZ.toString(),
				sequenceFileBDataset.getDataTypeExt());
	}

	/**
	 * Tests failing to upload a paired-end sequence file to Galaxy and
	 * constructing a collection due to no found forward file.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test(expected = NoSuchElementException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSequenceFilesPairedFailForward() throws ExecutionManagerException, IOException {

		History history = new History();
		history.setName("testUploadSequenceFilesPairedFailForward");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		Library library = new Library();
		library.setName("testUploadSequenceFilesPairedFailForward");
		Library createdLibrary = librariesClient.createLibrary(library);

		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFiles1AInvalidName, pairSequenceFiles2A));
		Map<Sample, IridaSequenceFilePair> sampleSequenceFilePairs = new HashMap<>(sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory,
				createdLibrary);
	}

	private Map<String, HistoryContents> historyContentsAsMap(List<HistoryContents> historyContents) {
		return historyContents.stream().collect(Collectors.toMap(HistoryContents::getName, historyContent->historyContent));
	}

	private Map<String, CollectionElementResponse> collectionElementsAsMap(
			List<CollectionElementResponse> collectionElements) {
		return collectionElements.stream().collect(Collectors.toMap(CollectionElementResponse::getElementIdentifier, collectionElement->collectionElement));
	}
}
