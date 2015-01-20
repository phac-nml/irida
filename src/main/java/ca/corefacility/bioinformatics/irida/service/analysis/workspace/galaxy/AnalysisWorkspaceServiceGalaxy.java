package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
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

	private static final String COLLECTION_NAME_SINGLE = "irida_sequence_files_single";
	private static final String COLLECTION_NAME_PAIRED = "irida_sequence_files_paired";
	
	private static final String FORWARD_NAME = "forward";
	private static final String REVERSE_NAME = "reverse";

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
	 * Given a set of sequence files, constructs a map between the sequence
	 * files and the corresponding samples.
	 * 
	 * @param sequenceFiles
	 *            The set of sequence files.
	 * @return A map linking a sample and the sequence files to run.
	 * @throws SampleAnalysisDuplicateException
	 *             If there was more than one sequence file with the same
	 *             sample.
	 */
	private Map<Sample, SequenceFile> getSequenceFileSingleSamples(Set<SequenceFile> sequenceFiles)
			throws SampleAnalysisDuplicateException {
		Map<Sample, SequenceFile> sampleSequenceFiles = new HashMap<>();

		for (SequenceFile file : sequenceFiles) {
			Join<Sample, SequenceFile> sampleSequenceFile = sampleSequenceFileJoinRepository
					.getSampleForSequenceFile(file);
			Sample sample = sampleSequenceFile.getSubject();
			SequenceFile sequenceFile = sampleSequenceFile.getObject();

			if (sampleSequenceFiles.containsKey(sample)) {
				SequenceFile previousFile = sampleSequenceFiles.get(sample);
				throw new SampleAnalysisDuplicateException("Sequence files " + sequenceFile + ", " + previousFile
						+ " both have the same sample " + sample);
			} else {
				sampleSequenceFiles.put(sample, sequenceFile);
			}
		}

		return sampleSequenceFiles;
	}
	
	/**
	 * Gets a map of sequence file pairs and corresponding samples.
	 * 
	 * @param pairedInputFiles
	 *            A {@link Set} of {@link SequenceFilePair}s.
	 * @return A {@link Map} of between {@link Sample} and
	 *         {@link SequenceFilePair}.
	 * @throws SampleAnalysisDuplicateException
	 *             If there is a duplicate sample.
	 */
	private Map<Sample, SequenceFilePair> getSequenceFilePairedSamples(Set<SequenceFilePair> pairedInputFiles)
			throws SampleAnalysisDuplicateException {
		Map<Sample, SequenceFilePair> sequenceFilePairsSampleMap = new HashMap<>();

		for (SequenceFilePair filePair : pairedInputFiles) {
			SequenceFile pair1 = filePair.getFiles().iterator().next();
			Join<Sample, SequenceFile> pair1Join = sampleSequenceFileJoinRepository.getSampleForSequenceFile(pair1);
			Sample sample = pair1Join.getSubject();
			if (sequenceFilePairsSampleMap.containsKey(sample)) {
				SequenceFilePair previousPair = sequenceFilePairsSampleMap.get(sample);
				throw new SampleAnalysisDuplicateException("Sequence file pairs " + pair1 + ", " + previousPair + " have the same sample " + sample);
			} else {
				sequenceFilePairsSampleMap.put(sample, filePair);
			}
		}

		return sequenceFilePairsSampleMap;
	}

	/**
	 * Uploads a list of single sequence files belonging to the given samples to
	 * Galaxy.
	 * 
	 * @param sampleSequenceFiles
	 *            A map between {@link Sample} and {@link SequenceFile}.
	 * @param workflowHistory
	 *            The history to upload the sequence files into.
	 * @param workflowLibrary
	 *            A temporary library to upload files into.
	 * @return A CollectionResponse for the dataset collection constructed from
	 *         the given files.
	 * @throws ExecutionManagerException
	 *             If there was an error uploading the files.
	 */
	private CollectionResponse uploadSequenceFilesSingle(Map<Sample, SequenceFile> sampleSequenceFiles, History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {

		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST.toString());
		description.setName(COLLECTION_NAME_SINGLE);

		Map<Path, Sample> samplesMap = new HashMap<>();
		for (Sample sample : sampleSequenceFiles.keySet()) {
			SequenceFile sequenceFile = sampleSequenceFiles.get(sample);
			samplesMap.put(sequenceFile.getFile(), sample);
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
	 * Uploads a list of paired sequence files belonging to the given samples to
	 * Galaxy.
	 * 
	 * @param sampleSequenceFilesPaired
	 *            A map between {@link Sample} and {@link SequenceFilePair}.
	 * @param workflowHistory
	 *            The history to upload the sequence files into.
	 * @param workflowLibrary
	 *            A temporary library to upload files into.
	 * @return A CollectionResponse for the dataset collection constructed from
	 *         the given files.
	 * @throws ExecutionManagerException
	 *             If there was an error uploading the files.
	 */
	private CollectionResponse uploadSequenceFilesPaired(Map<Sample, SequenceFilePair> sampleSequenceFilesPaired, History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {

		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST_PAIRED.toString());
		description.setName(COLLECTION_NAME_PAIRED);

		Map<Sample, Path> samplesMapPair1 = new HashMap<>();
		Map<Sample, Path> samplesMapPair2 = new HashMap<>();
		Set<Path> pathsToUpload = new HashSet<>();
		for (Sample sample : sampleSequenceFilesPaired.keySet()) {
			SequenceFilePair sequenceFilePair = sampleSequenceFilesPaired.get(sample);
			Iterator<SequenceFile> fileIter = sequenceFilePair.getFiles().iterator();
			SequenceFile file1 = fileIter.next();
			SequenceFile file2 = fileIter.next();
			
			samplesMapPair1.put(sample, file1.getFile());
			samplesMapPair2.put(sample, file2.getFile());
			pathsToUpload.add(file1.getFile());
			pathsToUpload.add(file2.getFile());
		}

		// upload files to library and then to a history
		Map<Path, String> pathHistoryDatasetId = galaxyHistoriesService.filesToLibraryToHistory(pathsToUpload,
				InputFileType.FASTQ_SANGER, workflowHistory, workflowLibrary, DataStorage.LOCAL);

		for (Sample sample : sampleSequenceFilesPaired.keySet()) {
			Path file1 = samplesMapPair1.get(sample);
			Path file2 = samplesMapPair2.get(sample);
			
			if (!pathHistoryDatasetId.containsKey(file1)) {
				throw new UploadException("Error, no corresponding history item found for " + file1);
			} else if (!pathHistoryDatasetId.containsKey(file2)) {
				throw new UploadException("Error, no corresponding history item found for " + file2);
			} else {
				String datasetHistoryId1 = pathHistoryDatasetId.get(file1);
				String datasetHistoryId2 = pathHistoryDatasetId.get(file2);
				
				CollectionElement pairedElement = new CollectionElement();
				pairedElement.setName(sample.getSampleName());
				pairedElement.setCollectionType(DatasetCollectionType.PAIRED.toString());
	
				HistoryDatasetElement datasetElement1 = new HistoryDatasetElement();
				datasetElement1.setId(datasetHistoryId1);
				datasetElement1.setName(FORWARD_NAME);
				pairedElement.addCollectionElement(datasetElement1);
				
				HistoryDatasetElement datasetElement2 = new HistoryDatasetElement();
				datasetElement2.setId(datasetHistoryId2);
				datasetElement2.setName(REVERSE_NAME);
				pairedElement.addCollectionElement(datasetElement2);
	
				description.addDatasetElement(pairedElement);
			}
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
		String sequenceFilesLabelSingle = workflowInput.getSequenceReadsSingle();
		String referenceFileLabel = workflowInput.getReference();
		checkNotNull(sequenceFilesLabelSingle, "sequenceReadsSingleLabel is null");
		checkNotNull(referenceFileLabel, "referenceFileLabel is null");

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			checkArgument(analysisSubmission.getReferenceFile().isPresent(),
					"workflow requires reference but none defined in submission");
		} else {
			checkArgument(!analysisSubmission.getReferenceFile().isPresent(),
					"workflow does not require a reference and a reference file is set in the submission");
		}

		String temporaryLibraryName = AnalysisSubmission.class.getSimpleName() + "-" + UUID.randomUUID().toString();

		History workflowHistory = galaxyHistoriesService.findById(analysisSubmission.getRemoteAnalysisId());
		Library workflowLibrary = libraryBuilder.buildEmptyLibrary(new GalaxyProjectName(temporaryLibraryName));

		Map<Sample, SequenceFile> sampleSequenceFilesSingle = getSequenceFileSingleSamples(analysisSubmission
				.getSingleInputFiles());
		Map<Sample, SequenceFilePair> sampleSequenceFilesPaired = getSequenceFilePairedSamples(analysisSubmission
				.getPairedInputFiles());
		if (samplesInCommon(sampleSequenceFilesSingle, sampleSequenceFilesPaired)) {
			throw new SampleAnalysisDuplicateException("Single and paired input files share a common sample for submission "
					+ analysisSubmission);
		}

		String workflowId = analysisSubmission.getRemoteWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);

		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
				sequenceFilesLabelSingle);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());

		if (!sampleSequenceFilesSingle.isEmpty()) {
			CollectionResponse collectionResponseSingle = uploadSequenceFilesSingle(sampleSequenceFilesSingle,
					workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFileInputId,
					new WorkflowInputs.WorkflowInput(collectionResponseSingle.getId(),
							WorkflowInputs.InputSourceType.HDCA));
		}

		if (!sampleSequenceFilesPaired.isEmpty()) {
			CollectionResponse collectionResponsePaired = uploadSequenceFilesPaired(sampleSequenceFilesPaired,
					workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFileInputId,
					new WorkflowInputs.WorkflowInput(collectionResponsePaired.getId(),
							WorkflowInputs.InputSourceType.HDCA));
		}

		String analysisId = workflowHistory.getId();

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			prepareReferenceFile(analysisSubmission.getReferenceFile().get(), workflowHistory, referenceFileLabel,
					workflowDetails, inputs);
		}

		return new PreparedWorkflowGalaxy(analysisId, new WorkflowInputsGalaxy(inputs));
	}

	/**
	 * Determines if the two data structures of samples/sequence files share a
	 * common sample.
	 * 
	 * @param sampleSequenceFilesSingle
	 *            A map of single sequence files and samples.
	 * @param sampleSequenceFilesPaired
	 *            A map of sequence file pairs and samples.
	 * @return True if the two data structures share a common sample, false
	 *         otherwise.
	 */
	private boolean samplesInCommon(Map<Sample, SequenceFile> sampleSequenceFilesSingle,
			Map<Sample, SequenceFilePair> sampleSequenceFilesPaired) {
		for (Sample sampleSingle : sampleSequenceFilesSingle.keySet()) {
			
			if (sampleSequenceFilesPaired.containsKey(sampleSingle)) {
				return true;
			}
		}
		
		return false;
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