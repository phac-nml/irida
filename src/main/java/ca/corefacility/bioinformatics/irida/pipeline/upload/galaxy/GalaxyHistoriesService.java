package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDownloadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;
import ca.corefacility.bioinformatics.irida.model.workflow.DatasetCollectionType;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.ExecutionManagerSearch;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset.Source;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Class for working with Galaxy Histories.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistoriesService implements ExecutionManagerSearch<History, String> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyHistoriesService.class);

	private HistoriesClient historiesClient;
	private ToolsClient toolsClient;
	
	private static final String FORWARD_PAIR_NAME = "forward";
	private static final String REVERSE_PAIR_NAME = "reverse";
	
	private static final String BASE_NAME = "file";
	
	private static final String COLLECTION_NAME = "collection";
	
	/**
	 * Builds a new GalaxyHistory object for working with Galaxy Histories.
	 * @param historiesClient  The HistoriesClient for interacting with Galaxy histories.
	 * @param toolsClient  The ToolsClient for interacting with tools in Galaxy.
	 */
	public GalaxyHistoriesService(HistoriesClient historiesClient,
			ToolsClient toolsClient) {
		checkNotNull(historiesClient, "historiesClient is null");
		checkNotNull(toolsClient, "toolsClient is null");
		
		this.historiesClient = historiesClient;
		this.toolsClient = toolsClient;
	}
	
	/**
	 * Creates a new History for running a workflow.
	 * @return  A new History for running a workflow.
	 */
	public History newHistoryForWorkflow() {
		History history = new History();
		history.setName(UUID.randomUUID().toString());
		return historiesClient.create(history);
	}
	
	
	/**
	 * Count the total number of history items for a given list of state ids.
	 * @param stateIds  A list of state ids to search through.
	 * @return  The total number of history items.
	 */
	private int countTotalHistoryItems(Map<String, List<String>> stateIds) {
		return stateIds.values().stream().mapToInt(List::size).sum();
	}
	
	/**
	 * Count the total number of history items within the given workflow state.
	 * @param stateIds  The list of history items to search through.
	 * @param state  A state to search for.
	 * @return  The number of history items in this state.
	 */
	private int countHistoryItemsInState(Map<String, List<String>> stateIds, WorkflowState state) {
		return stateIds.get(state.toString()).size();
	}
	
	/**
	 * Gets the percentage completed running of items within the given list of history items.
	 * @param stateIds  The list of history items.
	 * @return  The percent of history items that are finished running.
	 */
	private float getPercentComplete(Map<String, List<String>> stateIds) {
		return 100.0f*(countHistoryItemsInState(stateIds, WorkflowState.OK)/(float)countTotalHistoryItems(stateIds));
	}
	
	/**
	 * Given a history id returns the status for the given workflow.
	 * @param historyId  The history id to use to find a workflow.
	 * @return  The WorkflowStatus for the given workflow.
	 * @throws ExecutionManagerException If there was an exception when attempting to get the status for a history.
	 */
	public WorkflowStatus getStatusForHistory(String historyId) throws ExecutionManagerException {
		checkNotNull(historyId, "historyId is null");
		
		WorkflowStatus workflowStatus;
		
		WorkflowState workflowState;
		float percentComplete;
			
		try {
			HistoryDetails details = historiesClient.showHistory(historyId);
			workflowState = WorkflowState.stringToState(details.getState());
			
			Map<String, List<String>> stateIds = details.getStateIds();
			percentComplete = getPercentComplete(stateIds);
			
			workflowStatus = new WorkflowStatus(workflowState, percentComplete);
			
			logger.debug("Details for history " + details.getId() + ": state=" + details.getState());
			
			return workflowStatus;
		} catch (ClientHandlerException | UniformInterfaceException e) {
			throw new WorkflowException(e);
		}
	}
	
	/**
	 * Transfers a dataset from a Galaxy library into a history for a workflow.
	 * @param libraryFileId The id of a file within a Galaxy library.
	 * @param history The history to transfer this library dataset into.
	 * @return A HistoryDetails object describing the details of the created history dataset.
	 */
	public HistoryDetails libraryDatasetToHistory(String libraryFileId, History history) {
		checkNotNull(libraryFileId, "libraryFileId is null");
		checkNotNull(history, "history is null");
				
		HistoryDataset historyDataset = new HistoryDataset();
		historyDataset.setSource(Source.LIBRARY);
		historyDataset.setContent(libraryFileId);
		
		return historiesClient.createHistoryDataset(
				history.getId(), historyDataset);
	}
	
	/**
	 * Uploads a file to a given history.
	 * @param path  The path to the file to upload.
	 * @param fileType The file type of the file to upload.
	 * @param history  The history to upload the file into.
	 * @return A Dataset object for the uploaded file.
	 * @throws UploadException  If there was an issue uploading the file to Galaxy.
	 * @throws GalaxyDatasetNotFoundException If a Dataset could not be found for the uploaded file to Galaxy.
	 */
	public Dataset fileToHistory(Path path, InputFileType fileType, History history) throws UploadException, GalaxyDatasetNotFoundException {
		checkNotNull(path, "path is null");
		checkNotNull(fileType, "fileType is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history id is null");
		checkState(path.toFile().exists(), "path " + path + " does not exist");
		
		File file = path.toFile();
				
		FileUploadRequest uploadRequest = new FileUploadRequest(history.getId(), file);
		uploadRequest.setFileType(fileType.toString());
		
		ClientResponse clientResponse = 
				toolsClient.uploadRequest(uploadRequest);
		
		if (clientResponse == null) {
			throw new UploadException("Could not upload " + file + " to history " + history.getId() +
					" ClientResponse is null");
		} else if (!ClientResponse.Status.OK.equals(clientResponse.getClientResponseStatus())) {
			String message = "Could not upload " + file + " to history " + history.getId() +
					" ClientResponse: " + clientResponse.getClientResponseStatus() + " " +
					clientResponse.getEntity(String.class);
			
			logger.error(message);
			
			throw new UploadException(message);
		} else {
			return getDatasetForFileInHistory(file.getName(), history);
		}
	}
	
	/**
	 * Uploads a list of files into the given history.
	 * @param dataFiles  The list of files to upload.
	 * @param inputFileType  The type of files to upload.
	 * @param workflowHistory  The history to upload the files into.String
	 * @return  A list of Datasets describing each uploaded file.
	 * @throws UploadException  If an error occured uploading the file.
	 * @throws GalaxyDatasetNotFoundException If a dataset could not be cpnstructed for the uploaded file.
	 */
	public List<Dataset> uploadFilesListToHistory(List<Path> dataFiles,
			InputFileType inputFileType, History history) throws UploadException, GalaxyDatasetNotFoundException {
		checkNotNull(dataFiles, "dataFiles is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(history, "history is null");
		
		List<Dataset> inputDatasets = new LinkedList<Dataset>();
		
		for (Path file : dataFiles) {
			Dataset inputDataset = fileToHistory(file, inputFileType, history);
			inputDatasets.add(inputDataset);
		}
		
		return inputDatasets;
	}
	
	/**
	 * Constructs a collection containing a list of files from the given datasets.
	 * @param inputDatasetsForward  The forward datasets to construct a collection from.
	 * @param inputDatasetsReverse  The reverse datasets to construct a collection from.
	 * @param history  The history to construct the collection within.
	 * @return  A CollectionResponse describing the dataset collection.
	 * @throws ExecutionManagerException  If an exception occured constructing the collection.
	 */
	public CollectionResponse constructPairedFileCollection(List<Dataset> inputDatasetsForward,
			List<Dataset> inputDatasetsReverse, History history) throws ExecutionManagerException {
		checkNotNull(inputDatasetsForward, "inputDatasetsForward is null");
		checkNotNull(inputDatasetsReverse, "inputDatasetsReverse is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history does not have an associated id");
		checkArgument(inputDatasetsForward.size() == inputDatasetsReverse.size(),
				"inputDatasets do not have equal sizes");
		
		CollectionDescription collectionDescription = new CollectionDescription();
		collectionDescription.setCollectionType(DatasetCollectionType.LIST_PAIRED.toString());
		collectionDescription.setName(COLLECTION_NAME);
		
		for (int i = 0; i < inputDatasetsForward.size(); i++) {
			Dataset datasetForward = inputDatasetsForward.get(i);
			Dataset datasetReverse = inputDatasetsReverse.get(i);
			
			HistoryDatasetElement elementForward = new HistoryDatasetElement();
			elementForward.setId(datasetForward.getId());
			elementForward.setName(FORWARD_PAIR_NAME);
			
			HistoryDatasetElement elementReverse = new HistoryDatasetElement();
			elementReverse.setId(datasetReverse.getId());
			elementReverse.setName(REVERSE_PAIR_NAME);
			
		    // Create an object to link together the forward and reverse reads for file2
		    CollectionElement element = new CollectionElement();
		    element.setName(BASE_NAME + i);
		    element.setCollectionType(DatasetCollectionType.PAIRED.toString());
		    element.addCollectionElement(elementForward);
		    element.addCollectionElement(elementReverse);
			
			collectionDescription.addDatasetElement(element);
		}
		
		try {
			return historiesClient.createDatasetCollection(history.getId(), collectionDescription);
		} catch (RuntimeException e) {
			throw new ExecutionManagerException("Could not construct dataset collection", e);
		}
	}
	
	/**
	 * Constructs a collection containing a list of datasets within a history.
	 * @param datasets  The datasets to construct a collection around.
	 * @param history  The history to construct the collection within.
	 * @return  A CollectionResponse describing the dataset collection.
	 * @throws ExecutionManagerException  If an exception occured constructing the collection.
	 */
	public CollectionResponse constructCollectionList(List<Dataset> datasets,
			History history) throws ExecutionManagerException {
		checkNotNull(datasets, "datasets is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history does not have an associated id");
		
		CollectionDescription collectionDescription = new CollectionDescription();
		collectionDescription.setCollectionType(DatasetCollectionType.LIST.toString());
		collectionDescription.setName(COLLECTION_NAME);
		
		for (Dataset dataset : datasets) {
			HistoryDatasetElement element = new HistoryDatasetElement();
			element.setId(dataset.getId());
			element.setName(dataset.getName());
			
			collectionDescription.addDatasetElement(element);
		}
		
		try {
			return historiesClient.createDatasetCollection(history.getId(), collectionDescription);
		} catch (RuntimeException e) {
			throw new ExecutionManagerException("Could not construct dataset collection", e);
		}
	}
	
	
	/**
	 * Gets a Dataset object for a file with the given name in the given history.
	 * @param filename  The name of the file to get a Dataset object for.
	 * @param history  The history to look for the dataset.
	 * @return The corresponding dataset for the given file name.
	 * @throws GalaxyDatasetNotFoundException  If the dataset could not be found.
	 */
	public Dataset getDatasetForFileInHistory(String filename, History history) throws GalaxyDatasetNotFoundException {
		checkNotNull(filename, "filename is null");
		checkNotNull(history, "history is null");
				
		List<HistoryContents> historyContentsList =
				historiesClient.showHistoryContents(history.getId());

		Optional<HistoryContents> h = historyContentsList.stream().
				filter((historyContents) -> filename.equals(historyContents.getName())).findFirst();
		if (h.isPresent()) {
			String dataId = h.get().getId();
			if (dataId != null) {
				Dataset dataset = historiesClient.showDataset(history.getId(), dataId);	
				if (dataset != null) {
					return dataset;
				}
			}
		}

		throw new GalaxyDatasetNotFoundException("dataset for file " + filename +
				" not found in Galaxy history " + history.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public History findById(String id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");
		
		List<History> galaxyHistories = historiesClient.getHistories();
		
		if (galaxyHistories != null) {
			Optional<History> h = galaxyHistories.stream().
					filter((history) -> id.equals(history.getId())).findFirst();
			if (h.isPresent()) {
				return h.get();
			}
		}
		
		throw new NoGalaxyHistoryException("No history for id " + id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(String id) {
		try {
			return findById(id) != null;
		} catch (ExecutionManagerObjectNotFoundException e) {
			return false;
		}
	}

	/**
	 * Given a particular output dataset id within a history, get the Galaxy id of this output dataset.
	 * @param historyId  The history to search through.
	 * @param name  The file name to find an output id for.
	 * @param outputIds  The list of output ids to search through.
	 * @return  The dataset of the corresponding output for the given label within the given history.
	 * @throws GalaxyDatasetNotFoundException If a dataset could not be found for the corresponding Galaxy information.
	 */
	public Dataset getOutputDataset(String historyId,
			String name, List<String> outputIds) throws GalaxyDatasetNotFoundException {
		checkNotNull(historyId, "historyId is null");
		checkNotNull(name, "label is null");
		checkNotNull(outputIds, "outputIds is null");
		
		History history = new History();
		history.setId(historyId);
		
		Dataset dataset = getDatasetForFileInHistory(name, history);
		
		if (outputIds.contains(dataset.getId())) {
			return dataset;
		} else {
			throw new GalaxyDatasetNotFoundException("Could not find valid dataset for label " + name + " in history " + historyId);
		}
	}

	/**
	 * Given a particular dataset id within a Galaxy history download this
	 * dataset to the local filesystem.
	 * 
	 * @param historyId
	 *            The id of the history containing the dataset.
	 * @param datasetId
	 *            The id of the dataset to download.
	 * @param destination
	 *            The destination to download a file to (will overwrite any
	 *            exisiting content).
	 * @throws IOException
	 *             If there was an error downloading the file.
	 * @throws GalaxyDownloadException
	 *             If there was an issue downloading the dataset.
	 */
	public void downloadDatasetTo(String historyId, String datasetId,
			Path destination) throws IOException, GalaxyDownloadException {
		checkNotNull(historyId, "historyId is null");
		checkNotNull(datasetId, "datasetId is null");
		checkNotNull(destination, "destination is null");

		try {
			historiesClient.downloadDataset(historyId, datasetId,
					destination.toFile());
		} catch (RuntimeException e) {
			throw new GalaxyDownloadException(
					"Could not download dataset identified by historyId="
							+ historyId + ", datasetId=" + datasetId
							+ " to destination=" + destination);
		}
	}
}
