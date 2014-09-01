package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowPreprationException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Tasks for executing a Phylogenomics Pipeline in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkspaceServicePhylogenomics 
	extends AnalysisWorkspaceServiceGalaxy<RemoteWorkflowPhylogenomics,
		AnalysisSubmissionPhylogenomics, AnalysisPhylogenomicsPipeline> {
	
	private static final String COLLECTION_NAME = "phylogenomics_collection_list";
	
	private GalaxyWorkflowService galaxyWorkflowService;
	
	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;
	private GalaxyLibraryBuilder libraryBuilder;
	
	/**
	 * Builds a new WorkspaceServicePhylogenomics with the given information.
	 * @param galaxyHistoriesService  A GalaxyHistoriesService for interacting with Galaxy Histories.
	 * @param galaxyWorkflowService  A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param sampleSequenceFileJoinRepository  A repository joining together sequence files and samples.
	 * @param libraryBuilder An object for building libraries in Galaxy.
	 */
	public WorkspaceServicePhylogenomics(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService,
			SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository,
			GalaxyLibraryBuilder libraryBuilder) {
		super(galaxyHistoriesService);
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.sampleSequenceFileJoinRepository = sampleSequenceFileJoinRepository;
		this.libraryBuilder = libraryBuilder;
	}
	
	/**
	 * Given a set of sequence files, gets a join between these sequence files and the corresponding samples.
	 * @param sequenceFiles  The set of sequence files.
	 * @return  A list of joins between sample and sequence files.
	 */
	private List<Join<Sample, SequenceFile>> getSequenceFileSamples(Set<SequenceFile> sequenceFiles) {
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = new LinkedList<>();	
		
		for (SequenceFile file : sequenceFiles) {
			Join<Sample, SequenceFile> sampleSequenceFile = 
				sampleSequenceFileJoinRepository.getSampleForSequenceFile(file);
			
			sampleSequenceFiles.add(sampleSequenceFile);
		}
		
		return sampleSequenceFiles;
	}
	
	/**
	 * Uploads a list of sequence files belonging to the given samples to Galaxy.
	 * TODO Only supports one sequence file per sample right now and throws an exception if more 
	 * 	than one sequence file shares the same sample.  This will be fixed in a future release.
	 * @param sampleSequenceFiles  A join between sequence files and samples.
	 * @param workflowHistory  The history to upload the sequence files into.
	 * @param workflowLibrary  A temporary library to upload files into.
	 * @return  A CollectionResponse for the dataset collection constructed from the given files.
	 * @throws ExecutionManagerException  If there was an error uploading the files.
	 */
	private CollectionResponse uploadSequenceFiles(List<Join<Sample, SequenceFile>> sampleSequenceFiles,
			History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {
		
		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST.toString());
		description.setName(COLLECTION_NAME);
		
		Map<Path,Sample> samplesMap = new HashMap<>();
		for (Join<Sample, SequenceFile> sampleSequenceJoin : sampleSequenceFiles) {
			SequenceFile sequenceFile = sampleSequenceJoin.getObject();
			Sample sample = sampleSequenceJoin.getSubject();
			
			if (samplesMap.containsValue(sample)) {
				throw new WorkflowPreprationException("Sequence file: " + sequenceFile.getFile() + " belongs to sample " +
						sample + " but there is another sequence file with this sample");
			} else {
				samplesMap.put(sequenceFile.getFile(),sample);
			}
		}
		
		// upload files to library and then to a history
		Set<Path> pathsToUpload = samplesMap.keySet();
		Map<Path, String> pathHistoryDatasetId = 
				galaxyHistoriesService.filesToLibraryToHistory(pathsToUpload,
				InputFileType.FASTQ_SANGER, workflowHistory, workflowLibrary, DataStorage.LOCAL);
		
		for (Path sequenceFilePath : samplesMap.keySet()) {
			if (!pathHistoryDatasetId.containsKey(sequenceFilePath)) {
				throw new UploadException("Error, no corresponding history item found for " +
						sequenceFilePath);
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
	 * Uploads the given reference file to the given history.
	 * @param referenceFile  The reference file to upload.
	 * @param workflowHistory  The history to upload the reference file to.
	 * @return  A Dataset containing the reference file within Galaxy.
	 * @throws UploadException  If there was an issue uploading a reference file.
	 * @throws GalaxyDatasetException  If there was an issue getting the corresponding Galaxy dataset.
	 */
	private Dataset uploadReferenceFile(ReferenceFile referenceFile, History workflowHistory) throws UploadException, GalaxyDatasetException {
		return galaxyHistoriesService.
				fileToHistory(referenceFile.getFile(), InputFileType.FASTA, workflowHistory);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedWorkflowGalaxy prepareAnalysisFiles(AnalysisSubmissionPhylogenomics analysisSubmission)
			throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteAnalysisId(), "analysisId is null");
		checkNotNull(analysisSubmission.getInputFiles(), "inputFiles are null");
		checkNotNull(analysisSubmission.getReferenceFile(), "referenceFile is null");
		
		String temporaryLibraryName = AnalysisSubmissionPhylogenomics.class.getSimpleName() + "-" +
				UUID.randomUUID().toString();
		
		History workflowHistory = galaxyHistoriesService.findById(analysisSubmission.getRemoteAnalysisId());
		Library workflowLibrary = libraryBuilder.buildEmptyLibrary(new GalaxyProjectName(temporaryLibraryName));
		
		List<Join<Sample, SequenceFile>> sampleSequenceFiles =
				getSequenceFileSamples(analysisSubmission.getInputFiles());

		CollectionResponse collectionResponse = 
				uploadSequenceFiles(sampleSequenceFiles, workflowHistory, workflowLibrary);

		Dataset referenceDataset = 
				uploadReferenceFile(analysisSubmission.getReferenceFile(), workflowHistory);
		
		RemoteWorkflowPhylogenomics remoteWorkflow = analysisSubmission.getRemoteWorkflow();
		
		String workflowId = remoteWorkflow.getWorkflowId();
		WorkflowDetails workflowDetails = galaxyWorkflowService.getWorkflowDetails(workflowId);
		
		String workflowSequenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getInputSequenceFilesLabel());
		String workflowReferenceFileInputId = galaxyWorkflowService.getWorkflowInputId(workflowDetails, 
				remoteWorkflow.getInputReferenceFileLabel());
		
		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowSequenceFileInputId,
				new WorkflowInputs.WorkflowInput(collectionResponse.getId(),
				WorkflowInputs.InputSourceType.HDCA));
		inputs.setInput(workflowReferenceFileInputId,
				new WorkflowInputs.WorkflowInput(referenceDataset.getId(),
				WorkflowInputs.InputSourceType.HDA));
		
		String analysisId = workflowHistory.getId();
		
		return new PreparedWorkflowGalaxy(analysisId, new WorkflowInputsGalaxy(inputs));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisPhylogenomicsPipeline getAnalysisResults(
			AnalysisSubmissionPhylogenomics analysisSubmission)
			throws ExecutionManagerException, IOException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteWorkflow(),
				"remote workflow is null");
		checkNotNull(analysisSubmission.getInputFiles(),
				"input sequence files is null");

		RemoteWorkflowPhylogenomics remoteWorkflow = analysisSubmission
				.getRemoteWorkflow();
		String analysisId = analysisSubmission.getRemoteAnalysisId();

		AnalysisPhylogenomicsPipeline results = new AnalysisPhylogenomicsPipeline(
				analysisSubmission.getInputFiles(), analysisId);

		Dataset treeOutput = galaxyHistoriesService.getDatasetForFileInHistory(
				remoteWorkflow.getOutputPhylogeneticTreeName(),
				analysisId);
		
		Dataset matrixOutput = galaxyHistoriesService.getDatasetForFileInHistory(
				remoteWorkflow.getOutputSnpMatrixName(),
				analysisId);
		
		Dataset tableOutput = galaxyHistoriesService.getDatasetForFileInHistory(
				remoteWorkflow.getOutputSnpTableName(),
				analysisId);

		results.setPhylogeneticTree(buildOutputFile(analysisId, treeOutput));
		results.setSnpMatrix(buildOutputFile(analysisId, matrixOutput));
		results.setSnpTable(buildOutputFile(analysisId, tableOutput));

		return results;
	}
}
