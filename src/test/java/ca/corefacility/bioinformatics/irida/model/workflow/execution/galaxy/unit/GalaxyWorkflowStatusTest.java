package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.unit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;

import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.google.common.collect.Lists;

/**
 * Tests out building a new {@link GalaxyWorkflowState}.
 **/
public class GalaxyWorkflowStatusTest {

	private static float DELTA = 0.00001f;

	private static final List<String> EMPTY_IDS = Lists.newLinkedList();
	private static final String DATASET_ID = "1";

	private static final List<String> STATES = Lists.newArrayList("discarded", "empty", "error", "failed_metadata",
			"ok", "paused", "queued", "resubmitted", "running", "setting_metadata", "upload");
	
	/**
	 * Sets up objects for test.
	 */
	@Before
	public void setup() {
	}

	/**
	 * Builds a map of states to state ids with the given state filled with the
	 * given ids.
	 * 
	 * @param stateToFill
	 *            The state to set the given ids with.
	 * @param ids
	 *            The ids to add to the given state.
	 * @return A map of states to a list of ids.
	 */
	private Map<String, List<String>> buildStateIdsWithStateFilled(String stateToFill, List<String> ids) {
		Map<String, List<String>> stateIds = new HashMap<>();

		for (String state : STATES) {
			if (state.equals(stateToFill)) {
				stateIds.put(state, ids);
			} else {
				stateIds.put(state, EMPTY_IDS);
			}
		}

		return stateIds;
	}

	/**
	 * Tests successfully building a workflow status from history details (everything complete).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessComplete() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.buildStatusFromHistoryDetails(historyDetails);

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.OK, workflowStatus.getState());
		assertEquals("percentage complete not correct", 100.0f, workflowStatus.getPercentComplete(), DELTA);
	}
	
	/**
	 * Tests successfully building a workflow status from history details (still queued).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessQueued() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("queued");
		historyDetails.setStateIds(buildStateIdsWithStateFilled("queued", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.buildStatusFromHistoryDetails(historyDetails);

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.QUEUED, workflowStatus.getState());
		assertEquals("percentage complete not correct", 0.0f, workflowStatus.getPercentComplete(), DELTA);
	}
	
	/**
	 * Tests successfully building a workflow status from history details (still running).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		Map<String, List<String>> stateIds = buildStateIdsWithStateFilled("running", Lists.newArrayList(DATASET_ID));
		stateIds.put("ok", Lists.newArrayList(DATASET_ID));
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.buildStatusFromHistoryDetails(historyDetails);

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.RUNNING, workflowStatus.getState());
		assertEquals("percentage complete not correct", 50.0f, workflowStatus.getPercentComplete(), DELTA);
	}
	
	/**
	 * Tests successfully building a workflow status from history details.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsUnknownState() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("unknown new galaxy state");

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.buildStatusFromHistoryDetails(historyDetails);

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.UNKNOWN, workflowStatus.getState());
	}
}
