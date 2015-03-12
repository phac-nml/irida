package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;

import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Unit tests for the GalaxyWorkflowManager.
 *
 */
public class GalaxyWorkflowServiceTest {

	@Mock private GalaxyHistoriesService galaxyHistory;
	@Mock private HistoriesClient historiesClient;
	@Mock private WorkflowsClient workflowsClient;
	@Mock private WorkflowDetails workflowDetails;
	@Mock private History workflowHistory;
	@Mock private Dataset inputDataset;
	@Mock private Dataset downloadDataset;
	@Mock private GalaxyResponseException responseException;
	
	private GalaxyWorkflowService galaxyWorkflowService;
	
	private static final String VALID_WORKFLOW_ID = "1";
	private static final String INVALID_WORKFLOW_ID = "invalid";
	
	private static final String VALID_INPUT_LABEL = "fastq";
			
	private Map<String, WorkflowInputDefinition> workflowInputs;
	
	/**
	 * Sets up variables for workflow tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		galaxyWorkflowService = new GalaxyWorkflowService(historiesClient, workflowsClient, StandardCharsets.UTF_8);
		
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
