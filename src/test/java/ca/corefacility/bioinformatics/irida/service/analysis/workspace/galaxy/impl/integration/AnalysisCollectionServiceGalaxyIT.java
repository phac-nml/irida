package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.integration;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Tests out preparing a workspace for execution of workflows in Galaxy.
 *
 *
 */
@GalaxyIntegrationTest
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
	@BeforeEach
	public void setup() throws URISyntaxException, IOException, IridaWorkflowLoadException {
		assumeFalse(WindowsPlatformCondition.isWindows());

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
		Set<SequencingObject> sequenceFiles = Sets
				.newHashSet(databaseSetupGalaxyITService.setupSequencingObjectInDatabase(1L, sequenceFilePathA));

		Sample sample = sampleRepository.findById(1L).orElse(null);

		SequencingObject sequenceFile = sequenceFiles.iterator().next();

		Map<Sample, SequencingObject> sampleSequenceFiles = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(sequenceFiles);
		assertEquals(1, sampleSequenceFiles.size(), "sampleSequenceFiles map has size != 1");
		assertEquals(sequenceFile, sampleSequenceFiles.get(sample),
				"sampleSequenceFiles map does not have sequenceFile " + sequenceFile + " corresponding to sample "
						+ sample);
	}

	/**
	 * Tests failing to get a map of samples and sequence files (single).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFileSingleSamplesFail() throws DuplicateSampleException {
		List<SingleEndSequenceFile> seqObjects = databaseSetupGalaxyITService.setupSequencingObjectInDatabase(1L,
				sequenceFilePathA, sequenceFilePath2A);

		assertThrows(DuplicateSampleException.class, () -> {
			sequencingObjectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(seqObjects));
		});
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
		Sample sample = sampleRepository.findById(1L).orElse(null);
		SequenceFilePair sequenceFilePair = sequenceFiles.iterator().next();

		Map<Sample, SequenceFilePair> sampleSequenceFilePairs = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(sequenceFiles);

		assertEquals(1, sampleSequenceFilePairs.size(), "sampleSequenceFiles map has size != 1");
		assertEquals(sequenceFilePair, sampleSequenceFilePairs.get(sample),
				"sampleSequenceFiles map does not have sequenceFilePair " + sequenceFilePair
						+ " corresponding to sample " + sample);
	}

	/**
	 * Tests failing to get a map of samples and sequence files (pair).
	 *
	 * @throws DuplicateSampleException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetSequenceFilePairSamplesFail() throws DuplicateSampleException {
		Set<SequenceFilePair> sequenceFiles = Sets.newHashSet(databaseSetupGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, pairSequenceFiles1AB, pairSequenceFiles2AB));
		assertThrows(DuplicateSampleException.class, () -> {
			sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles);
		});
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

		Map<Sample, SingleEndSequenceFile> sampleSequenceFiles = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		Sample sample1 = sampleRepository.findById(1L).orElse(null);

		CollectionResponse collectionResponse = analysisCollectionServiceGalaxy
				.uploadSequenceFilesSingleEnd(sampleSequenceFiles, createdHistory, createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(2, historyContents.size(), "historyContents should have size 2");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFilePathA.toFile().getName()),
				"sequenceFile should have been uploaded to history");
		assertTrue(contentsMap.containsKey(INPUTS_SINGLE_NAME),
				"dataset collection with name " + INPUTS_SINGLE_NAME + " should have been created in history");

		Dataset sequenceFileACompressedDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePathA.getFileName().toString()).getId());
		assertEquals(InputFileType.FASTQ_SANGER.toString(), sequenceFileACompressedDataset.getDataTypeExt(),
				"Invalid file type");

		// verify correct collection has been created
		assertEquals(DatasetCollectionType.LIST.toString(), collectionResponse.getCollectionType(),
				"constructed dataset collection should have been " + DatasetCollectionType.LIST + " but is instead "
						+ collectionResponse.getCollectionType());
		List<CollectionElementResponse> collectionElements = collectionResponse.getElements();
		assertEquals(1, collectionElements.size(), "dataset collection should have only 1 element");
		Map<String, CollectionElementResponse> collectionElementsMap = collectionElementsAsMap(collectionElements);
		assertTrue(collectionElementsMap.containsKey(sample1.getSampleName()),
				"dataset collection should have an element with the name " + sample1.getSampleName());
		CollectionElementResponse sample1Response = collectionElementsMap.get(sample1.getSampleName());
		assertEquals(HISTORY_DATASET_NAME, sample1Response.getElementType(), "invalid type for dataset element");
	}

	/**
	 * Tests successfully uploading a compressed single end sequence file to
	 * Galaxy and constructing a collection.
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
			assertTrue(file.getFile().toString().endsWith(".gz"), "Sequence files were uncompressed");
		}

		Map<Sample, SingleEndSequenceFile> sampleSequenceFiles = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		analysisCollectionServiceGalaxy.uploadSequenceFilesSingleEnd(sampleSequenceFiles, createdHistory,
				createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(2, historyContents.size(), "historyContents should have size 2");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFileNameCompressedA),
				"sequenceFile should have been uploaded to history");
		assertTrue(contentsMap.containsKey(INPUTS_SINGLE_NAME),
				"dataset collection with name " + INPUTS_SINGLE_NAME + " should have been created in history");

		// verify correct file types
		Dataset sequenceFileACompressedDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedA).getId());
		assertEquals(InputFileType.FASTQ_SANGER_GZ.toString(), sequenceFileACompressedDataset.getDataTypeExt(),
				"Invalid file type");
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

		Map<Sample, SequenceFilePair> sampleSequenceFilePairs = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		Sample sample1 = sampleRepository.findById(1L).orElse(null);

		CollectionResponse collectionResponse = analysisCollectionServiceGalaxy
				.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory, createdLibrary);

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(3, historyContents.size(), "history does not have correct number of files");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFilePathA.toFile().getName()),
				"the history should have a sequence file with name " + sequenceFilePathA.toFile().getName());
		assertTrue(contentsMap.containsKey(sequenceFilePath2A.toFile().getName()),
				"the history should have a file with name " + sequenceFilePath2A.toFile().getName());
		assertTrue(contentsMap.containsKey(INPUTS_PAIRED_NAME),
				"the history should have a dataset collection with name " + INPUTS_PAIRED_NAME);

		// verify correct file types
		Dataset sequenceFileADataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePathA.toFile().getName()).getId());
		Dataset sequenceFileBDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFilePath2A.toFile().getName()).getId());
		assertEquals(InputFileType.FASTQ_SANGER.toString(), sequenceFileADataset.getDataTypeExt(), "Invalid file type");
		assertEquals(InputFileType.FASTQ_SANGER.toString(), sequenceFileBDataset.getDataTypeExt(), "Invalid file type");

		// verify correct collection has been created
		assertEquals(DatasetCollectionType.LIST_PAIRED.toString(), collectionResponse.getCollectionType(),
				"invalid type of dataset collection created");
		List<CollectionElementResponse> collectionElements = collectionResponse.getElements();
		assertEquals(1, collectionElements.size(), "invalid number of elements in the dataset collection");
		Map<String, CollectionElementResponse> collectionElementsMap = collectionElementsAsMap(collectionElements);
		assertTrue(collectionElementsMap.containsKey(sample1.getSampleName()),
				"the dataset collection element should have name " + sample1.getSampleName());
		CollectionElementResponse sample1Response = collectionElementsMap.get(sample1.getSampleName());

		// verify collection has 2 files (paired end data)
		ElementResponse subElements = sample1Response.getResponseElement();
		assertEquals(CollectionResponse.class, subElements.getClass(),
				"invalid class for sub-element in dataset collection");
		CollectionResponse subElementsCollection = (CollectionResponse) subElements;
		assertEquals(DatasetCollectionType.PAIRED.toString(), subElementsCollection.getCollectionType(),
				"invalid type for sub-element in dataset collection");
		List<CollectionElementResponse> subCollectionElements = subElementsCollection.getElements();
		assertEquals(2, subCollectionElements.size(), "invalid number of files for paired dataset collection element");
		Map<String, CollectionElementResponse> subCollectionElementsMap = collectionElementsAsMap(
				subCollectionElements);
		assertTrue(subCollectionElementsMap.containsKey(FORWARD_NAME),
				"dataset collection should have a sub-element with name " + FORWARD_NAME);
		assertTrue(subCollectionElementsMap.containsKey(REVERSE_NAME),
				"dataset collection should have a sub-element with name " + REVERSE_NAME);

		// verify paired-end files are correct type in collection
		CollectionElementResponse sequenceFile1 = subCollectionElementsMap.get(FORWARD_NAME);
		CollectionElementResponse sequenceFile2 = subCollectionElementsMap.get(REVERSE_NAME);
		assertEquals(HISTORY_DATASET_NAME, sequenceFile1.getElementType(),
				"the " + FORWARD_NAME + " sub-element should be a history dataset");
		assertEquals(HISTORY_DATASET_NAME, sequenceFile2.getElementType(),
				"the " + REVERSE_NAME + " sub-element should be a history dataset");

		// verify paired-end files are in correct order in collection
		ElementResponse sequenceFile1Response = sequenceFile1.getResponseElement();
		assertEquals(Dataset.class, sequenceFile1Response.getClass(),
				"the " + FORWARD_NAME + " element is not of the correct type");
		ElementResponse sequenceFile2Response = sequenceFile2.getResponseElement();
		assertEquals(Dataset.class, sequenceFile2Response.getClass(),
				"the " + REVERSE_NAME + " element is not of the correct type");
		Dataset sequenceFile1Dataset = (Dataset) sequenceFile1Response;
		assertEquals(sequenceFilePathA.getFileName().toString(), sequenceFile1Dataset.getName(),
				"forward file in Galaxy is named incorrectly");
		Dataset sequenceFile2Dataset = (Dataset) sequenceFile2Response;
		assertEquals(sequenceFilePath2A.getFileName().toString(), sequenceFile2Dataset.getName(),
				"reverse file in Galaxy is named incorrectly");
	}

	/**
	 * Tests successfully uploading a compressed paired-end sequence file to
	 * Galaxy and constructing a collection.
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

		Map<Sample, SequenceFilePair> sampleSequenceFilePairs = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory,
				createdLibrary);

		SequenceFilePair pairedSequenceFile = sequenceFiles.iterator().next();
		for (SequenceFile file : pairedSequenceFile.getFiles()) {
			assertTrue(file.getFile().toString().endsWith(".gz"), "Sequence files were uncompressed");
		}

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(3, historyContents.size(), "history does not have correct number of files");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFileNameCompressedA),
				"the history should have a sequence file with name " + sequenceFileNameCompressedA);
		assertTrue(contentsMap.containsKey(sequenceFileNameCompressedB),
				"the history should have a file with name " + sequenceFileNameCompressedB);
		assertTrue(contentsMap.containsKey(INPUTS_PAIRED_NAME),
				"the history should have a dataset collection with name " + INPUTS_PAIRED_NAME);

		// verify correct file types
		Dataset sequenceFileADataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedA).getId());
		Dataset sequenceFileBDataset = historiesClient.showDataset(createdHistory.getId(),
				contentsMap.get(sequenceFileNameCompressedB).getId());
		assertEquals(InputFileType.FASTQ_SANGER_GZ.toString(), sequenceFileADataset.getDataTypeExt(),
				"Invalid file type");
		assertEquals(InputFileType.FASTQ_SANGER_GZ.toString(), sequenceFileBDataset.getDataTypeExt(),
				"Invalid file type");
	}

	/**
	 * Tests failing to upload a paired-end sequence file to Galaxy and
	 * constructing a collection due to no found forward file.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 */
	@Test
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
		Map<Sample, SequenceFilePair> sampleSequenceFilePairs = new HashMap<>(
				sequencingObjectService.getUniqueSamplesForSequencingObjects(sequenceFiles));

		assertThrows(NoSuchElementException.class, () -> {
			analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairs, createdHistory,
					createdLibrary);
		});
	}

	private Map<String, HistoryContents> historyContentsAsMap(List<HistoryContents> historyContents) {
		return historyContents.stream()
				.collect(Collectors.toMap(HistoryContents::getName, historyContent -> historyContent));
	}

	private Map<String, CollectionElementResponse> collectionElementsAsMap(
			List<CollectionElementResponse> collectionElements) {
		return collectionElements.stream().collect(Collectors.toMap(CollectionElementResponse::getElementIdentifier,
				collectionElement -> collectionElement));
	}
}
