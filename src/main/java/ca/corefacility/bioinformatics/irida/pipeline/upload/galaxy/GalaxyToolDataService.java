package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyToolDataTableException;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;

/**
 * A service class for dealing with Galaxy Tool Data Tables.
 */
public class GalaxyToolDataService {

	private ToolDataClient toolDataClient;

	/**
	 * Builds a new GalaxyToolDataService with the given ToolDataClient.
	 *
	 * @param toolDataClient The ToolDataClient used to interact with Galaxy Tool Data Tables.
	 */
	public GalaxyToolDataService(ToolDataClient toolDataClient) {
		checkNotNull(toolDataClient, "toolDataClient is null");
		this.toolDataClient = toolDataClient;
	}

	/**
	 * Gets details about a given tool data table.
	 *
	 * @param dataTableId The id of the tool data table.
	 * @return The Tool Data Table Object.
	 * @throws GalaxyToolDataTableException If there was an issue getting the details of the tool data table.
	 */
	public TabularToolDataTable getToolDataTable(String dataTableId) throws GalaxyToolDataTableException {
		checkNotNull(dataTableId, "dataTableId is null");
		checkNotNull(toolDataClient, "toolDataClient is null");
		TabularToolDataTable toolDataTable = toolDataClient.showDataTable(dataTableId);
		if (toolDataTable != null) {
			return toolDataTable;
		} else {
			throw new GalaxyToolDataTableException("Could not find Tool Data Table named: " + dataTableId);
		}
	}

}
