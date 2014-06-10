package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
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
	private GalaxyInstance galaxyInstance;
	
	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * @param galaxyInstance  A Galaxyinstance defining the Galaxy to submit to.
	 * @param galaxyHistory  A GalaxyHistory for methods on operating with Galaxy histories.
	 */
	public GalaxyWorkflowManager(GalaxyInstance galaxyInstance, GalaxyHistoriesService galaxyHistory) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(galaxyHistory, "galaxyHistory is null");
		
		this.galaxyHistory = galaxyHistory;
		this.galaxyInstance = galaxyInstance;
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
			WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
			invalid = workflowsClient.showWorkflow(workflowId) == null;
		} catch (Exception e) {
		}
		
		if (invalid) {
			throw new WorkflowException("workflow with id " + workflowId + " does not exist in Galaxy instance");
		}
	}
	
	/**
	 * Starts the execution of a workflow with a single fastq file and the given workflow id.
	 * @param inputFile  An input file to start the workflow.
	 * @param inputFileType The file type of the input file.
	 * @param workflowId  The id of the workflow to start.
	 * @param workflowInputLabel The label of a workflow input in Galaxy.
	 * @throws GalaxyDatasetNotFoundException If there was an error uploading the Galaxy dataset.
	 * @throws UploadException If there was an error uploading the Galaxy dataset.
	 * @throws IOException If there was an error with the file.
	 * @throws WorkflowException If there was an error with running the workflow.
	 */
	public WorkflowOutputs runSingleFileWorkflow(Path inputFile, String inputFileType,
			String workflowId, String workflowInputLabel)
			throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		checkNotNull(inputFile, "file is null");
		checkNotNull(inputFileType, "inputFileType is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
				
		checkArgument(inputFile.toFile().exists(), "inputFile " + inputFile + " does not exist");
		checkWorkflowIdValid(workflowId);
		
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		Dataset inputDataset = galaxyHistory.fileToHistory(inputFile, inputFileType, workflowHistory);
		
		// setup workflow inputs
		String workflowInputId = null;
		for(final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
			final String label = inputEntry.getValue().getLabel();
			if(label.equals(workflowInputLabel)) {
				workflowInputId = inputEntry.getKey();
			}
		}

		if (workflowInputId != null) {
			WorkflowInputs inputs = new WorkflowInputs();
			inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
			inputs.setWorkflowId(workflowDetails.getId());
			inputs.setInput(workflowInputId, new WorkflowInputs.WorkflowInput(inputDataset.getId(), WorkflowInputs.InputSourceType.HDA));
			
			// execute workflow
			WorkflowOutputs output = workflowsClient.runWorkflow(inputs);
	
			logger.debug("Running workflow in history " + output.getHistoryId());
			
			return output;
		} else {
			throw new WorkflowException("Could not find workflow input: " + workflowInputId);
		}
	}
	
	/**
	 * Gets the total number of history items for a given list of state ids.
	 * @param stateIds  A list of state ids to search through.
	 * @return  The total number of history items.
	 */
	private int getTotalHistoryItems(Map<String, List<String>> stateIds) {
		int sum = 0;
		
		for (String stateKey : stateIds.keySet()) {
			sum += stateIds.get(stateKey).size();
		}
		
		return sum;
	}
	
	/**
	 * Gets the total number of history items within the given workflow state.
	 * @param stateIds  The list of history items to search through.
	 * @param state  A state to search for.
	 * @return  The number of history items in this state.
	 */
	private int getHistoryItemsInState(Map<String, List<String>> stateIds, WorkflowState state) {
		return stateIds.get(state.toString()).size();
	}
	
	/**
	 * Gets the percentage completed running of items within the given list of history items.
	 * @param stateIds  The list of history items.
	 * @return  The percent of history items that are finished running.
	 */
	private float getPercentComplete(Map<String, List<String>> stateIds) {
		return 100.0f*(getHistoryItemsInState(stateIds, WorkflowState.OK)/(float)getTotalHistoryItems(stateIds));
	}
	
	/**
	 * Given a history id returns the status for the given workflow.
	 * @param historyId  The history id to use to find a workflow.
	 * @return  The WorkflowStatus for the given workflow.
	 * @throws WorkflowException If there was an exception when attempting to get the status for a history.
	 */
	public WorkflowStatus getStatusForHistory(String historyId) throws WorkflowException {
		WorkflowStatus workflowStatus;
		
		WorkflowState workflowState;
		float percentComplete;
		
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		
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
	 * @throws GalaxyOutputsForWorkflowException 
	 */
	public List<URL> getWorkflowOutputDownloadURLs(
			WorkflowOutputs workflowOutputs) throws GalaxyOutputsForWorkflowException {
		checkNotNull(workflowOutputs, "workflowOutputs is null");
		
		List<URL> workflowDownloadURLs = new LinkedList<URL>();
		
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		
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
