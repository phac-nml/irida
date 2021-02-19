package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTempFile;
import ca.corefacility.bioinformatics.irida.pipeline.upload.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTempFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaTemporaryFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A service for constructing dataset collections of input files for workflows
 * in galaxy.
 * 
 */
public class AnalysisCollectionServiceGalaxy {

	private static final String COLLECTION_NAME_SINGLE = "irida_sequence_files_single";
	private static final String COLLECTION_NAME_PAIRED = "irida_sequence_files_paired";

	private static final String FORWARD_NAME = "forward";
	private static final String REVERSE_NAME = "reverse";

	private GalaxyHistoriesService galaxyHistoriesService;
	private IridaFileStorageUtility iridaFileStorageUtility;
	private AnalysisSubmissionTempFileRepository analysisSubmissionTempFileRepository;

	/**
	 * Builds a new {@link AnalysisCollectionServiceGalaxy} with the given
	 * information.
	 *
	 * @param galaxyHistoriesService               A GalaxyHistoriesService for interacting with
	 *                                             Galaxy Histories.
	 * @param iridaFileStorageUtility              The file storage utility implementation
	 * @param analysisSubmissionTempFileRepository {@link AnalysisSubmissionTempFileRepository} for {@link AnalysisSubmissionTempFile} objects
	 */
	public AnalysisCollectionServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			IridaFileStorageUtility iridaFileStorageUtility,
			AnalysisSubmissionTempFileRepository analysisSubmissionTempFileRepository) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.iridaFileStorageUtility = iridaFileStorageUtility;
		this.analysisSubmissionTempFileRepository = analysisSubmissionTempFileRepository;
	}

	/**
	 * Uploads a list of single sequence files belonging to the given samples to
	 * Galaxy.
	 *
	 * @param sampleSequenceFiles A map between {@link Sample} and
	 *                            {@link SingleEndSequenceFile}.
	 * @param workflowHistory     The history to upload the sequence files into.
	 * @param workflowLibrary     A temporary library to upload files into.
	 * @param analysisSubmission  The {@link AnalysisSubmission}
	 * @return A CollectionResponse for the dataset collection constructed from the
	 * given files.
	 * @throws ExecutionManagerException If there was an error uploading the files.
	 * @throws IOException               If there was an error reading the sequence file.
	 */
	public CollectionResponse uploadSequenceFilesSingleEnd(
			Map<Sample, SingleEndSequenceFile> sampleSequenceFiles, History workflowHistory,
			Library workflowLibrary, AnalysisSubmission analysisSubmission) throws ExecutionManagerException, IOException {

		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST.toString());
		description.setName(COLLECTION_NAME_SINGLE);

		Map<Path, Sample> samplesMap = new HashMap<>();
		for (Sample sample : sampleSequenceFiles.keySet()) {
			SingleEndSequenceFile sequenceFile = sampleSequenceFiles.get(sample);
			IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(sequenceFile.getSequenceFile().getFile(), "analysis");
			samplesMap.put(iridaTemporaryFile.getFile(), sample);

			/*
			 If the storage type is an object store then we create an AnalysisSubmissionTempFile
			 record to save to the database. The cleanup of these files happens once the final
			 workflow status is set for an analysis.
			 */
			if (!iridaFileStorageUtility.isStorageTypeLocal()) {
				AnalysisSubmissionTempFile analysisSubmissionTempFile = new AnalysisSubmissionTempFile(analysisSubmission,
						iridaTemporaryFile.getFile(), iridaTemporaryFile.getDirectoryPath());
				analysisSubmissionTempFileRepository.save(analysisSubmissionTempFile);
			}
		}

		// upload files to library and then to a history
		Map<Path, String> pathHistoryDatasetId = galaxyHistoriesService.filesToLibraryToHistory(samplesMap.keySet(),
				workflowHistory, workflowLibrary, DataStorage.LOCAL);

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
	 * @param sampleSequenceFilesPaired A map between {@link Sample} and
	 *                                  {@link SequenceFilePair}.
	 * @param workflowHistory           The history to upload the sequence files
	 *                                  into.
	 * @param workflowLibrary           A temporary library to upload files into.
	 * @param analysisSubmission        The {@link AnalysisSubmission}
	 * @return A CollectionResponse for the dataset collection constructed from the
	 * given files.
	 * @throws ExecutionManagerException If there was an error uploading the files.
	 * @throws IOException               If there was an error reading the sequence file.
	 */
	public CollectionResponse uploadSequenceFilesPaired(
			Map<Sample, SequenceFilePair> sampleSequenceFilesPaired, History workflowHistory,
			Library workflowLibrary, AnalysisSubmission analysisSubmission) throws ExecutionManagerException, IOException {

		CollectionDescription description = new CollectionDescription();
		description.setCollectionType(DatasetCollectionType.LIST_PAIRED.toString());
		description.setName(COLLECTION_NAME_PAIRED);

		Map<Sample, Path> samplesMapPairForward = new HashMap<>();
		Map<Sample, Path> samplesMapPairReverse = new HashMap<>();
		Set<Path> pathsToUpload = new HashSet<>();
		for (Sample sample : sampleSequenceFilesPaired.keySet()) {
			SequenceFilePair sequenceFilePair = sampleSequenceFilesPaired.get(sample);
			SequenceFile fileForward = sequenceFilePair.getForwardSequenceFile();
			SequenceFile fileReverse = sequenceFilePair.getReverseSequenceFile();

			IridaTemporaryFile iridaTemporaryFileForward = iridaFileStorageUtility.getTemporaryFile(fileForward.getFile(), "analysis");
			IridaTemporaryFile iridaTemporaryFileReverse = iridaFileStorageUtility.getTemporaryFile(fileReverse.getFile(), "analysis");

			samplesMapPairForward.put(sample, iridaTemporaryFileForward.getFile());
			samplesMapPairReverse.put(sample, iridaTemporaryFileReverse.getFile());
			pathsToUpload.add(iridaTemporaryFileForward.getFile());
			pathsToUpload.add(iridaTemporaryFileReverse.getFile());

			/*
			 If the storage type is an object store then we create an AnalysisSubmissionTempFile
			 record to save to the database. The cleanup of these files happens once the final
			 workflow status is set for an analysis.
			 */
			if (!iridaFileStorageUtility.isStorageTypeLocal()) {
				AnalysisSubmissionTempFile analysisSubmissionTempFileForward = new AnalysisSubmissionTempFile(
						analysisSubmission, iridaTemporaryFileForward.getFile(),
						iridaTemporaryFileForward.getDirectoryPath());
				AnalysisSubmissionTempFile analysisSubmissionTempFileReverse = new AnalysisSubmissionTempFile(
						analysisSubmission, iridaTemporaryFileReverse.getFile(),
						iridaTemporaryFileReverse.getDirectoryPath());
				analysisSubmissionTempFileRepository.save(analysisSubmissionTempFileForward);
				analysisSubmissionTempFileRepository.save(analysisSubmissionTempFileReverse);
			}
		}

		// upload files to library and then to a history
		Map<Path, String> pathHistoryDatasetId = galaxyHistoriesService.filesToLibraryToHistory(pathsToUpload,
				workflowHistory, workflowLibrary, DataStorage.LOCAL);

		for (Sample sample : sampleSequenceFilesPaired.keySet()) {
			Path fileForward = samplesMapPairForward.get(sample);
			Path fileReverse = samplesMapPairReverse.get(sample);

			if (!pathHistoryDatasetId.containsKey(fileForward)) {
				throw new UploadException("Error, no corresponding history item found for " + fileForward);
			} else if (!pathHistoryDatasetId.containsKey(fileReverse)) {
				throw new UploadException("Error, no corresponding history item found for " + fileReverse);
			} else {
				String datasetHistoryIdForward = pathHistoryDatasetId.get(fileForward);
				String datasetHistoryIdReverse = pathHistoryDatasetId.get(fileReverse);

				CollectionElement pairedElement = new CollectionElement();
				pairedElement.setName(sample.getSampleName());
				pairedElement.setCollectionType(DatasetCollectionType.PAIRED.toString());

				HistoryDatasetElement datasetElementForward = new HistoryDatasetElement();
				datasetElementForward.setId(datasetHistoryIdForward);
				datasetElementForward.setName(FORWARD_NAME);
				pairedElement.addCollectionElement(datasetElementForward);

				HistoryDatasetElement datasetElementReverse = new HistoryDatasetElement();
				datasetElementReverse.setId(datasetHistoryIdReverse);
				datasetElementReverse.setName(REVERSE_NAME);
				pairedElement.addCollectionElement(datasetElementReverse);

				description.addDatasetElement(pairedElement);
			}
		}

		return galaxyHistoriesService.constructCollection(description, workflowHistory);
	}
}