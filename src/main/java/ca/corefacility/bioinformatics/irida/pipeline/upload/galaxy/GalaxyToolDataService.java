package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;

import java.util.List;


/**
 * A service class for dealing with Galaxy Tool Data Tables.
 *
 *
 */
public class GalaxyToolDataService {

    private static final Logger logger = LoggerFactory
            .getLogger(GalaxyToolDataService.class);

    private ToolDataClient toolDataClient;


    /**
     * Builds a new GalaxyToolDataService with the given ToolDataClient.
     *
     * @param toolDataClient
     *            The ToolDataClient used to interact with Galaxy Tool Data Tables.
     */
    public GalaxyToolDataService(ToolDataClient toolDataClient) {
        checkNotNull(toolDataClient, "toolDataClient is null");
        this.toolDataClient = toolDataClient;
    }

    /**
     * Gets details about a given tool data table.
     *
     * @param dataTableId
     *            The id of the tool data table.
     * @return  The Tool Data Table Object.
     * @throws GalaxyDatasetNotFoundException
     *             If there was an issue getting the details of the tool data table.
     */
    public TabularToolDataTable getToolDataTable(String dataTableId) throws GalaxyDatasetNotFoundException {
        checkNotNull(dataTableId, "dataTableId is null");
        checkNotNull(toolDataClient, "toolDataClient is null");
        TabularToolDataTable toolDataTable = toolDataClient.showDataTable(dataTableId);
        if (toolDataTable != null) {
            return toolDataTable;
        } else {
            throw new GalaxyDatasetNotFoundException("Could not find Tool Data Table named: " + dataTableId);
        }
    }

    /**
     * Given a TabularToolDataTable and a workflowInputLabel find the corresponding id for this input.
     * @param toolDataTable  The TabularToolDataTable to look in.
     * @param value  Tool Data Table 'value' entry (acts as unique key for the table).
     * @param column  Tool Data Table column to select data from.
     * @return  The entry in the Tool Data Table corresponding to that value/column pair.
     * @throws GalaxyDatasetNotFoundException  If no such data could be found.
     */
    public String getToolDataField(TabularToolDataTable toolDataTable, String column, String value) throws GalaxyDatasetNotFoundException {
        checkNotNull(toolDataTable, "toolDataTable is null");
        checkNotNull(column, "column is null");
        checkNotNull(value, "value is null");
        String field = toolDataTable.getField(column, value);
        if (field != null) {
            return field;
        } else {
            throw new GalaxyDatasetNotFoundException("Cannot find data for value " + value + " in column " + column);
        }
    }

    /**
     * Given a TabularToolDataTable and a workflowInputLabel find the corresponding id for this input.
     * @param toolDataTable  The TabularToolDataTable to look in.
     * @param column  Tool Data Table column to select data from.
     * @return  A list of fields in the Tool Data Table corresponding to that column.
     * @throws GalaxyDatasetNotFoundException  If no such column could be found in the table.
     */
    public List<String> getToolDataColumn(TabularToolDataTable toolDataTable, String column) throws GalaxyDatasetNotFoundException {
        checkNotNull(toolDataTable, "toolDataTable is null");
        checkNotNull(column, "column is null");
        List<String> fields = toolDataTable.getFieldsForColumn(column);
        if (fields != null) {
            return fields;
        } else {
            throw new GalaxyDatasetNotFoundException("Cannot find data for column: " + column + " in tool data table " + toolDataTable.getName());
        }
    }

}
