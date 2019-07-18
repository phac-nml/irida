package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowTestBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.github.jmchilton.blend4j.galaxy.beans.*;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests out preparing a Galaxy Phylogenomics Pipeline workflow for execution.
 * 
 *
 */
public class AnalysisWorkspaceServiceGalaxyTest {

	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowService galaxyWorkflowService;
	@Mock
	private GalaxyLibrariesService galaxyLibrariesService;
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

	@Mock
	private AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy;

	@Mock
	private AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy;

	@Mock
	private SequencingObjectService sequencingObjectService;

	private AnalysisWorkspaceServiceGalaxy workflowPreparation;

	private Set<SequencingObject> inputFiles;
	private ReferenceFile referenceFile;
	private Path refFile;
	private AnalysisSubmission submission;
	private WorkflowDetails workflowDetails;

	private History workflowHistory;
	private Library workflowLibrary;

	private static final String HISTORY_ID = "10";
	private static final String LIBRARY_ID = "9";
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

	private SingleEndSequenceFile sObjA;
	private SingleEndSequenceFile sObjB;
	private SingleEndSequenceFile sObjC;

	private Dataset output1Dataset;
	private Dataset output2Dataset;
	private String output1Filename = "output1.txt";
	private String output2Filename = "output2.txt";

	private UUID workflowId = IridaWorkflowTestBuilder.DEFAULT_ID;
	private UUID workflowIdMultiSamples = IridaWorkflowTestBuilder.MULTI_SAMPLES_ID;
	private IridaWorkflow iridaWorkflowSingle = IridaWorkflowTestBuilder.buildTestWorkflowSingle();
	private IridaWorkflow iridaWorkflowSingleNoReference = IridaWorkflowTestBuilder
			.buildTestWorkflowSingleNoReference();
	private IridaWorkflow iridaWorkflowPaired = IridaWorkflowTestBuilder.buildTestWorkflowPaired();
	private IridaWorkflow iridaWorkflowSinglePaired = IridaWorkflowTestBuilder.buildTestWorkflowSinglePaired();
	private IridaWorkflow iridaWorkflowSinglePairedMultipleSamples = IridaWorkflowTestBuilder
			.buildTestWorkflowSinglePairedMultipleSamples();

	private Map<Sample, SingleEndSequenceFile> sampleSingleSequenceFileMap;
	private Map<Sample, SequenceFilePair> sampleSequenceFilePairMap;
	private Map<Sample, SequenceFilePair> sampleSequenceFilePairMapSampleA;
	private SequenceFilePair sequenceFilePair;
	private SingleEndSequenceFile singleEndSequenceFile;

	private CollectionResponse collectionResponseSingle;
	private CollectionResponse collectionResponsePaired;

	private Set<SequencingObject> singleInputFiles;
	private Set<SequencingObject> pairedInputFiles;

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

		sObjA = new SingleEndSequenceFile(sFileA);
		sObjB = new SingleEndSequenceFile(sFileB);
		sObjC = new SingleEndSequenceFile(sFileC);

		sequenceFilePair = new SequenceFilePair(sFileB, sFileC);
		singleEndSequenceFile = sObjA;

		Sample sampleA = new Sample();
		sampleA.setSampleName("SampleA");

		Sample sampleB = new Sample();
		sampleB.setSampleName("SampleB");

		Sample sampleC = new Sample();
		sampleC.setSampleName("SampleC");

		sampleSingleSequenceFileMap = ImmutableMap.of(sampleA, singleEndSequenceFile);
		sampleSequenceFilePairMap = ImmutableMap.of(sampleB, sequenceFilePair);
		sampleSequenceFilePairMapSampleA = ImmutableMap.of(sampleA, sequenceFilePair);

		refFile = createTempFile("reference", "fasta");
		referenceFile = new ReferenceFile(refFile);

		inputFiles = new HashSet<>();
		inputFiles.addAll(Arrays.asList(sObjA, sObjB, sObjC));

		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(inputFiles)
				.referenceFile(referenceFile).build();

		workflowHistory = new History();
		workflowHistory.setId(HISTORY_ID);

