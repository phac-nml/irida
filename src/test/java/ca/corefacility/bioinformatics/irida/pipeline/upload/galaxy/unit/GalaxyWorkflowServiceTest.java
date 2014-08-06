package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Unit tests for the GalaxyWorkflowManager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowServiceTest {

	@Mock private GalaxyHistoriesService galaxyHistory;
	@Mock private HistoriesClient historiesClient;
	@Mock private HistoryDetails historyDetails;
	@Mock private WorkflowsClient workflowsClient;
	@Mock private WorkflowDetails workflowDetails;
	@Mock private History workflowHistory;
	@Mock private Dataset inputDataset;
	@Mock private UniformInterfaceException uniformInterfaceException;
	@Mock private Dataset downloadDataset;
	
	private GalaxyWorkflowService galaxyWorkflowService;
		
	private static final String VALID_HISTORY_ID = "1";
	private static final String INVALID_HISTORY_ID = "2";
	
	private static final String VALID_WORKFLOW_ID = "1";
	private static final String INVALID_WORKFLOW_ID = "invalid";
	
	private static final String VALID_INPUT_LABEL = "fastq";
	
	private static final float delta = 0.00001f;
		
	private Map<String, WorkflowInputDefinition> workflowInputs;
	
	/**
	 * Sets up variables for workflow tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		galaxyWorkflowService = new GalaxyWorkflowService(historiesClient, workflowsClient);
		
		String workflowInputId = "1";
		WorkflowInputDefinition worklowInput = new WorkflowInputDefinition();
		worklowInput.setLabel(VALID_INPUT_LABEL);
		
		workflowInputs = new HashMap<String, WorkflowInputDefinition>();
		workflowInputs.put(workflowInputId, worklowInput);
		
		when(workflowsClient.showWorkflow(VALID_WORKFLOW_ID)).thenReturn(workflowDetails);
		when(workflowDetails.getInputs()).thenReturn(workflowInputs);
	}
	
	/**
	 * Tests checking for a valid workflow id.
	 */
	@Test
	public void testCheckWorkflowIdValid() {
		String workflowId = "valid";
		
		WorkflowDetails details = new WorkflowDetails();
		
		when(workflowsClient.showWorkflow(workflowId)).thenReturn(details);
		
		assertTrue(galaxyWorkflowService.isWorkflowIdValid(workflowId));
	}
	
	/**
	 * Tests checking for an invalid workflow id.
	 */
	@Test
	public void testCheckWorkflowIdInvalid() {
		String workflowId = "invalid";
		
		when(workflowsClient.showWorkflow(workflowId)).thenThrow(new RuntimeException());
		
		assertFalse(galaxyWorkflowService.isWorkflowIdValid(workflowId));
	}
	
	/**
	 * Tests getting a valid workflow input id from a workflow details.
	 * @throws WorkflowException 
	 */
	@Test
	public void testGetWorkflowInputIdValid() throws WorkflowException {
		WorkflowDetails details = new WorkflowDetails();
		WorkflowInputDefinition validDefinition = new WorkflowInputDefinition();
		validDefinition.setLabel("valid");
		
		Map<String, WorkflowInputDefinition> workflowInputMap = new HashMap<>();
		workflowInputMap.put("validInputId", validDefinition);
		details.setInputs(workflowInputMap);
		
		assertEquals("validInputId", galaxyWorkflowService.getWorkflowInputId(details, "valid"));
	}
	
	/**
	 * Tests failing to find a valid workflow input id from a workflow details.
	 * @throws WorkflowException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowInputIdInvalid() throws WorkflowException {
		WorkflowDetails details = new WorkflowDetails();
		WorkflowInputDefinition validDefinition = new WorkflowInputDefinition();
		validDefinition.setLabel("valid");
		
		Map<String, WorkflowInputDefinition> workflowInputMap = new HashMap<>();
		workflowInputMap.put("validInputId", validDefinition);
		details.setInputs(workflowInputMap);
		
		galaxyWorkflowService.getWorkflowInputId(details, "invalid");
	}
	
	/**
	 * Tests getting status for a completed/ok workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusOkState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1", "2"));
		validStateIds.put("running", Arrays.asList());
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("ok");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowService.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.OK, status.getState());
		assertEquals(100.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusRunningState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList());
		validStateIds.put("running", Arrays.asList("1", "2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowService.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.RUNNING, status.getState());
		assertEquals(0.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testGetStatusPartialCompleteState() throws ExecutionManagerException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1"));
		validStateIds.put("running", Arrays.asList("2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowService.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.RUNNING, status.getState());
		assertEquals(50.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for an invalid history.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetStatusInvalidHistory() throws ExecutionManagerException {
		when(historiesClient.showHistory(INVALID_HISTORY_ID)).thenThrow(uniformInterfaceException);
		galaxyWorkflowService.getStatusForHistory(INVALID_HISTORY_ID);
	}
	
	/**
	 * Tests getting a list of workflow output download URLs for each workflow output.
	 * @throws GalaxyOutputsForWorkflowException
	 * @throws MalformedURLException
	 */
	@Test
	public void testGetWorkflowOutputDownloadURLs() throws GalaxyOutputsForWorkflowException, MalformedURLException {
		String outputId = "1";
		String downloadString = "http://localhost/download";
		URL downloadURL = new URL(downloadString);
		List<String> outputIds = Arrays.asList(outputId);
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		workflowOutputs.setHistoryId(VALID_WORKFLOW_ID);
		workflowOutputs.setOutputIds(outputIds);
		
		when(historiesClient.showDataset(VALID_WORKFLOW_ID, outputId)).thenReturn(downloadDataset);
		when(downloadDataset.getFullDownloadUrl()).thenReturn(downloadString);
		
		List<URL> urls = galaxyWorkflowService.getWorkflowOutputDownloadURLs(workflowOutputs);
		assertEquals(Arrays.asList(downloadURL), urls);
	}
	
	/**
	 * Tests getting a list of workflow output download URLs from invalid workflow id.
	 * @throws GalaxyOutputsForWorkflowException
	 * @throws MalformedURLException
	 */
	@Test(expected=GalaxyOutputsForWorkflowException.class)
	public void testGetWorkflowOutputDownloadURLsInvalid() throws GalaxyOutputsForWorkflowException, MalformedURLException {
		String outputId = "1";
		String downloadString = "http://localhost/download";
		List<String> outputIds = Arrays.asList(outputId);
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		workflowOutputs.setHistoryId(INVALID_WORKFLOW_ID);
		workflowOutputs.setOutputIds(outputIds);
		
		when(historiesClient.showDataset(VALID_WORKFLOW_ID, outputId)).thenReturn(downloadDataset);
		when(downloadDataset.getFullDownloadUrl()).thenReturn(downloadString);
		
		galaxyWorkflowService.getWorkflowOutputDownloadURLs(workflowOutputs);
	}
}
