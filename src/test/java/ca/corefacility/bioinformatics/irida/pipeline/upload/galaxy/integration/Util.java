package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.google.common.collect.Lists;

/**
 * Utility methods for Galaxy integration tests.
 *
 */
public class Util {
	private static final List<String> EMPTY_IDS = Lists.newLinkedList();
	private static final List<String> STATES = Lists.newArrayList("discarded", "empty", "error", "failed_metadata",
			"ok", "paused", "queued", "resubmitted", "running", "setting_metadata", "upload", "new");
	
	/**
	 * Given a file within a Galaxy history, finds the id of that file.
	 * @param filename  The name of the file within a history.
	 * @param historyId  The id of the history.
	 * @param galaxyInstance  The GalaxyInstance to use for connections.
	 * @return  The id of the file in this history, or null if no such file.
	 */
	public static String getIdForFileInHistory(String filename, String historyId,
			GalaxyInstance galaxyInstance) {
		String dataId = null;
		List<HistoryContents> historyContentsList = galaxyInstance
				.getHistoriesClient().showHistoryContents(historyId);

		for (HistoryContents contents : historyContentsList) {
			if (filename.equals(contents.getName())) {
				dataId = contents.getId();
				break;
			}
		}

		return dataId;
	}
	
	/**
	 * Waits for the given history to complete or until a timeout occurs.
	 * @param historyId  The id of the history to wait for.
	 * @param historyService  The history service to get the status of the history.
	 * @param timeout  The timeout, in seconds.
	 * @throws TimeoutException  If a timeout occurs
	 * @throws ExecutionManagerException 
	 * @throws InterruptedException 
	 */
	public static void waitUntilHistoryComplete(String historyId, GalaxyHistoriesService historyService, int timeout) throws TimeoutException, ExecutionManagerException, InterruptedException {
		
		GalaxyWorkflowStatus workflowStatus;
		
		long timeBefore = System.currentTimeMillis();
		do {
			workflowStatus = historyService.getStatusForHistory(historyId);
			long timeAfter = System.currentTimeMillis();
			double deltaSeconds = (timeAfter - timeBefore)/1000.0;
			if (deltaSeconds <= timeout) {
				Thread.sleep(2000);
			} else {
				throw new TimeoutException("Timeout for history " + historyId +
						" " + deltaSeconds + "s > " + timeout + "s");
			}
		} while (!GalaxyWorkflowState.OK.equals(workflowStatus.getState()));
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
	public static Map<String, List<String>> buildStateIdsWithStateFilled(String stateToFill, List<String> ids) {
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
}