		workflowLibrary = new Library();
		workflowLibrary.setId(LIBRARY_ID);

		workflowDetails = new WorkflowDetails();
		workflowDetails.setId(WORKFLOW_ID);

		workflowPreparation = new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService, galaxyWorkflowService,
				galaxyLibrariesService, iridaWorkflowsService, analysisCollectionServiceGalaxy,
				analysisProvenanceServiceGalaxy, analysisParameterServiceGalaxy,
				sequencingObjectService);

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

		singleInputFiles = Sets.newHashSet(singleEndSequenceFile);
		pairedInputFiles = Sets.newHashSet(sequenceFilePair);
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
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesSinglePairedSuccess() throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());

		Set<SequencingObject> joinedInput = Sets.newHashSet(singleFiles);
		joinedInput.addAll(pairedFiles);

		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(joinedInput)
				.referenceFile(referenceFile).build();

		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSinglePaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(singleFiles))
				.thenReturn(sampleSingleSequenceFileMap);
		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMap);

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(analysisParameterServiceGalaxy.prepareAnalysisParameters(any(Map.class), any(IridaWorkflow.class)))
				.thenReturn(new WorkflowInputsGalaxy(new WorkflowInputs()));
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_SINGLE_LABEL))
				.thenReturn(SEQUENCE_FILE_SINGLE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_PAIRED_LABEL))
				.thenReturn(SEQUENCE_FILE_PAIRED_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL))
				.thenReturn(REFERENCE_FILE_ID);

		when(analysisCollectionServiceGalaxy.uploadSequenceFilesSingleEnd(any(Map.class), eq(workflowHistory),
				eq(workflowLibrary))).thenReturn(collectionResponseSingle);
		when(analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(any(Map.class), eq(workflowHistory),
				eq(workflowLibrary))).thenReturn(collectionResponsePaired);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());
		assertEquals("preparedWorkflow library is invalid", LIBRARY_ID, preparedWorkflow.getRemoteDataId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("invalid number of workflow inputs", 3, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference entry", workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file single entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_SINGLE_ID));
		assertTrue("workflow inputs should contain sequence file paired entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_PAIRED_ID));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesSingleEnd(any(Map.class), any(History.class),
				any(Library.class));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out successfully to preparing an analysis with single files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesSingleSuccess() throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(singleFiles)).referenceFile(referenceFile)
				.build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(singleFiles))
				.thenReturn(sampleSingleSequenceFileMap);
		
		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(analysisParameterServiceGalaxy.prepareAnalysisParameters(any(Map.class), any(IridaWorkflow.class)))
				.thenReturn(new WorkflowInputsGalaxy(new WorkflowInputs()));
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_SINGLE_LABEL))
				.thenReturn(SEQUENCE_FILE_SINGLE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL))
				.thenReturn(REFERENCE_FILE_ID);

		when(analysisCollectionServiceGalaxy.uploadSequenceFilesSingleEnd(any(Map.class), eq(workflowHistory),
				eq(workflowLibrary))).thenReturn(collectionResponseSingle);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());
		assertEquals("preparedWorkflow library is invalid", LIBRARY_ID, preparedWorkflow.getRemoteDataId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("workflow inputs has invalid size", 2, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference file entry",
				workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file single entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_SINGLE_ID));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesSingleEnd(any(Map.class), any(History.class),
				any(Library.class));
		verify(analysisCollectionServiceGalaxy, never()).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out successfully to preparing an analysis with paired files
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisFilesPairedSuccess() throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());
		
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(pairedFiles)).referenceFile(referenceFile)
				.build();

		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);
		
		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMap);

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(analysisParameterServiceGalaxy.prepareAnalysisParameters(any(Map.class), any(IridaWorkflow.class)))
				.thenReturn(new WorkflowInputsGalaxy(new WorkflowInputs()));
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, SEQUENCE_FILE_PAIRED_LABEL))
				.thenReturn(SEQUENCE_FILE_PAIRED_ID);
		when(galaxyWorkflowService.getWorkflowInputId(workflowDetails, REFERENCE_FILE_LABEL))
				.thenReturn(REFERENCE_FILE_ID);

		when(analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(any(Map.class), eq(workflowHistory),
				eq(workflowLibrary))).thenReturn(collectionResponsePaired);

		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisFiles(submission);

		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID, HISTORY_ID,
				preparedWorkflow.getRemoteAnalysisId());
		assertEquals("preparedWorkflow library is invalid", LIBRARY_ID, preparedWorkflow.getRemoteDataId());

		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
		Map<String, WorkflowInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals("workflow inputs has invalid size", 2, workflowInputsMap.size());
		assertTrue("workflow inputs should contain reference file entry",
				workflowInputsMap.containsKey(REFERENCE_FILE_ID));
		assertTrue("workflow inputs should contain sequence file paired entry",
				workflowInputsMap.containsKey(SEQUENCE_FILE_PAIRED_ID));
		verify(analysisCollectionServiceGalaxy, never()).uploadSequenceFilesSingleEnd(any(Map.class),
				any(History.class), any(Library.class));
		verify(analysisCollectionServiceGalaxy).uploadSequenceFilesPaired(any(Map.class), any(History.class),
				any(Library.class));
	}

	/**
	 * Tests out successfully to preparing an analysis with single files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = CreateLibraryException.class)
	public void testPrepareAnalysisFilesNoCreateLibraryFail() throws ExecutionManagerException, IridaWorkflowException, IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(sampleSingleSequenceFileMap.values())).referenceFile(referenceFile)
				.build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class)))
				.thenThrow(new CreateLibraryException(""));

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis due to duplicate samples
	 * between single and paired input files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = SampleAnalysisDuplicateException.class)
	public void testPrepareAnalysisFilesSinglePairedDuplicateFail()
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMapSampleA.values());
		
		Set<SequencingObject> joinedInputs = Sets.newHashSet(singleFiles);
		joinedInputs.addAll(pairedFiles);
		
		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(joinedInputs)
				.referenceFile(referenceFile).build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSinglePaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(singleFiles))
				.thenReturn(sampleSingleSequenceFileMap);
		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMapSampleA);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis with paired files when it
	 * cannot accept paired files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesPairedNoAcceptFail() throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMapSampleA.values());

		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(pairedFiles))
				.referenceFile(referenceFile).build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMapSampleA);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis with single files when it
	 * cannot accept single files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesSingleNoAcceptFail() throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());

		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(sampleSingleSequenceFileMap.values())).referenceFile(referenceFile)
				.build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);
		
		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis with no files in the
	 * submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesNoSubmittedFilesFail()
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(Sets.newHashSet())
				.referenceFile(referenceFile).build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis which requires a reference but
	 * no reference found in submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesRequiresReferenceFail()
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(sampleSingleSequenceFileMap.values())).build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis which does not require a
	 * reference but a reference is found in submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesNoRequiresReferenceFail()
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(sampleSingleSequenceFileMap.values())).referenceFile(referenceFile)
				.build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingleNoReference);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to preparing an analysis which is passed both single
	 * and paired files but only accepts paired files.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisFilesSinglePairedNoAcceptFail()
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		Set<SequencingObject> joindInputs = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		joindInputs.addAll(sampleSequenceFilePairMap.values());

		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(joindInputs)
				.referenceFile(referenceFile).build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);
		
		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowPaired);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests out failing to prepare workflow files due to a failure to prepare
	 * parameters.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IridaWorkflowParameterException.class)
	public void testPrepareAnalysisFilesFailParameters() throws ExecutionManagerException, IridaWorkflowException, IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis")
				.inputFiles(Sets.newHashSet(sampleSingleSequenceFileMap.values())).referenceFile(referenceFile)
				.build();
		submission.setRemoteAnalysisId(HISTORY_ID);
		submission.setRemoteWorkflowId(WORKFLOW_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);

		when(galaxyHistoriesService.findById(HISTORY_ID)).thenReturn(workflowHistory);
		when(galaxyLibrariesService.buildEmptyLibrary(any(GalaxyProjectName.class))).thenReturn(workflowLibrary);

		when(galaxyHistoriesService.fileToHistory(refFile, InputFileType.FASTA, workflowHistory))
				.thenReturn(refDataset);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(analysisParameterServiceGalaxy.prepareAnalysisParameters(any(Map.class), any(IridaWorkflow.class)))
				.thenThrow(new IridaWorkflowParameterException(""));

		workflowPreparation.prepareAnalysisFiles(submission);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy with single end
	 * input files.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccessSingleEnd() throws IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException, ExecutionManagerException, IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());

		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(singleInputFiles)
				.referenceFile(referenceFile).build();
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);

		when(sequencingObjectService.getSequencingObjectsForAnalysisSubmission(submission))
				.thenReturn(Sets.newHashSet(singleFiles));

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenReturn(output1Dataset);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output2Filename, HISTORY_ID)).thenReturn(output2Dataset);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(singleFiles))
				.thenReturn(sampleSingleSequenceFileMap);

		Analysis analysis = workflowPreparation.getAnalysisResults(submission);

		assertNotNull("analysis is not valid", analysis);
		assertEquals("invalid number of output files", 2, analysis.getAnalysisOutputFiles().size());
		assertEquals("missing output file for analysis", Paths.get("output1.txt"),
				analysis.getAnalysisOutputFile("output1").getFile().getFileName());
		assertEquals("missing label for analysis output file", "SampleA-output1.txt",
				analysis.getAnalysisOutputFile("output1").getLabel());
		assertEquals("missing output file for analysis", "SampleA-output2.txt",
				analysis.getAnalysisOutputFile("output2").getLabel());

		verify(galaxyHistoriesService).getDatasetForFileInHistory("output1.txt", HISTORY_ID);
		verify(galaxyHistoriesService).getDatasetForFileInHistory("output2.txt", HISTORY_ID);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy with paired end
	 * input files.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccessPairedEnd() throws IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException, ExecutionManagerException, IOException {
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());

		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(pairedInputFiles)
				.referenceFile(referenceFile).build();
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenReturn(output1Dataset);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output2Filename, HISTORY_ID)).thenReturn(output2Dataset);

		when(sequencingObjectService.getSequencingObjectsForAnalysisSubmission(submission))
				.thenReturn(Sets.newHashSet(pairedFiles));

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMap);

		Analysis analysis = workflowPreparation.getAnalysisResults(submission);

		assertNotNull("analysis is not valid", analysis);
		assertEquals("invalid number of output files", 2, analysis.getAnalysisOutputFiles().size());
		assertEquals("missing output file for analysis", Paths.get("output1.txt"),
				analysis.getAnalysisOutputFile("output1").getFile().getFileName());
		assertEquals("missing label for analysis output file", "SampleB-output1.txt",
				analysis.getAnalysisOutputFile("output1").getLabel());
		assertEquals("missing output file for analysis", "SampleB-output2.txt",
				analysis.getAnalysisOutputFile("output2").getLabel());

		verify(galaxyHistoriesService).getDatasetForFileInHistory("output1.txt", HISTORY_ID);
		verify(galaxyHistoriesService).getDatasetForFileInHistory("output2.txt", HISTORY_ID);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy with
	 * single/paired end input files.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccessSinglePairedEnd()
			throws IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException, ExecutionManagerException,
			IOException {
		
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());

		Set<SequencingObject> joinedFiles = Sets.newHashSet(singleFiles);
		joinedFiles.addAll(pairedFiles);
		
		Map<Sample, SequencingObject> joinedMap = Maps.newHashMap(sampleSingleSequenceFileMap);
		joinedMap.putAll(sampleSequenceFilePairMap);
		
		submission = AnalysisSubmission.builder(workflowIdMultiSamples).name("my analysis")
				.inputFiles(singleInputFiles).inputFiles(pairedInputFiles).referenceFile(referenceFile)
				.build();
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);
		
		when(sequencingObjectService.getSequencingObjectsForAnalysisSubmission(submission)).thenReturn(joinedFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowIdMultiSamples))
				.thenReturn(iridaWorkflowSinglePairedMultipleSamples);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenReturn(output1Dataset);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output2Filename, HISTORY_ID)).thenReturn(output2Dataset);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(joinedFiles))
				.thenReturn(joinedMap);

		Analysis analysis = workflowPreparation.getAnalysisResults(submission);

		assertNotNull("analysis is not valid", analysis);
		assertEquals("invalid number of output files", 2, analysis.getAnalysisOutputFiles().size());
		assertEquals("missing output file for analysis", Paths.get("output1.txt"),
				analysis.getAnalysisOutputFile("output1").getFile().getFileName());

		// labels should now not have sample associated with them.
		assertEquals("missing label for analysis output file", "output1.txt",
				analysis.getAnalysisOutputFile("output1").getLabel());
		assertEquals("missing output file for analysis", "output2.txt",
				analysis.getAnalysisOutputFile("output2").getLabel());

		verify(galaxyHistoriesService).getDatasetForFileInHistory("output1.txt", HISTORY_ID);
		verify(galaxyHistoriesService).getDatasetForFileInHistory("output2.txt", HISTORY_ID);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy where there's
	 * multiple samples but workflow should have only accepted single sample (no
	 * label on name).
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccessMultiSample()
			throws IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException, ExecutionManagerException,
			IOException {
		Set<SingleEndSequenceFile> singleFiles = Sets.newHashSet(sampleSingleSequenceFileMap.values());
		Set<SequenceFilePair> pairedFiles = Sets.newHashSet(sampleSequenceFilePairMap.values());
		
		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(pairedInputFiles)
				.referenceFile(referenceFile).build();
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);
		
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SingleEndSequenceFile.class)).thenReturn(singleFiles);
		when(sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class)).thenReturn(pairedFiles);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID)).thenReturn(output1Dataset);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output2Filename, HISTORY_ID)).thenReturn(output2Dataset);

		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(singleFiles))
				.thenReturn(sampleSingleSequenceFileMap);
		when(sequencingObjectService.getUniqueSamplesForSequencingObjects(pairedFiles))
				.thenReturn(sampleSequenceFilePairMap);

		Analysis analysis = workflowPreparation.getAnalysisResults(submission);

		assertNotNull("analysis is not valid", analysis);
		assertEquals("invalid number of output files", 2, analysis.getAnalysisOutputFiles().size());
		assertEquals("missing output file for analysis", Paths.get("output1.txt"),
				analysis.getAnalysisOutputFile("output1").getFile().getFileName());

		// labels should now not have sample associated with them.
		assertEquals("missing label for analysis output file", "output1.txt",
				analysis.getAnalysisOutputFile("output1").getLabel());
		assertEquals("missing output file for analysis", "output2.txt",
				analysis.getAnalysisOutputFile("output2").getLabel());

		verify(galaxyHistoriesService).getDatasetForFileInHistory("output1.txt", HISTORY_ID);
		verify(galaxyHistoriesService).getDatasetForFileInHistory("output2.txt", HISTORY_ID);
	}

	/**
	 * Tests successfully getting analysis results from Galaxy where there's no
	 * sample associated with the sequence files (no label is prefixed to output
	 * file name).
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testGetAnalysisResultsSuccessNoSample()
			throws IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException, ExecutionManagerException,
			IOException {
		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(pairedInputFiles)
				.referenceFile(referenceFile).build();
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

		// labels should now not have sample associated with them.
		assertEquals("missing label for analysis output file", "output1.txt",
				analysis.getAnalysisOutputFile("output1").getLabel());
		assertEquals("missing output file for analysis", "output2.txt",
				analysis.getAnalysisOutputFile("output2").getLabel());

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
		submission = AnalysisSubmission.builder(workflowId).name("my analysis").inputFiles(singleInputFiles)
				.referenceFile(referenceFile).build();
		submission.setRemoteWorkflowId(WORKFLOW_ID);
		submission.setRemoteAnalysisId(HISTORY_ID);

		when(iridaWorkflowsService.getIridaWorkflow(workflowId)).thenReturn(iridaWorkflowSingle);
		when(galaxyHistoriesService.getDatasetForFileInHistory(output1Filename, HISTORY_ID))
				.thenThrow(new GalaxyDatasetException());

		workflowPreparation.getAnalysisResults(submission);
	}
}
