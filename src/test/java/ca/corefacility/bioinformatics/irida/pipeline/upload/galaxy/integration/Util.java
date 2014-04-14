package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.util.List;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;

/**
 * Utility methods for Galaxy integration tests.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class Util {
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
}
