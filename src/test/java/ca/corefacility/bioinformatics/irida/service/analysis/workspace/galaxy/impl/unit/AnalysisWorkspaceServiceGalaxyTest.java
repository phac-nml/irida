package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SampleAnalysisDuplicateException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowTestBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests out preparing a Galaxy Phylogenomics Pipeline workflow for execution.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisWorkspaceServiceGalaxyTest {

	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowService galaxyWorkflowService;
	@Mock
	private GalaxyLibraryBuilder libraryBuilder;
	@Mock
	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;
	@Mock
	private List<Dataset> sequenceDatasets;
	@Mock
	private Dataset refDataset;

	@Mock
	private SequenceFileRepository sequenceFileRepository;

	@Mock
	private IridaWorkflowsService iridaWorkflowsService;

	@Mock
	private AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy;

	private AnalysisWorkspaceServiceGalaxy workflowPreparation;

	private Set<SequenceFile> inputFiles;
	private ReferenceFile referenceFile;
	private Path refFile;
	private AnalysisSubmission submission;
	private WorkflowDetails workflowDetails;

	private History workflowHistory;
	private Library workflowLibrary;

	private static final String HISTORY_ID = "10";
	private static final String WORKFLOW_ID = "11";
	private static final String SEQUENCE_FILE_SINGLE_LABEL = "sequence_reads";
	private static final String SEQUENCE_FILE_PAIRED_LABEL = "sequence_reads_paired";
	private static final String REFERENCE_FILE_LABEL = "reference";
	private static final String SEQUENCE_FILE_SINGLE_ID = "12";
	private static final String SEQUENCE_FILE_PAIRED_ID = "13";
	private static final String REFERENCE_FILE_ID = "14";
	private static final String COLLECTION_SINGLE_ID = "20";
	private static final String COLLECTION_PAIRED_ID = "21";

	private SequenceFile sFileA;
	private SequenceFile sFileB;
	private SequenceFile sFileC;

	private Dataset output1Dataset;
	private Dataset output2Dataset;
	private String output1Filename = "output1.txt";
	private String output2Filename = "output2.txt";

	private UUID workflowId = IridaWorkflowTestBuilder.DEFAULT_ID;
	private IridaWorkflow iridaWorkflowSingle = IridaWorkflowTestBuilder.buildTestWorkflowSingle();
	private IridaWorkflow iridaWorkflowPaired = IridaWorkflowTestBuilder.buildTestWorkflowPaired();
	private IridaWorkflow iridaWorkflowSinglePaired = IridaWorkflowTestBuilder.buildTestWorkflowSinglePaired();

	private Map<Sample, SequenceFile> sampleSequenceFileMap;
	private Map<Sample, SequenceFilePair> sampleSequenceFilePairMap;
	private Map<Sample, SequenceFilePair> sampleSequenceFilePairMapSampleA;
	private SequenceFilePair sequenceFilePair;

	private CollectionResponse collectionResponseSingle;
	private CollectionResponse collectionResponsePaired;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws IOException
	 * @throws GalaxyDatasetException
	 * @throws UploadException
	 */
	@Before
	public void setup() throws IOException, UploadException, GalaxyDatasetException {
		MockitoAnnotations.initMocks(this);

		sFileA = new SequenceFile(createTempFile("fileA", "fastq"));
		sFileB = new SequenceFile(createTempFile("fileB", "fastq"));
		sFileC = new SequenceFile(createTempFile("fileC", "fastq"));

		sequenceFilePair = new SequenceFilePair(sFileB, sFileC);

		Sample sampleA = new Sample();
		sampleA.setSampleName("SampleA");

		Sample sampleB = new Sample();
		sampleB.setSampleName("SampleB");

		Sample sampleC = new Sample();
		sampleC.setSampleName("SampleC");

		sampleSequenceFileMap = ImmutableMap.of(sampleA, sFileA);
		sampleSequenceFilePairMap = ImmutableMap.of(sampleB, sequenceFilePair);
		sampleSequenceFilePairMapSampleA = ImmutableMap.of(sampleA, sequenceFilePair);

		refFile = createTempFile("reference", "fasta");
		referenceFile = new ReferenceFile(refFile);

		inputFiles = new HashSet<>();
		inputFiles.addAll(Arrays.asList(sFileA, sFileB, sFileC));

		submission = AnalysisSubmission.createSubmissionSingleReference("my analysis", inputFiles, referenceFile,
				workflowId);

		workflowHistory = new History();
		workflowHistory.setId(HISTORY_ID);

		workflowLibrary = new Library();
		workflowLibrary.setId("1");

		workflowDetails = new WorkflowDetails();
		workflowDetails.setId(WORKFLOW_ID);

		workflowPreparation = new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService, galaxyWorkflowService,
				sequenceFileRepository, libraryBuilder, iridaWorkflowsService, analysisCollectionServiceGalaxy);

		output1Dataset = new Dataset();
		output1Dataset.setId("1");
		output1Dataset.setName("output1.txt");

		output2Dataset = new Dataset();
		output2Dataset.setId("2");
		output2Dataset.setName("output2.txt");

		collectionResponseSingle = new CollectionResponse();
		collectionResponseSingle.setId(COLLECTION_SINGLE_ID);
		collectionResponsePaired = new CollectionResponse();
		collectionResponsePaired.setId(COLLECTION_PAIRED_ID);
	}

	private Path createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();

		return file.toPath();
	}

	/**
	 * Tests out successfully to preparing an analysis workspace
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		assertEquals("history id is invalid", HISTORY_ID, workflowPreparation.prepareAnalysisWorkspace(submission));
	}

	/**
	 * Tests out failing to preparing an analysis workspace
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test(expected = RuntimeException.class)
	public void testPrepareAnalysisWorkspaceFail() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenThrow(new RuntimeException());
		assertEquals("history id is invalid", HISTORY_ID, workflowPreparation.prepareAnalysisWorkspace(submission));
	}

	/**
	 * Tests out successfully to preparing an analysis with both single and
	 * paired files
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesSinglePairedSuccess() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionSingleAndPairedReference("my analysis",
				Sets.newHashSet(sampleSequenceFileMap.values()), Sets.newHashSet(sampleSequenceFilePairMap.values()),
				referenceFile, workflowId);

		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSinglePaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class))).thenReturn(
				sampleSequenceFileMap);
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class))).thenReturn(
				sampleSequenceFilePairMap);

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_SINGLE_LABEL)).thenReturn(
				SEQUENCE_FILE_SINGLE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_PAIRED_LABEL)).thenReturn(
				SEQUENCE_FILE_PAIRED_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL)).thenReturn(
				REFERENCE_FILE_ID);

		when(
				analysisCollectionServiceGalaxy.uploadSequenceFilesSingle(sampleSequenceFileMap, workflowHistory,
						workflowLibrary)).thenReturn(collectionResponseSingle);
		when(
				analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairMap, workflowHistory,
						workflowLibrary)).thenReturn(collectionResponsePaired);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("invalid number of workflow inputs", 3, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference entry", workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file single entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_SINGLE_ID));
		assertTrue("workflow inputs should contain sequence file paired entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_PAIRED_ID));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesSingle(any(Map.class), any(History.class),
				any(Library.class));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out successfully to preparing an analysis with single files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesSingleSuccess() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionSingleReference("my analysis",
				Sets.newHashSet(sampleSequenceFileMap.values()), referenceFile, workflowId);
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class))).thenReturn(
				sampleSequenceFileMap);
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class)))
				.thenReturn(ImmutableMap.of());

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_SINGLE_LABEL)).thenReturn(
				SEQUENCE_FILE_SINGLE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL)).thenReturn(
				REFERENCE_FILE_ID);

		when(
				analysisCollectionServiceGalaxy.uploadSequenceFilesSingle(sampleSequenceFileMap, workflowHistory,
						workflowLibrary)).thenReturn(collectionResponseSingle);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("workflow inputs has invalid size", 2, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference file entry",
				workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file single entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_SINGLE_ID));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesSingle(any(Map.class), any(History.class),
				any(Library.class));
		verify(analysisCollectionServiceGalaxy, never()).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out successfully to preparing an analysis with paired files
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesPairedSuccess() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionPairedReference("my analysis",
				Sets.newHashSet(sampleSequenceFilePairMap.values()), referenceFile, workflowId);

		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class)))
				.thenReturn(ImmutableMap.of());
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class))).thenReturn(
				sampleSequenceFilePairMap);

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_PAIRED_LABEL)).thenReturn(
				SEQUENCE_FILE_PAIRED_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL)).thenReturn(
				REFERENCE_FILE_ID);

		when(
				analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilePairMap, workflowHistory,
						workflowLibrary)).thenReturn(collectionResponsePaired);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("workflow inputs has invalid size", 2, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference file entry",
				workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file paired entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_PAIRED_ID));
		verify(analysisCollectionServiceGalaxy, never()).uploadSequenceFilesSingle(any(Map.class), any(History.class),
				any(Library.class));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out failing to preparing an analysis due to duplicate samples
	 * between single and paired input files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = SampleAnalysisDuplicateException.class)
	public void testPrepareAnalysisFilesSinglePairedDuplicateFail() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionSingleAndPairedReference("my analysis",
				Sets.newHashSet(sampleSequenceFileMap.values()),
				Sets.newHashSet(sampleSequenceFilePairMapSampleA.values()), referenceFile, workflowId);
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSinglePaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class))).thenReturn(
				sampleSequenceFileMap);
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class))).thenReturn(
				sampleSequenceFilePairMapSampleA);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis with paired files when it
	 * cannot accept paired files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesPairedNoAcceptFail() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionPairedReference("my analysis",
				Sets.newHashSet(sampleSequenceFilePairMap.values()), referenceFile, workflowId);
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class)))
				.thenReturn(ImmutableMap.of());
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class))).thenReturn(
				sampleSequenceFilePairMap);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis with single files when it
	 * cannot accept single files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesSingleNoAcceptFail() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionSingleReference("my analysis",
				Sets.newHashSet(sampleSequenceFileMap.values()), referenceFile, workflowId);
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class))).thenReturn(
				sampleSequenceFileMap);
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class)))
				.thenReturn(ImmutableMap.of());

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis which is passed both single
	 * and paired files but only accepts paired files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesSinglePairedNoAcceptFail() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		submission = AnalysisSubmission.createSubmissionSingleAndPairedReference("my analysis",
				Sets.newHashSet(sampleSequenceFileMap.values()), Sets.newHashSet(sampleSequenceFilePairMap.values()),
				referenceFile, workflowId);
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(libraryBuilder.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(any(Set.class))).thenReturn(
				sampleSequenceFileMap);
		when(analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(any(Set.class))).thenReturn(
				sampleSequenceFilePairMap);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccess() throws IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException, ExecutionManagerException, IOException {
		submission = AnalysisSubmission.createSubmissionSingleReference("my analysis", Sets.newHashSet(),
				referenceFile, workflowId);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenReturn(output1Dataset);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output2Filename, HISTORY_ID)).thenReturn(output2Dataset);

		Analysis analysis = workflowPreparation.getAnalysisResults(submission);

		assertNotNull("analysis is not valid", analysis);
		assertEquals("invalid number of output files", 2, analysis.getAnalysisOutputFiles().size());
		assertEquals("missing output file for analysis", Paths.get("output1.txt"),
				analysis.getAnalysisOutputFile("output1").getFile().getFileName());
		assertEquals("missing output file for analysis", Paths.get("output2.txt"),
				analysis.getAnalysisOutputFile("output2").getFile().getFileName());

		verify(galaxyHistoriesService).getDatasetForFileInHistory("output1.txt", HISTORY_ID);
		verify(galaxyHistoriesService).getDatasetForFileInHistory("output2.txt", HISTORY_ID);
	}

	/**
	 * Tests failure to get analysis results from Galaxy due to failure to get a
	 * dataset
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test(expected = GalaxyDatasetException.class)
	public void testGetAnalysisResultsFail() throws IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException,
			ExecutionManagerException, IOException {
		submission = AnalysisSubmission.createSubmissionSingleReference("my analysis", Sets.newHashSet(),
				referenceFile, workflowId);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenThrow(
				new GalaxyDatasetException());

		workflowPreparation.getAnalysisResults(submission);
	}
}
