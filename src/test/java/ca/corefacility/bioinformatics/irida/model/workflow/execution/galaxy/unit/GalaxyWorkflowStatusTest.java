package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	@BeforeEach
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

		assertTrue(workflowStatus.completedSuccessfully(), "Workflow did not complete successfully");
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

		assertTrue(workflowStatus.isRunning(), "Workflow is not still running");
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

		assertTrue(workflowStatus.isRunning(), "Workflow is not still running");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertTrue(workflowStatus.errorOccurred(), "Workflow is not in an error state");
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

		assertEquals(GalaxyWorkflowState.OK, workflowStatus.getState(), "workflow status not in correct state");
		assertEquals(1.0f, workflowStatus.getProportionComplete(), DELTA, "percentage complete not correct");
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

		assertEquals(GalaxyWorkflowState.QUEUED, workflowStatus.getState(), "workflow status not in correct state");
		assertEquals(0.0f, workflowStatus.getProportionComplete(), DELTA, "percentage complete not correct");
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

		assertEquals(GalaxyWorkflowState.RUNNING, workflowStatus.getState(), "workflow status not in correct state");
		assertEquals(0.5f, workflowStatus.getProportionComplete(), DELTA, "percentage complete not correct");
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

		assertEquals(GalaxyWorkflowState.RUNNING, workflowStatus.getState(), "workflow status not in correct state");
		assertEquals(0.25f, workflowStatus.getProportionComplete(), DELTA, "percentage complete not correct");
	}

	/**
	 * Tests failing to build a workflow status due to an unknwon state.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsUnknownState() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("unknown");
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		assertThrows(NullPointerException.class, () -> {
			GalaxyWorkflowStatus.builder(historyDetails).build();
		});
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details and
	 * failing.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetails() {
		assertThrows(NullPointerException.class, () -> {
			GalaxyWorkflowStatus.builder(null).build();
		});
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details
	 * state and failing.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetailsState() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setStateIds(Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID)));

		assertThrows(NullPointerException.class, () -> {
			GalaxyWorkflowStatus.builder(historyDetails).build();
		});
	}

	/**
	 * Tests building a GalaxyWorkflowStatus object with no history details
	 * state ids map and failing.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsErrorNoDetailsIdsMap() {
		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(null);

		assertThrows(NullPointerException.class, () -> {
			GalaxyWorkflowStatus.builder(historyDetails).build();
		});
	}

	/**
	 *	Tests building a GalaxyWorkflowStatus object which has a subset of
	 *	the expected states and passing
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsMissingStateSuccess(){
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("ok", Lists.newArrayList(DATASET_ID));
		stateIds.remove("running");

		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("ok");
		historyDetails.setStateIds(stateIds);

		GalaxyWorkflowStatus.builder(historyDetails).build();
	}

	/**
	 * Tests failing to build a GalaxyWorkflowStats object with some state
	 * ids in an unknown state.
	 */
	@Test
	public void testBuildWorkflowStatusFromHistoryDetailsUnknownStateIdsFail() {
		Map<String, List<String>> stateIds = Util.buildStateIdsWithStateFilled("running",
				Lists.newArrayList(DATASET_ID));
		stateIds.put("unknown", Lists.newArrayList(DATASET_ID));

		HistoryDetails historyDetails = new HistoryDetails();
		historyDetails.setState("running");
		historyDetails.setStateIds(stateIds);

		assertThrows(NullPointerException.class, () -> {
			GalaxyWorkflowStatus.builder(historyDetails).build();
		});
	}
}
