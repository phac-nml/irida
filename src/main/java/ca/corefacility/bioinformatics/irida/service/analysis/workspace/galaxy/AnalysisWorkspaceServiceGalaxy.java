package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
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
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.Maps;

/**
 * A service for performing tasks for analysis in Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisWorkspaceServiceGalaxy implements AnalysisWorkspaceService {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisWorkspaceServiceGalaxy.class);
	
	private GalaxyWorkflowService galaxyWorkflowService;

	private SequenceFileRepository sequenceFileRepository;
	private GalaxyLibraryBuilder libraryBuilder;

	private GalaxyHistoriesService galaxyHistoriesService;

	private IridaWorkflowsService iridaWorkflowsService;
	
	private AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy;

	/**
	 * Builds a new {@link AnalysisWorkspaceServiceGalaxy} with the given
	 * information.
	 * 
	 * @param galaxyHistoriesService
	 *            A GalaxyHistoriesService for interacting with Galaxy
	 *            Histories.
	 * @param galaxyWorkflowService
	 *            A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param libraryBuilder
	 *            An object for building libraries in Galaxy.
	 * @param iridaWorkflowsService
	 *            A service used for loading workflows from IRIDA.
	 * @param analysisCollectionServiceGalaxy
	 *            A service for constructing dataset collections of input files.
	 */
	public AnalysisWorkspaceServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService,
			SequenceFileRepository sequenceFileRepository, GalaxyLibraryBuilder libraryBuilder,
			IridaWorkflowsService iridaWorkflowsService, AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.sequenceFileRepository = sequenceFileRepository;
		this.libraryBuilder = libraryBuilder;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.analysisCollectionServiceGalaxy = analysisCollectionServiceGalaxy;
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
		String sequenceFilesLabelPaired = workflowInput.getSequenceReadsPaired();
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

		Map<Sample, SequenceFile> sampleSequenceFilesSingle = analysisCollectionServiceGalaxy.getSequenceFileSingleSamples(analysisSubmission
				.getSingleInputFiles());
		Map<Sample, SequenceFilePair> sampleSequenceFilesPaired = analysisCollectionServiceGalaxy.getSequenceFilePairedSamples(analysisSubmission
				.getPairedInputFiles());
		if (samplesInCommon(sampleSequenceFilesSingle, sampleSequenceFilesPaired)) {
			throw new SampleAnalysisDuplicateException("Single and paired input files share a common sample for submission "
					+ analysisSubmission);
		}

		String workflowId = analysisSubmission.getRemoteWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());

		if (!sampleSequenceFilesSingle.isEmpty()) {
			String workflowSequenceFileSingleInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
					sequenceFilesLabelSingle);
			CollectionResponse collectionResponseSingle = analysisCollectionServiceGalaxy.uploadSequenceFilesSingle(sampleSequenceFilesSingle,
					workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFileSingleInputId,
					new WorkflowInputs.WorkflowInput(collectionResponseSingle.getId(),
							WorkflowInputs.InputSourceType.HDCA));
		}

		if (!sampleSequenceFilesPaired.isEmpty()) {
			String workflowSequenceFilePairedInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
					sequenceFilesLabelPaired);
			CollectionResponse collectionResponsePaired = analysisCollectionServiceGalaxy.uploadSequenceFilesPaired(sampleSequenceFilesPaired,
					workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFilePairedInputId,
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