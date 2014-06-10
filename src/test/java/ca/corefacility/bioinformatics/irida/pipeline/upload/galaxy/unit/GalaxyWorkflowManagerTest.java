package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyOutputsForWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowManager;

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
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Unit tests for the GalaxyWorkflowManager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowManagerTest {

	@Mock private GalaxyHistoriesService galaxyHistory;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private HistoriesClient historiesClient;
	@Mock private HistoryDetails historyDetails;
	@Mock private WorkflowsClient workflowsClient;
	@Mock private WorkflowDetails workflowDetails;
	@Mock private History workflowHistory;
	@Mock private Dataset inputDataset;
	@Mock private UniformInterfaceException uniformInterfaceException;
	@Mock private Dataset downloadDataset;
	
	private GalaxyWorkflowManager galaxyWorkflowManager;
		
	private static final String VALID_HISTORY_ID = "1";
	private static final String INVALID_HISTORY_ID = "2";
	
	private static final String VALID_WORKFLOW_ID = "1";
	private static final String INVALID_WORKFLOW_ID = "invalid";
	
	private static final String VALID_INPUT_LABEL = "fastq";
	private static final String INVALID_INPUT_LABEL = "invalid";
	
	private static final String FILE_TYPE = "fastqsanger";
	private static final String INVALID_FILE_TYPE = "invalid";
	
	private static final float delta = 0.00001f;
	
	private Path dataFile;
	
	private Map<String, WorkflowInputDefinition> workflowInputs;
	
	/**
	 * Sets up variables for workflow tests.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		
		when(galaxyInstance.getHistoriesClient()).thenReturn(historiesClient);
		when(galaxyInstance.getWorkflowsClient()).thenReturn(workflowsClient);
		
		galaxyWorkflowManager = new GalaxyWorkflowManager(galaxyInstance, galaxyHistory);
		
		dataFile = Paths.get(this.getClass().getResource("testData1.fastq")
				.toURI());
		
		String workflowInputId = "1";
		WorkflowInputDefinition worklowInput = new WorkflowInputDefinition();
		worklowInput.setLabel(VALID_INPUT_LABEL);
		
		workflowInputs = new HashMap<String, WorkflowInputDefinition>();
		workflowInputs.put(workflowInputId, worklowInput);
		
		when(workflowsClient.showWorkflow(VALID_WORKFLOW_ID)).thenReturn(workflowDetails);
		when(workflowDetails.getInputs()).thenReturn(workflowInputs);
	}
	
	/**
	 * Tests getting status for a completed/ok workflow state.
	 * @throws WorkflowException 
	 */
	@Test
	public void testGetStatusOkState() throws WorkflowException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1", "2"));
		validStateIds.put("running", Arrays.asList());
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("ok");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.OK, status.getState());
		assertEquals(100.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws WorkflowException 
	 */
	@Test
	public void testGetStatusRunningState() throws WorkflowException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList());
		validStateIds.put("running", Arrays.asList("1", "2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.RUNNING, status.getState());
		assertEquals(0.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for a running workflow state.
	 * @throws WorkflowException 
	 */
	@Test
	public void testGetStatusPartialCompleteState() throws WorkflowException {
		Map<String, List<String>> validStateIds = new HashMap<String,List<String>>();
		validStateIds.put("ok", Arrays.asList("1"));
		validStateIds.put("running", Arrays.asList("2"));
		validStateIds.put("queued", Arrays.asList());
		
		when(historiesClient.showHistory(VALID_HISTORY_ID)).thenReturn(historyDetails);
		when(historyDetails.getState()).thenReturn("running");
		when(historyDetails.getStateIds()).thenReturn(validStateIds);
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusForHistory(VALID_HISTORY_ID);
		
		assertEquals(WorkflowState.RUNNING, status.getState());
		assertEquals(50.0f, status.getPercentComplete(), delta);
	}
	
	/**
	 * Tests getting status for an invalid history.
	 * @throws WorkflowException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetStatusInvalidHistory() throws WorkflowException {
		when(historiesClient.showHistory(INVALID_HISTORY_ID)).thenThrow(uniformInterfaceException);
		galaxyWorkflowManager.getStatusForHistory(INVALID_HISTORY_ID);
	}
	
	/**
	 * Tests running a single file workflow.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test
	public void testRunSingleFileWorkflowValid() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		
		when(galaxyHistory.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(workflowHistory.getId()).thenReturn(VALID_HISTORY_ID);
		when(galaxyHistory.fileToHistory(dataFile, FILE_TYPE, workflowHistory)).thenReturn(inputDataset);
		when(workflowsClient.runWorkflow(any(WorkflowInputs.class))).thenReturn(workflowOutputs);
		
		WorkflowOutputs expectedOutputs =
				galaxyWorkflowManager.runSingleFileWorkflow(dataFile, FILE_TYPE, VALID_WORKFLOW_ID, VALID_INPUT_LABEL);
		assertNotNull(expectedOutputs);
	}
	
	/**
	 * Tests running a workflow with an invalid input label.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=WorkflowException.class)
	public void testRunSingleFileWorkflowInvalidInput() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		
		when(galaxyHistory.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(workflowHistory.getId()).thenReturn(VALID_HISTORY_ID);
		when(galaxyHistory.fileToHistory(dataFile, FILE_TYPE, workflowHistory)).thenReturn(inputDataset);
		when(workflowsClient.runWorkflow(any(WorkflowInputs.class))).thenReturn(workflowOutputs);
		
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile, FILE_TYPE, VALID_WORKFLOW_ID, INVALID_INPUT_LABEL);
	}
	
	/**
	 * Tests running a workflow with an invalid workflow id.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=WorkflowException.class)
	public void testRunSingleFileWorkflowInvalidWorkflowId() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		
		when(galaxyHistory.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(workflowHistory.getId()).thenReturn(VALID_HISTORY_ID);
		when(galaxyHistory.fileToHistory(dataFile, FILE_TYPE, workflowHistory)).thenReturn(inputDataset);
		when(workflowsClient.runWorkflow(any(WorkflowInputs.class))).thenReturn(workflowOutputs);
		
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile, FILE_TYPE, INVALID_WORKFLOW_ID, VALID_INPUT_LABEL);
	}
	
	/**
	 * Tests running a workflow with an invalid input file type.
	 * @throws UploadException
	 * @throws GalaxyDatasetNotFoundException
	 * @throws IOException
	 * @throws WorkflowException
	 */
	@Test(expected=UploadException.class)
	public void testRunSingleFileWorkflowInvalidFileType() throws UploadException, GalaxyDatasetNotFoundException, IOException, WorkflowException {
		WorkflowOutputs workflowOutputs = new WorkflowOutputs();
		
		when(galaxyHistory.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(workflowHistory.getId()).thenReturn(VALID_HISTORY_ID);
		when(galaxyHistory.fileToHistory(dataFile, INVALID_FILE_TYPE, workflowHistory))
			.thenThrow(new UploadException(""));
		when(workflowsClient.runWorkflow(any(WorkflowInputs.class))).thenReturn(workflowOutputs);
		
		galaxyWorkflowManager.runSingleFileWorkflow(dataFile, INVALID_FILE_TYPE, VALID_WORKFLOW_ID, VALID_INPUT_LABEL);
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
		workflowOutputs.seOutputIds(outputIds);
		
		when(historiesClient.showDataset(VALID_WORKFLOW_ID, outputId)).thenReturn(downloadDataset);
		when(downloadDataset.getFullDownloadUrl()).thenReturn(downloadString);
		
		List<URL> urls = galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutputs);
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
		workflowOutputs.seOutputIds(outputIds);
		
		when(historiesClient.showDataset(VALID_WORKFLOW_ID, outputId)).thenReturn(downloadDataset);
		when(downloadDataset.getFullDownloadUrl()).thenReturn(downloadString);
		
		galaxyWorkflowManager.getWorkflowOutputDownloadURLs(workflowOutputs);
	}
}
