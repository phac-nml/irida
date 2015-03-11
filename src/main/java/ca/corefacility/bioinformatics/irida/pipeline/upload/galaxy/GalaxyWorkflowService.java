package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Handles operating with workflows in Galaxy.
 *
 */
public class GalaxyWorkflowService {
			
	private HistoriesClient historiesClient;
	private WorkflowsClient workflowsClient;
	
	private final Charset workflowCharset;
	
	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * @param historiesClient  The HistoriesClient used to connect to Galaxy histories.
	 * @param workflowsClient  The WorkflowsClient used to connect to Galaxy workflows.
	 * @param workflowCharset  The {@link Charset} to use for reading in workflows from files.
	 */
	public GalaxyWorkflowService(HistoriesClient historiesClient,
			WorkflowsClient workflowsClient, Charset workflowCharset) {
		checkNotNull(historiesClient, "historiesClient is null");
		checkNotNull(workflowsClient, "workflowsClient is null");
		checkNotNull(workflowCharset, "workflowCharset is null");
		
		this.historiesClient = historiesClient;
		this.workflowsClient = workflowsClient;
		this.workflowCharset = workflowCharset;
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
	 * Uploads a workflow definined in the given file to Galaxy.
	 * @param workflowFile  The file to upload.
	 * @return  The id of the workflow in Galaxy.
	 * @throws IOException If there was an issue reading the file.
	 * @throws WorkflowUploadException If there was an issue uploading the workflow to Galaxy.
	 */
	public String uploadGalaxyWorkflow(Path workflowFile) throws IOException, WorkflowUploadException {
		checkNotNull(workflowFile, "workflowFile is null");
		
		byte[] fileBytes = Files.readAllBytes(workflowFile);
		String workflowString = new String(fileBytes, workflowCharset);
		
		try {
			Workflow workflow = workflowsClient.importWorkflow(workflowString);
			return workflow.getId();
		} catch (RuntimeException e) {
			throw new WorkflowUploadException("Could not upload workflow from " + workflowFile,e);
		}
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

	/**
	 * Gets details about a given workflow.
	 * 
	 * @param workflowId
	 *            The id of the workflow.
	 * @return A details object for this workflow.
	 * @throws WorkflowException
	 *             If there was an issue getting the details of the workflow.
	 */
	public WorkflowDetails getWorkflowDetails(String workflowId) throws WorkflowException {
		checkNotNull(workflowId, "workflowId is null");

		try {
			return workflowsClient.showWorkflow(workflowId);
		} catch (RuntimeException e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * Attempts to run the workflow definined by the given WorkflowInputs object.
	 * @param inputs  The inputs to the workflow.
	 * @return  A WorkflowOutputs object with information on output files in the workflow.
	 * @throws WorkflowException  If there was an issue running the workflow.
	 */
	public WorkflowOutputs runWorkflow(WorkflowInputsGalaxy inputs) throws WorkflowException {
		checkNotNull(inputs, "inputs is null");
		
		try {
			return workflowsClient.runWorkflow(inputs.getInputsObject());
		} catch (RuntimeException e) {
			throw new WorkflowException(e);
		}
	}
}
