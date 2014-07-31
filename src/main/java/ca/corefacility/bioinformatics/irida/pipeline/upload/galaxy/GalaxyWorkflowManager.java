package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Handles submission of workflows to Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowManager {
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowManager.class);
		
	private GalaxyHistoriesService galaxyHistory;
	private HistoriesClient historiesClient;
	private WorkflowsClient workflowsClient;
	
	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * @param galaxyInstance  A Galaxyinstance defining the Galaxy to submit to.
	 * @param galaxyHistory  A GalaxyHistory for methods on operating with Galaxy histories.
	 */
	public GalaxyWorkflowManager(HistoriesClient historiesClient,
			WorkflowsClient workflowsClient, GalaxyHistoriesService galaxyHistory) {
		checkNotNull(historiesClient, "historiesClient is null");
		checkNotNull(workflowsClient, "workflowsClient is null");
		checkNotNull(galaxyHistory, "galaxyHistory is null");
		
		this.galaxyHistory = galaxyHistory;
		this.historiesClient = historiesClient;
		this.workflowsClient = workflowsClient;
	}
	
	/**
	 * Checks whether or not the given workflow id is valid.
	 * @param workflowId  A workflow id to check.
	 * @throws WorkflowException  If the workflow id is invalid.
	 */
	private void checkWorkflowIdValid(String workflowId) throws WorkflowException {
		checkNotNull(workflowId, "workflow id is null");
		boolean invalid = true;
		
		try {
			invalid = workflowsClient.showWorkflow(workflowId) == null;
		} catch (Exception e) {
		}
		
		if (invalid) {
			throw new WorkflowException("workflow with id " + workflowId + " does not exist in Galaxy instance");
		}
	}
	
	/**
	 * Given a WorkflowDetails an a workflowInputLabel find the corresponding id for this input.
	 * @param workflowDetails  The WorkflowDetails describing the workflow.
	 * @param workflowInputLabel  The label defining the input to search for.
	 * @return  The id of the input corresponding to the passed label.
	 * @throws WorkflowException  If no such input id could be found.
	 */
	private String getWorkflowInputId(WorkflowDetails workflowDetails, String workflowInputLabel) throws WorkflowException {
		
		Map<String, WorkflowInputDefinition> workflowInputMap = workflowDetails.getInputs();
		
		Optional<Map.Entry<String, WorkflowInputDefinition>> e = 
				workflowInputMap.entrySet().stream().filter((entry) -> 
				workflowInputLabel.equals(entry.getValue().getLabel())).findFirst();
		
		if (e.isPresent()) {
			return e.get().getKey();
		} else {
			throw new WorkflowException("Cannot find workflowInputId for input label " + workflowInputLabel);
		}
	}
	
	/**
	 * Starts the execution of a workflow with a single fastq file and the given workflow id.
	 * @param inputFile  An input file to start the workflow.
	 * @param inputFileType The file type of the input file.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	public WorkflowOutputs runSingleFileWorkflow(Path inputFile, String inputFileType,
			String workflowId, String workflowInputLabel)
			throws ExecutionManagerException {
		checkNotNull(inputFile, "file is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
				
		checkArgument(Files.exists(inputFile), "inputFile " + inputFile + " does not exist");
		checkWorkflowIdValid(workflowId);
				
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		Dataset inputDataset = galaxyHistory.fileToHistory(inputFile, inputFileType, workflowHistory);
		
		String workflowInputId = getWorkflowInputId(workflowDetails, workflowInputLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(inputDataset.getId(), WorkflowInputs.InputSourceType.HDA));
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		
		return output;
	}
	
	/**
	 * Starts the execution of a workflow with a list of fastq files and the given workflow id.
	 * @param inputFilesForward  A list of forward read fastq files start the workflow.
	 * @param inputFilesReverse  A list of reverse read fastq files start the workflow.
	 * @param inputFileType The file type of the input files.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws ExecutionManagerException If there was an error executing the workflow.
	 */
	public WorkflowOutputs runSingleCollectionWorkflow(List<Path> inputFilesForward, List<Path> inputFilesReverse,
			String inputFileType, String workflowId, String workflowInputLabel)
			throws ExecutionManagerException {
		checkNotNull(inputFilesForward, "inputFilesForward is null");
		checkNotNull(inputFilesReverse, "inputFilesForward is null");
		checkArgument(inputFilesForward.size() == inputFilesReverse.size(),
				"inputFiles have different number of elements");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
		
		for (Path file : inputFilesForward) {
			checkArgument(Files.exists(file), "inputFileForward " + file + " does not exist");
		}
		
		for (Path file : inputFilesReverse) {
			checkArgument(Files.exists(file), "inputFilesReverse " + file + " does not exist");
		}
		
		checkWorkflowIdValid(workflowId);
				
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		List<Dataset> inputDatasetsForward = 
				uploadFilesListToHistory(inputFilesForward, inputFileType, workflowHistory);
		List<Dataset> inputDatasetsReverse = 
				uploadFilesListToHistory(inputFilesForward, inputFileType, workflowHistory);
		
		// construct list of datasets
		CollectionResponse collection = constructFileCollection(inputDatasetsForward, inputDatasetsReverse, workflowHistory);
		logger.debug("Constructed dataset collection: id=" + collection.getId() + ", " + collection.getName());
		
		String workflowInputId = getWorkflowInputId(workflowDetails, workflowInputLabel);

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(collection.getId(),
				WorkflowInputs.InputSourceType.HDCA));
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		
		return output;
	}
	
	/**
	 * Constructs a collection containing a list of files from the given datasets.
	 * @param inputDatasetsForward  The datasets to construct a collection from.
	 * @param workflowHistory  The history of the workflow to construct the collection within.
	 * @return  A CollectionResponse describing the workflow collection.
	 * @throws ExecutionManagerException 
	 */
	private CollectionResponse constructFileCollection(List<Dataset> inputDatasetsForward,
			List<Dataset> inputDatasetsReverse, History workflowHistory) throws ExecutionManagerException {
		CollectionDescription collectionDescription = new CollectionDescription();
		collectionDescription.setCollectionType("list:paired");
		collectionDescription.setName("collection");
		
		for (int i = 0; i < inputDatasetsForward.size(); i++) {
			Dataset datasetForward = inputDatasetsForward.get(i);
			Dataset datasetReverse = inputDatasetsReverse.get(i);
			
			HistoryDatasetElement elementForward = new HistoryDatasetElement();
			elementForward.setId(datasetForward.getId());
			elementForward.setName("forward");
			
			HistoryDatasetElement elementReverse = new HistoryDatasetElement();
			elementReverse.setId(datasetReverse.getId());
			elementReverse.setName("reverse");
			
		    // Create an object to link together the forward and reverse reads for file2
		    CollectionElement element = new CollectionElement();
		    element.setName("file"+i);
		    element.setCollectionType("paired");
		    element.addCollectionElement(elementForward);
		    element.addCollectionElement(elementReverse);
			
			collectionDescription.addDatasetElement(element);
		}
		
		try {
			return historiesClient.createDatasetCollection(workflowHistory.getId(), collectionDescription);
		} catch (RuntimeException e) {
			throw new ExecutionManagerException("Could not construct dataset collection", e);
		}
	}
	
	/**
	 * Uploads a list of files into the given history.
	 * @param dataFiles  The list of files to upload.
	 * @param inputFileType  The type of files to upload.
	 * @param workflowHistory  The history to upload the files into.
	 * @return  A list of Datasets describing each uploaded file.
	 * @throws UploadException  If an error occured uploading the file.
	 * @throws GalaxyDatasetNotFoundException If a dataset could not be cpnstructed for the uploaded file.
	 */
	private List<Dataset> uploadFilesListToHistory(List<Path> dataFiles,
			String inputFileType, History workflowHistory) throws UploadException, GalaxyDatasetNotFoundException {
		List<Dataset> inputDatasets = new LinkedList<Dataset>();
		
		for (Path file : dataFiles) {
			Dataset inputDataset = galaxyHistory.fileToHistory(file, inputFileType, workflowHistory);
			inputDatasets.add(inputDataset);
		}
		
		return inputDatasets;
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
	 * Gets a list of download URLs for the given passed WorkflowOutputs.
	 * @param workflowOutputs  A list of WorkflowOutputs to find the download URLs for.
	 * @return  A list of download URLs for each workflow output.
	 * @throws GalaxyOutputsForWorkflowException If there was an error getting information about
	 * 	the workflow outputs.
	 */
	public List<URL> getWorkflowOutputDownloadURLs(
			WorkflowOutputs workflowOutputs) throws GalaxyOutputsForWorkflowException {
		checkNotNull(workflowOutputs, "workflowOutputs is null");
		
		List<URL> workflowDownloadURLs = new LinkedList<URL>();
				
		try {
			for(String outputId : workflowOutputs.getOutputIds()) {
				Dataset dataset = historiesClient.showDataset(workflowOutputs.getHistoryId(), outputId);
				URL downloadURL = new URL(dataset.getFullDownloadUrl());
				workflowDownloadURLs.add(downloadURL);
			}
			
			return workflowDownloadURLs;
		} catch (RuntimeException | MalformedURLException e) {
			throw new GalaxyOutputsForWorkflowException(e);
		}
	}
}
