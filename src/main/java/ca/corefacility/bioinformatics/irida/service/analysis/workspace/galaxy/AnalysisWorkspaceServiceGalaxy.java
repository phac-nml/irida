package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerDownloadException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SampleAnalysisDuplicateException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowPreprationException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.Maps;

/**
 * A service for performing tasks for analysis in Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisWorkspaceServiceGalaxy implements AnalysisWorkspaceService {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisWorkspaceServiceGalaxy.class);

	private static final String COLLECTION_NAME = "irida_collection_list";

	private GalaxyWorkflowService galaxyWorkflowService;

	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;
	private SequenceFileRepository sequenceFileRepository;
	private GalaxyLibraryBuilder libraryBuilder;

	private GalaxyHistoriesService galaxyHistoriesService;

	private IridaWorkflowsService iridaWorkflowsService;

	/**
	 * Builds a new {@link AnalysisWorkspaceServiceGalaxy} with the
	 * given information.
	 * 
	 * @param galaxyHistoriesService
	 *            A GalaxyHistoriesService for interacting with Galaxy
	 *            Histories.
	 * @param galaxyWorkflowService
	 *            A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param sampleSequenceFileJoinRepository
	 *            A repository joining together sequence files and samples.
	 * @param libraryBuilder
	 *            An object for building libraries in Galaxy.
	 * @param iridaWorkflowsService
	 *            A service used for loading workflows from IRIDA.
	 */
	public AnalysisWorkspaceServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService,
			SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository,
			SequenceFileRepository sequenceFileRepository, GalaxyLibraryBuilder libraryBuilder,
			IridaWorkflowsService iridaWorkflowsService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.sampleSequenceFileJoinRepository = sampleSequenceFileJoinRepository;
		this.sequenceFileRepository = sequenceFileRepository;
		this.libraryBuilder = libraryBuilder;
		this.iridaWorkflowsService = iridaWorkflowsService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String prepareAnalysisWorkspace(AnalysisSubmission analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getSingleInputFiles(), "inputFiles are null");
		checkArgument(analysisSubmission.getRemoteAnalysisId() == null, "analysis id should be null");

		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();

		return workflowHistory.getId();
	}

	/**
	 * Builds a new AnalysisOutputFile from the given file in Galaxy.
	 * 
	 * @param analysisId
	 *            The id of the analysis performed in Galaxy.
	 * @param dataset
	 *            The dataset containing the data for the AnalysisOutputFile.
	 * @param outputDirectory
	 *            A directory to download the resulting output files.
	 * @return An AnalysisOutputFile storing a local copy of the Galaxy file.
	 * @throws IOException
	 *             If there was an issue creating a local file.
	 * @throws ExecutionManagerDownloadException
	 *             If there was an issue downloading the data from Galaxy.
	 */
	private AnalysisOutputFile buildOutputFile(String analysisId, Dataset dataset, Path outputDirectory)
			throws IOException, ExecutionManagerDownloadException {
		String datasetId = dataset.getId();
		String fileName = dataset.getName();

		Path outputFile = outputDirectory.resolve(fileName);
		galaxyHistoriesService.downloadDatasetTo(analysisId, datasetId, outputFile);

		AnalysisOutputFile analysisOutputFile = new AnalysisOutputFile(outputFile, datasetId);

		return analysisOutputFile;
	}

	/**
	 * Given a set of sequence files, gets a join between these sequence files
	 * and the corresponding samples.
	 * 
	 * @param sequenceFiles
	 *            The set of sequence files.
	 * @return A list of joins between sample and sequence files.
	 */
	private List<Join<Sample, SequenceFile>> getSequenceFileSingleSamples(Set<SequenceFile> sequenceFiles) {
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = new LinkedList<>();

		for (SequenceFile file : sequenceFiles) {
			Join<Sample, SequenceFile> sampleSequenceFile = sampleSequenceFileJoinRepository
					.getSampleForSequenceFile(file);

			sampleSequenceFiles.add(sampleSequenceFile);
		}

		return sampleSequenceFiles;
	}
	
	/**
	 * Gets a map of sequence file pairs and corresponding samples.
	 * 
	 * @param pairedInputFiles
	 *            A {@link Set} of {@link SequenceFilePair}s.
	 * @return A {@link Map} of between {@link SequenceFilePair} and
	 *         {@link Sample}.
	 * @throws SampleAnalysisDuplicateException
	 *             If there is a duplicate sample.
	 */
	private Map<SequenceFilePair, Sample> getSequenceFilePairedSamples(Set<SequenceFilePair> pairedInputFiles)
			throws SampleAnalysisDuplicateException {
		Map<SequenceFilePair, Sample> sequenceFilePairsSampleMap = new HashMap<>();

		for (SequenceFilePair filePair : pairedInputFiles) {
			SequenceFile pair1 = filePair.getFiles().iterator().next();
			Join<Sample, SequenceFile> pair1Join = sampleSequenceFileJoinRepository.getSampleForSequenceFile(pair1);
			Sample sample = pair1Join.getSubject();
			if (sequenceFilePairsSampleMap.containsKey(filePair)) {
				throw new SampleAnalysisDuplicateException("Sample " + sample
						+ " already exists in set for sequence file " + pairedInputFiles);
			} else {
				sequenceFilePairsSampleMap.put(filePair, pair1Join.getSubject());
			}
		}

		return sequenceFilePairsSampleMap;
	}

	/**
	 * Uploads a list of sequence files belonging to the given samples to
	 * Galaxy. TODO Only supports one sequence file per sample right now and
	 * throws an exception if more than one sequence file shares the same
	 * sample. This will be fixed in a future release.
	 * 
	 * @param sampleSequenceFiles
	 *            A join between sequence files and samples.
	 * @param workflowHistory
	 *            The history to upload the sequence files into.
	 * @param workflowLibrary
	 *            A temporary library to upload files into.
	 * @return A CollectionResponse for the dataset collection constructed from
	 *         the given files.
	 * @throws ExecutionManagerException
	 *             If there was an error uploading the files.
	 */
	private CollectionResponse uploadSequenceFiles(List<Join<Sample, SequenceFile>> sampleSequenceFiles,
			History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {

		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST.toString());
		description.setName(COLLECTION_NAME);

		// TODO Only supports one sequence file per sample right now and
		// throws an exception if more than one sequence file shares the same
		// sample. This will be fixed in a future release.
		Map<Path, Sample> samplesMap = new HashMap<>();
		for (Join<Sample, SequenceFile> sampleSequenceJoin : sampleSequenceFiles) {
			SequenceFile sequenceFile = sampleSequenceJoin.getObject();
			Sample sample = sampleSequenceJoin.getSubject();

			if (samplesMap.containsValue(sample)) {
				throw new WorkflowPreprationException("Sequence file: " + sequenceFile.getFile()
						+ " belongs to sample " + sample + " but there is another sequence file with this sample");
			} else {
				samplesMap.put(sequenceFile.getFile(), sample);
			}
		}

		// upload files to library and then to a history
		Set<Path> pathsToUpload = samplesMap.keySet();
		Map<Path, String> pathHistoryDatasetId = galaxyHistoriesService.filesToLibraryToHistory(pathsToUpload,
				InputFileType.FASTQ_SANGER, workflowHistory, workflowLibrary, DataStorage.LOCAL);

		for (Path sequenceFilePath : samplesMap.keySet()) {
			if (!pathHistoryDatasetId.containsKey(sequenceFilePath)) {
				throw new UploadException("Error, no corresponding history item found for " + sequenceFilePath);
			}

			Sample sample = samplesMap.get(sequenceFilePath);
			String datasetHistoryId = pathHistoryDatasetId.get(sequenceFilePath);

			HistoryDatasetElement datasetElement = new HistoryDatasetElement();
			datasetElement.setId(datasetHistoryId);
			datasetElement.setName(sample.getSampleName());

			description.addDatasetElement(datasetElement);
		}

		return galaxyHistoriesService.constructCollection(description, workflowHistory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedWorkflowGalaxy prepareAnalysisFiles(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteAnalysisId(), "analysisId is null");
		checkNotNull(analysisSubmission.getSingleInputFiles(), "inputFiles are null");
		checkNotNull(analysisSubmission.getWorkflowId(), "workflowId is null");
		checkNotNull(analysisSubmission.getRemoteWorkflowId(), "remoteWorkflowId is null");

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
		IridaWorkflowInput workflowInput = iridaWorkflow.getWorkflowDescription().getInputs();
		String sequenceFilesLabel = workflowInput.getSequenceReadsSingle();
		String referenceFileLabel = workflowInput.getReference();
		checkNotNull(sequenceFilesLabel, "sequenceReadsSingleLabel is null");
		checkNotNull(referenceFileLabel, "referenceFileLabel is null");

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			checkArgument(analysisSubmission.getReferenceFile().isPresent(),
					"workflow requires reference but none defined in submission");
		} else {
			checkArgument(!analysisSubmission.getReferenceFile().isPresent(),
					"workflow does not require a reference and a reference file is set in the submission");
		}

		String temporaryLibraryName = AnalysisSubmission.class.getSimpleName() + "-"
				+ UUID.randomUUID().toString();

		History workflowHistory = galaxyHistoriesService.findById(analysisSubmission.getRemoteAnalysisId());
		Library workflowLibrary = libraryBuilder.buildEmptyLibrary(new GalaxyProjectName(temporaryLibraryName));

		List<Join<Sample, SequenceFile>> sampleSequenceFilesSingle = getSequenceFileSingleSamples(analysisSubmission
				.getSingleInputFiles());
		Map<SequenceFilePair, Sample> sampleSequenceFilesPaired = getSequenceFilePairedSamples(analysisSubmission.getPairedInputFiles());

		CollectionResponse collectionResponse = uploadSequenceFiles(sampleSequenceFilesSingle, workflowHistory,
				workflowLibrary);

		String workflowId = analysisSubmission.getRemoteWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);

		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
				sequenceFilesLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId, new WorkflowInputs.WorkflowInput(collectionResponse.getId(),
				WorkflowInputs.InputSourceType.HDCA));

		String analysisId = workflowHistory.getId();

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			prepareReferenceFile(analysisSubmission.getReferenceFile().get(), workflowHistory, referenceFileLabel,
					workflowDetails, inputs);
		}

		return new PreparedWorkflowGalaxy(analysisId, new WorkflowInputsGalaxy(inputs));
	}

	/**
	 * Prepares a reference file for input to the workflow.
	 * 
	 * @param referenceFile
	 *            The {@link ReferenceFile} for the workflow.
	 * @param workflowHistory
	 *            The {@link History} for the workflow.
	 * @param referenceFileLabel
	 *            The label for the reference file in the workflow.
	 * @param workflowDetails
	 *            The {@link WorkflowDetails} for the workflow.
	 * @param inputs
	 *            The {@link WorkflowInputs} object used to setup inputs for the
	 *            workflow.
	 * @throws UploadException
	 *             If there's an exception when uploading files to the workflow
	 *             engine.
	 * @throws GalaxyDatasetException
	 *             If there's an exception with Galaxy datasets.
	 * @throws WorkflowException
	 *             If there's an exception with workflow methods.
	 */
	private void prepareReferenceFile(ReferenceFile referenceFile, History workflowHistory, String referenceFileLabel,
			WorkflowDetails workflowDetails, WorkflowInputs inputs) throws UploadException, GalaxyDatasetException,
			WorkflowException {

		Dataset referenceDataset = galaxyHistoriesService.fileToHistory(referenceFile.getFile(), InputFileType.FASTA,
				workflowHistory);

		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
				referenceFileLabel);

		inputs.setInput(workflowReferenceFileInputId, new WorkflowInputs.WorkflowInput(referenceDataset.getId(),
				WorkflowInputs.InputSourceType.HDA));
	}
	
	/**
	 * Creates a set of {@link SequenceFile} from the given input files in the
	 * submission.
	 * 
	 * @param analysisSubmission
	 *            The submission containing the input files.
	 * @return A {@link Set} of {@link SequenceFile} for any input files in the
	 *         submission.
	 */
	private Set<SequenceFile> createInputSequenceFilesSet(AnalysisSubmission analysisSubmission) {
		Set<SequenceFile> inputFiles = new HashSet<>();
		for (SequenceFile sf : analysisSubmission.getSingleInputFiles()) {
			inputFiles.add(sequenceFileRepository.findOne(sf.getId()));
		}

		for (SequenceFilePair sfp : analysisSubmission.getPairedInputFiles()) {
			Iterator<SequenceFile> sfpIter = sfp.getFiles().iterator();
			SequenceFile sf1 = sfpIter.next();
			SequenceFile sf2 = sfpIter.next();
			inputFiles.add(sequenceFileRepository.findOne(sf1.getId()));
			inputFiles.add(sequenceFileRepository.findOne(sf2.getId()));
		}

		return inputFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getAnalysisResults(AnalysisSubmission analysisSubmission) throws ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getSingleInputFiles(), "input sequence files is null");
		checkNotNull(analysisSubmission.getWorkflowId(), "workflowId is null");
		checkNotNull(analysisSubmission.getRemoteWorkflowId(), "remoteWorkflowId is null");

		Path outputDirectory = Files.createTempDirectory("analysis-output");
		logger.trace("Created temporary directory " + outputDirectory + " for analysis output files");

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
		String analysisId = analysisSubmission.getRemoteAnalysisId();

		Set<SequenceFile> inputFiles = createInputSequenceFilesSet(analysisSubmission);

		Map<String, IridaWorkflowOutput> outputsMap = iridaWorkflow.getWorkflowDescription().getOutputsMap();

		Map<String, AnalysisOutputFile> analysisOutputFiles = Maps.newHashMap();
		for (String analysisOutputName : outputsMap.keySet()) {
			String outputFileName = outputsMap.get(analysisOutputName).getFileName();
			Dataset outputDataset = galaxyHistoriesService.getDatasetForFileInHistory(outputFileName, analysisId);
			AnalysisOutputFile analysisOutput = buildOutputFile(analysisId, outputDataset, outputDirectory);

			analysisOutputFiles.put(analysisOutputName, analysisOutput);
		}

		Class<? extends Analysis> analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType()
				.getAnalysisClass();
		try {
			Constructor<? extends Analysis> analysisConstructor = analysisType.getConstructor(Set.class, String.class,
					Map.class);
			return analysisConstructor.newInstance(inputFiles, analysisId, analysisOutputFiles);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new IridaWorkflowAnalysisTypeException("Error building Analysis object of type " + analysisType, e);
		}
	}
}