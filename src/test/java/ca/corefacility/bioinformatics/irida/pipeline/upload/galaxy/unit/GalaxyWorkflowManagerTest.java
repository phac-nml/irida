package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistory;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowManager;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Unit tests for the GalaxyWorkflowManager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowManagerTest {

	@Mock private GalaxyHistory galaxyHistory;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private HistoriesClient historiesClient;
	@Mock private HistoryDetails historyDetails;
	@Mock private WorkflowsClient workflowsClient;
	
	@Mock private UniformInterfaceException uniformInterfaceException;
	
	private GalaxyWorkflowManager galaxyWorkflowManager;
	
	private static final String VALID_HISTORY_ID = "1";
	private static final String INVALID_HISTORY_ID = "2";
	
	private static final float delta = 0.00001f;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		when(galaxyInstance.getHistoriesClient()).thenReturn(historiesClient);
		
		galaxyWorkflowManager = new GalaxyWorkflowManager(galaxyInstance, galaxyHistory);
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
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusFor(VALID_HISTORY_ID);
		
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
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusFor(VALID_HISTORY_ID);
		
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
		
		WorkflowStatus status = galaxyWorkflowManager.getStatusFor(VALID_HISTORY_ID);
		
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
		galaxyWorkflowManager.getStatusFor(INVALID_HISTORY_ID);
	}
}
