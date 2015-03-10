package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.unit;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.Util;

import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.google.common.collect.Lists;

/**
 * Tests out building a new {@link GalaxyWorkflowState}.
 **/
public class GalaxyWorkflowStatusTest {

	private static float DELTA = 0.00001f;

	private static final String DATASET_ID = "1";
	private static final String DATASET_ID2 = "2";
	private static final String DATASET_ID3 = "3";

	/**
	 * Sets up objects for test.
	 */
	@Before
	public void setup() {
	}

	/**
	 * Tests whether or not this workflow completed successfully.
	 */
	@Test
	public void testCompletedSuccessfully() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow did not complete successfully", workflowStatus.completedSuccessfully());
	}

	/**
	 * Tests whether or not this workflow is still running.
	 */
	@Test
	public void testIsRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("running", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not still running", workflowStatus.isRunning());
	}

	/**
	 * Tests whether or not this workflow is considered still running (while
	 * queued).
	 */
	@Test
	public void testIsRunningWhileQueued() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("queued");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("queued", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not still running", workflowStatus.isRunning());
	}

	/**
	 * Tests whether or not this workflow is in an error state.
	 */
	@Test
	public void testErrorOccured() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("error");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("error", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not an error occured even while still running.
	 */
	@Test
	public void testErrorOccuredWhileStillRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("error", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not this workflow is in an error state when there is
	 * failed metadata.
	 */
	@Test
	public void testErrorOccuredFailedMetadata() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("failed_metadata");
		historyDetails
				.setStateIds(Util.buildStateIdsWithStateFilled("failed_metadata", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not this workflow is in an error state when there is
	 * failed metadata but the overall workflow is still running.
	 */
	@Test
	public void testErrorOccuredFailedMetadataStillRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails
				.setStateIds(Util.buildStateIdsWithStateFilled("failed_metadata", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not this workflow is in an error state when it is an
	 * empty state.
	 */
	@Test
	public void testErrorOccuredEmpty() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("empty");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("empty", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not this workflow is in an error state when it is empty
	 * but the overall workflow is still running.
	 */
	@Test
	public void testErrorOccuredEmptyStillRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("empty", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}
	
	/**
	 * Tests whether or not this workflow is in an error state when it is discarded.
	 */
	@Test
	public void testErrorOccuredDiscarded() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("discarded");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("discarded", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests whether or not this workflow is in an error state when it is discarded
	 * but the overall workflow is still running.
	 */
	@Test
	public void testErrorOccuredDiscardedStillRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("discarded");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("discarded", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertTrue("Workflow is not in an error state", workflowStatus.errorOccurred());
	}

	/**
	 * Tests successfully building a workflow status from history details
	 * (everything complete).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessComplete() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.OK, workflowStatus.getState());
		assertEquals("percentage complete not correct", 100.0f, workflowStatus.getPercentComplete(), DELTA);
	}

	/**
	 * Tests successfully building a workflow status from history details (still
	 * queued).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessQueued() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("queued");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("queued", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.QUEUED, workflowStatus.getState());
		assertEquals("percentage complete not correct", 0.0f, workflowStatus.getPercentComplete(), DELTA);
	}

	/**
	 * Tests successfully building a workflow status from history details (still
	 * running).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessRunning() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("running",
				Lists.newArrayList(DATASET_ID));
		stateIds.put("ok", Lists.newArrayList(DATASET_ID));
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.RUNNING, workflowStatus.getState());
		assertEquals("percentage complete not correct", 50.0f, workflowStatus.getPercentComplete(), DELTA);
	}

	/**
	 * Tests successfully building a workflow status from history details
	 * (quater complete).
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsSuccessQuarterComplete() {
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("running",
				Lists.newArrayList(DATASET_ID, DATASET_ID2, DATASET_ID3));
		stateIds.put("ok", Lists.newArrayList(DATASET_ID));

		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.RUNNING, workflowStatus.getState());
		assertEquals("percentage complete not correct", 25.0f, workflowStatus.getPercentComplete(), DELTA);
	}

	/**
	 * Tests successfully building a workflow status from history details.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsUnknownState() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("unknown new galaxy state");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.UNKNOWN, workflowStatus.getState());
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details and
	 * failing.
	 */
	@Test(expected = NullPointerException.class)
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetails() {
		GalaxyWorkflowStatus.builder(null).build();
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details
	 * state and failing.
	 */
	@Test(expected = NullPointerException.class)
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetailsState() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		GalaxyWorkflowStatus.builder(historyDetails).build();
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details
	 * state ids map and failing.
	 */
	@Test(expected = NullPointerException.class)
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetailsIdsMap() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(null);

		GalaxyWorkflowStatus.builder(historyDetails).build();
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object which has a missing state
	 * from Galaxy and failing.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBuildWorkflowStatusFromHistoryDetailsErrorMissingState() {
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID));
		stateIds.remove("running");

		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus.builder(historyDetails).build();
	}

	/**
	 * Tests successfully building a GalaxyWorkflowStats object with some state
	 * ids in an unknown state.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsUnknownStateIds() {
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("running",
				Lists.newArrayList(DATASET_ID));
		stateIds.put("unknown", Lists.newArrayList(DATASET_ID));

		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus workflowStatus = GalaxyWorkflowStatus.builder(historyDetails).build();

		assertEquals("workflow status not in correct state", GalaxyWorkflowState.RUNNING, workflowStatus.getState());
		assertEquals("percentage complete not correct", 0.0f, workflowStatus.getPercentComplete(), DELTA);
	}
}
