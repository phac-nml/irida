package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.SampleAnalysisDuplicateException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * A service for constructing dataset collections of input files for workflows
 * in galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisCollectionServiceGalaxy {

	private static final String COLLECTION_NAME_SINGLE = "irida_sequence_files_single";
	private static final String COLLECTION_NAME_PAIRED = "irida_sequence_files_paired";

	private static final String FORWARD_NAME = "forward";
	private static final String REVERSE_NAME = "reverse";

	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;

	private GalaxyHistoriesService galaxyHistoriesService;

	/**
	 * Builds a new {@link AnalysisCollectionServiceGalaxy} with the given
	 * information.
	 * 
	 * @param galaxyHistoriesService
	 *            A GalaxyHistoriesService for interacting with Galaxy
	 *            Histories.
	 * @param sampleSequenceFileJoinRepository
	 *            A repository joining together sequence files and samples.
	 */
	public AnalysisCollectionServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.sampleSequenceFileJoinRepository = sampleSequenceFileJoinRepository;
	}

	/**
	 * Given a set of {@link SequenceFile}s, constructs a map between the
	 * {@link SequenceFile}s and the corresponding {@link Sample}s.
	 * 
	 * @param sequenceFiles
	 *            The set of sequence files.
	 * @return A map linking a sample and the sequence files to run.
	 * @throws SampleAnalysisDuplicateException
	 *             If there was more than one sequence file with the same
	 *             sample.
	 */
	public Map<Sample, SequenceFile> getSequenceFileSingleSamples(Set<SequenceFile> sequenceFiles)
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
	 * Gets a map of {@link SequenceFilePair}s and corresponding {@link Sample}
	 * s.
	 * 
	 * @param pairedInputFiles
	 *            A {@link Set} of {@link SequenceFilePair}s.
	 * @return A {@link Map} of between {@link Sample} and
	 *         {@link SequenceFilePair}.
	 * @throws SampleAnalysisDuplicateException
	 *             If there is a duplicate sample.
	 */
	public Map<Sample, SequenceFilePair> getSequenceFilePairedSamples(Set<SequenceFilePair> pairedInputFiles)
			throws SampleAnalysisDuplicateException {
		Map<Sample, SequenceFilePair> sequenceFilePairsSampleMap = new HashMap<>();

		for (SequenceFilePair filePair : pairedInputFiles) {
			SequenceFile pair1 = filePair.getFiles().iterator().next();
			Join<Sample, SequenceFile> pair1Join = sampleSequenceFileJoinRepository.getSampleForSequenceFile(pair1);
			Sample sample = pair1Join.getSubject();
			if (sequenceFilePairsSampleMap.containsKey(sample)) {
				SequenceFilePair previousPair = sequenceFilePairsSampleMap.get(sample);
				throw new SampleAnalysisDuplicateException("Sequence file pairs " + pair1 + ", " + previousPair
						+ " have the same sample " + sample);
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
	public CollectionResponse uploadSequenceFilesSingle(Map<Sample, SequenceFile> sampleSequenceFiles,
			History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {

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
	public CollectionResponse uploadSequenceFilesPaired(Map<Sample, SequenceFilePair> sampleSequenceFilesPaired,
			History workflowHistory, Library workflowLibrary) throws ExecutionManagerException {

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

			samplesMapPairForward.put(sample, fileForward.getFile());
			samplesMapPairReverse.put(sample, fileReverse.getFile());
			pathsToUpload.add(fileForward.getFile());
			pathsToUpload.add(fileReverse.getFile());
		}

		// upload files to library and then to a history
		Map<Path, String> pathHistoryDatasetId = galaxyHistoriesService.filesToLibraryToHistory(pathsToUpload,
				InputFileType.FASTQ_SANGER, workflowHistory, workflowLibrary, DataStorage.LOCAL);

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