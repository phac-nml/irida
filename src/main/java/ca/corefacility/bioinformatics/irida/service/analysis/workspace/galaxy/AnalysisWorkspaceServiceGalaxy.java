package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.github.jmchilton.blend4j.galaxy.beans.*;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A service for performing tasks for analysis in Galaxy.
 * 
 */
public class AnalysisWorkspaceServiceGalaxy implements AnalysisWorkspaceService {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisWorkspaceServiceGalaxy.class);

	private GalaxyWorkflowService galaxyWorkflowService;

	private GalaxyHistoriesService galaxyHistoriesService;

	private IridaWorkflowsService iridaWorkflowsService;

	private AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy;

	private AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy;

	private AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy;

	private GalaxyLibrariesService galaxyLibrariesService;

	private SequencingObjectService sequencingObjectService;

	/**
	 * Builds a new {@link AnalysisWorkspaceServiceGalaxy} with the given
	 * information.
	 *
	 * @param galaxyHistoriesService          A GalaxyHistoriesService for interacting with Galaxy
	 *                                        Histories.
	 * @param galaxyWorkflowService           A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param galaxyLibrariesService          An object for building libraries in Galaxy.
	 * @param iridaWorkflowsService           A service used for loading workflows from IRIDA.
	 * @param analysisCollectionServiceGalaxy A service for constructing dataset collections of input files.
	 * @param analysisProvenanceServiceGalaxy The service for provenance information.
	 * @param analysisParameterServiceGalaxy  A service for setting up parameters in Galaxy.
	 * @param sequencingObjectService         A service for reading {@link SequencingObject}s
	 */
	public AnalysisWorkspaceServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService, GalaxyLibrariesService galaxyLibrariesService,
			IridaWorkflowsService iridaWorkflowsService,
			AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy,
			AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy,
			AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy,
			SequencingObjectService sequencingObjectService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.galaxyLibrariesService = galaxyLibrariesService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.analysisCollectionServiceGalaxy = analysisCollectionServiceGalaxy;
		this.analysisProvenanceServiceGalaxy = analysisProvenanceServiceGalaxy;
		this.analysisParameterServiceGalaxy = analysisParameterServiceGalaxy;
		this.sequencingObjectService = sequencingObjectService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String prepareAnalysisWorkspace(AnalysisSubmission analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkArgument(analysisSubmission.getRemoteAnalysisId() == null, "analysis id should be null");

		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();

		return workflowHistory.getId();
	}

	/**
	 * Builds a new AnalysisOutputFile from the given file in Galaxy.
	 * 
	 * @param analysisId
	 *            The id of the analysis performed in Galaxy.
	 * @param labelPrefix
	 *            The prefix to add to the label of this file.
	 * @param dataset
	 *            The dataset containing the data for the AnalysisOutputFile.
	 * @param outputDirectory
	 *            A directory to download the resulting output files.
	 * @return An AnalysisOutputFile storing a local copy of the Galaxy file.
	 * @throws IOException
	 *             If there was an issue creating a local file.
	 * @throws ExecutionManagerDownloadException
	 *             If there was an issue downloading the data from Galaxy.
	 * @throws ExecutionManagerException
	 *             If there was an issue extracting tool execution provenance
	 *             from Galaxy.
	 */
	private AnalysisOutputFile buildOutputFile(String analysisId, String labelPrefix, Dataset dataset,
			Path outputDirectory) throws IOException, ExecutionManagerDownloadException, ExecutionManagerException {
		String datasetId = dataset.getId();
		String fileName = dataset.getName();

		Path outputFile = outputDirectory.resolve(fileName);
		galaxyHistoriesService.downloadDatasetTo(analysisId, datasetId, outputFile);
		final ToolExecution toolExecution = analysisProvenanceServiceGalaxy.buildToolExecutionForOutputFile(analysisId,
				fileName);

		AnalysisOutputFile analysisOutputFile = new AnalysisOutputFile(outputFile, labelPrefix, datasetId,
				toolExecution);

		return analysisOutputFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedWorkflowGalaxy prepareAnalysisFiles(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteAnalysisId(), "analysisId is null");
		checkNotNull(analysisSubmission.getWorkflowId(), "workflowId is null");
		checkNotNull(analysisSubmission.getRemoteWorkflowId(), "remoteWorkflowId is null");

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
		IridaWorkflowInput workflowInput = iridaWorkflow.getWorkflowDescription().getInputs();

		Set<SingleEndSequenceFile> singleEndFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedEndFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			checkArgument(analysisSubmission.getReferenceFile().isPresent(),
					"workflow requires reference but none defined in submission");
		} else {
			checkArgument(!analysisSubmission.getReferenceFile().isPresent(),
					"workflow does not require a reference and a reference file is set in the submission");
		}

		if (!iridaWorkflow.getWorkflowDescription().acceptsSingleSequenceFiles()) {
			checkArgument(singleEndFiles.isEmpty(),
					"workflow does not accept single sequence files, but single sequence files are passed as input to "
							+ analysisSubmission);
		}

		if (!iridaWorkflow.getWorkflowDescription().acceptsPairedSequenceFiles()) {
			checkArgument(pairedEndFiles.isEmpty(),
					"workflow does not accept paired sequence files, but paired sequence files are passed as input to "
							+ analysisSubmission);
		}

		String temporaryLibraryName = AnalysisSubmission.class.getSimpleName() + "-" + UUID.randomUUID().toString();

		History workflowHistory = galaxyHistoriesService.findById(analysisSubmission.getRemoteAnalysisId());
		Library workflowLibrary = galaxyLibrariesService.buildEmptyLibrary(new GalaxyProjectName(temporaryLibraryName));

		// get unique files for pairs and single files
		Map<Sample, SingleEndSequenceFile> singleFiles = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(singleEndFiles);

		Map<Sample, SequenceFilePair> pairedFiles = sequencingObjectService
				.getUniqueSamplesForSequencingObjects(pairedEndFiles);

		// check that there aren't common sample names between single and paired
		if (samplesInCommon(singleFiles, pairedFiles)) {
			throw new SampleAnalysisDuplicateException(
					"Single and paired input files share a common sample for submission " + analysisSubmission);
		}

		String workflowId = analysisSubmission.getRemoteWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterServiceGalaxy
				.prepareAnalysisParameters(analysisSubmission.getInputParameters(), iridaWorkflow);
		WorkflowInputs inputs = workflowInputsGalaxy.getInputsObject();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());

		if (!singleFiles.isEmpty()) {
			String sequenceFilesLabelSingle = workflowInput.getSequenceReadsSingle().get();
			String workflowSequenceFileSingleInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
					sequenceFilesLabelSingle);
			CollectionResponse collectionResponseSingle = analysisCollectionServiceGalaxy
					.uploadSequenceFilesSingleEnd(singleFiles, workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFileSingleInputId, new WorkflowInputs.WorkflowInput(
					collectionResponseSingle.getId(), WorkflowInputs.InputSourceType.HDCA));
		}

		if (!pairedFiles.isEmpty()) {
			String sequenceFilesLabelPaired = workflowInput.getSequenceReadsPaired().get();
			String workflowSequenceFilePairedInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
					sequenceFilesLabelPaired);
			CollectionResponse collectionResponsePaired = analysisCollectionServiceGalaxy
					.uploadSequenceFilesPaired(pairedFiles, workflowHistory, workflowLibrary);
			inputs.setInput(workflowSequenceFilePairedInputId, new WorkflowInputs.WorkflowInput(
					collectionResponsePaired.getId(), WorkflowInputs.InputSourceType.HDCA));
		}

		String analysisId = workflowHistory.getId();

		if (iridaWorkflow.getWorkflowDescription().requiresReference()) {
			String referenceFileLabel = workflowInput.getReference().get();
			prepareReferenceFile(analysisSubmission.getReferenceFile().get(), workflowHistory, referenceFileLabel,
					workflowDetails, inputs);
		}

		return new PreparedWorkflowGalaxy(analysisId, workflowLibrary.getId(), new WorkflowInputsGalaxy(inputs));
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
	private boolean samplesInCommon(Map<Sample, ?> sampleSequenceFilesSingle,
			Map<Sample, ?> sampleSequenceFilesPaired) {
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
			WorkflowDetails workflowDetails, WorkflowInputs inputs)
			throws UploadException, GalaxyDatasetException, WorkflowException {

		Dataset referenceDataset = galaxyHistoriesService.fileToHistory(referenceFile.getFile(), InputFileType.FASTA,
				workflowHistory);

		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails,
				referenceFileLabel);

		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(referenceDataset.getId(), WorkflowInputs.InputSourceType.HDA));
	}

	/**
	 * Gets the prefix for a label for an output file based on the input
	 * {@link Sample} name.
	 * 
	 * @param analysisSubmission
	 *            The submission containing input {@link Sample}s.
	 * @param iridaWorkflow
	 *            The {@link IridaWorkflow}.
	 * @return The label prefix (sample name) if this workflow operates only on
	 *         a single sample, otherwise an empty String.
	 */
	private String getLabelPrefix(AnalysisSubmission analysisSubmission, IridaWorkflow iridaWorkflow) {
		String labelPrefix = null;

		if (iridaWorkflow.getWorkflowDescription().getInputs().requiresSingleSample()) {

			try {
				Set<SequencingObject> sequencingObjectsForAnalysisSubmission = sequencingObjectService
						.getSequencingObjectsForAnalysisSubmission(analysisSubmission);
				Set<Sample> samples = Sets.newHashSet();
				samples.addAll(sequencingObjectService
						.getUniqueSamplesForSequencingObjects(sequencingObjectsForAnalysisSubmission).keySet());

				Set<String> sampleNames = samples.stream().map(Sample::getSampleName).collect(Collectors.toSet());

				if (sampleNames.size() == 0) {
					logger.warn(
							"Cannot define sample name prefix for output files. Input sequence files for analysis submission "
									+ analysisSubmission + " have no associated samples.");
				} else if (sampleNames.size() > 1) {
					logger.warn(
							"Cannot define sample name prefix for output files. Input sequence files for analysis submission "
									+ analysisSubmission + " have multiple associated samples.");
				} else {
					labelPrefix = sampleNames.iterator().next();
				}
			} catch (EntityNotFoundException e) {
				logger.warn("Got exception when attempting to read sample names for submission " + analysisSubmission
						+ " for adding to output file label", e);

			}
		} else {
			logger.trace("IRIDA workflow " + iridaWorkflow
					+ " supports multiple samples.  Will not append sample name(s) for submission "
					+ analysisSubmission);
		}

		return labelPrefix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getAnalysisResults(AnalysisSubmission analysisSubmission) throws ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getWorkflowId(), "workflowId is null");
		checkNotNull(analysisSubmission.getRemoteWorkflowId(), "remoteWorkflowId is null");

		Path outputDirectory = Files.createTempDirectory("analysis-output");
		logger.trace("Created temporary directory " + outputDirectory + " for analysis output files");

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
		String analysisId = analysisSubmission.getRemoteAnalysisId();

		Map<String, IridaWorkflowOutput> outputsMap = iridaWorkflow.getWorkflowDescription().getOutputsMap();

		String labelPrefix = getLabelPrefix(analysisSubmission, iridaWorkflow);

		Map<String, AnalysisOutputFile> analysisOutputFiles = Maps.newHashMap();
		for (String analysisOutputName : outputsMap.keySet()) {
			String outputFileName = outputsMap.get(analysisOutputName).getFileName();
			Dataset outputDataset = galaxyHistoriesService.getDatasetForFileInHistory(outputFileName, analysisId);

			AnalysisOutputFile analysisOutput = buildOutputFile(analysisId, labelPrefix, outputDataset,
					outputDirectory);
			analysisOutputFiles.put(analysisOutputName, analysisOutput);
		}

		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();
		
		return new Analysis(analysisId, analysisOutputFiles, analysisType);
	}
}