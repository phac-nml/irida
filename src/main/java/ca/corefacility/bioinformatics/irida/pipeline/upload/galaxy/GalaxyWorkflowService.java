package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Handles operating with workflows in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowService {
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowService.class);
		
	private HistoriesClient historiesClient;
	private WorkflowsClient workflowsClient;
	
	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * @param historiesClient  The HistoriesClient used to connect to Galaxy histories.
	 * @param workflowsClient  The WorkflowsClient used to connect to Galaxy workflows.
	 */
	public GalaxyWorkflowService(HistoriesClient historiesClient,
			WorkflowsClient workflowsClient) {
		checkNotNull(historiesClient, "historiesClient is null");
		checkNotNull(workflowsClient, "workflowsClient is null");
		
		this.historiesClient = historiesClient;
		this.workflowsClient = workflowsClient;
	}
	
	/**
	 * Checks whether or not the given workflow id is valid.
	 * @param workflowId  A workflow id to check.
	 * @return True if the workflow is valid, false otherwise.
	 */
	public boolean isWorkflowIdValid(String workflowId) {

		if (workflowId != null) {
			try {
				return workflowsClient.showWorkflow(workflowId) != null;
			} catch (Exception e) {
			}
		}

		return false;
	}
	
	/**
	 * Given a WorkflowDetails an a workflowInputLabel find the corresponding id for this input.
	 * @param workflowDetails  The WorkflowDetails describing the workflow.
	 * @param workflowInputLabel  The label defining the input to search for.
	 * @return  The id of the input corresponding to the passed label.
	 * @throws WorkflowException  If no such input id could be found.
	 */
	public String getWorkflowInputId(WorkflowDetails workflowDetails, String workflowInputLabel) throws WorkflowException {
		checkNotNull(workflowDetails, "workflowDetails is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");
		
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
