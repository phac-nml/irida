package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.DeleteGalaxyObjectFailedException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationOutputs;
import com.sun.jersey.api.client.ClientResponse;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles operating with workflows in Galaxy.
 */
public class GalaxyWorkflowService {
	private WorkflowsClient workflowsClient;

	private final Charset workflowCharset;

	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * 
	 * @param workflowsClient The WorkflowsClient used to connect to Galaxy workflows.
	 * @param workflowCharset The {@link Charset} to use for reading in workflows from files.
	 */
	public GalaxyWorkflowService(WorkflowsClient workflowsClient, Charset workflowCharset) {
		checkNotNull(workflowsClient, "workflowsClient is null");
		checkNotNull(workflowCharset, "workflowCharset is null");

		this.workflowsClient = workflowsClient;
		this.workflowCharset = workflowCharset;
	}

	/**
	 * Uploads a workflow definined in the given file to Galaxy.
	 * 
	 * @param workflowFile The file to upload.
	 * @return The id of the workflow in Galaxy.
	 * @throws IOException             If there was an issue reading the file.
	 * @throws WorkflowUploadException If there was an issue uploading the workflow to Galaxy.
	 */
	public String uploadGalaxyWorkflow(Path workflowFile) throws IOException, WorkflowUploadException {
		checkNotNull(workflowFile, "workflowFile is null");

		byte[] fileBytes = Files.readAllBytes(workflowFile);
		String workflowString = new String(fileBytes, workflowCharset);

		try {
			Workflow workflow = workflowsClient.importWorkflow(workflowString, false);
			return workflow.getId();
		} catch (RuntimeException e) {
			throw new WorkflowUploadException("Could not upload workflow from " + workflowFile, e);
		}
	}

	/**
	 * Given a WorkflowDetails an a workflowInputLabel find the corresponding id for this input.
	 * 
	 * @param workflowDetails    The WorkflowDetails describing the workflow.
	 * @param workflowInputLabel The label defining the input to search for.
	 * @return The id of the input corresponding to the passed label.
	 * @throws WorkflowException If no such input id could be found.
	 */
	public String getWorkflowInputId(WorkflowDetails workflowDetails, String workflowInputLabel)
			throws WorkflowException {
		checkNotNull(workflowDetails, "workflowDetails is null");
		checkNotNull(workflowInputLabel, "workflowInputLabel is null");

		Map<String, WorkflowInputDefinition> workflowInputMap = workflowDetails.getInputs();

		Optional<Map.Entry<String, WorkflowInputDefinition>> e = workflowInputMap.entrySet()
				.stream()
				.filter((entry) -> workflowInputLabel.equals(entry.getValue().getLabel()))
				.findFirst();

		if (e.isPresent()) {
			return e.get().getKey();
		} else {
			throw new WorkflowException("Cannot find workflowInputId for input label " + workflowInputLabel);
		}
	}

	/**
	 * Gets details about a given workflow.
	 * 
	 * @param workflowId The id of the workflow.
	 * @return A details object for this workflow.
	 * @throws WorkflowException If there was an issue getting the details of the workflow.
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
	 * 
	 * @param inputs The inputs to the workflow.
	 * @return A WorkflowOutputs object with information on output files in the workflow.
	 * @throws WorkflowException If there was an issue running the workflow.
	 */
	public WorkflowInvocationOutputs runWorkflow(WorkflowInputsGalaxy inputs) throws WorkflowException {
		checkNotNull(inputs, "inputs is null");

		try {
			return workflowsClient.invokeWorkflow(inputs.getInputsObject());
		} catch (RuntimeException e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * Deletes a workflow with the given id.
	 * 
	 * @param workflowId The id of the workflow to delete.
	 * @throws DeleteGalaxyObjectFailedException If there was an error while deleting the workflow.
	 */
	public void deleteWorkflow(String workflowId) throws DeleteGalaxyObjectFailedException {
		try {
			ClientResponse response = workflowsClient.deleteWorkflowRequest(workflowId);
			if (ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
				throw new DeleteGalaxyObjectFailedException("Could not workflow with id " + workflowId + ", status="
						+ response.getStatusInfo() + ", content=" + response.getEntity(String.class));
			}
		} catch (RuntimeException e) {
			throw new DeleteGalaxyObjectFailedException("Error while deleting workflow with id " + workflowId, e);
		}
	}
}
