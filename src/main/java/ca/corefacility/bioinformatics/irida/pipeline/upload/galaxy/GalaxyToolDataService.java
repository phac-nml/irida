package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;


/**
 * A service class for dealing with Galaxy libraries.
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
     *            The ToolDataClient used to interact with Galaxy libraries.
     */
    public GalaxyToolDataService(ToolDataClient toolDataClient) {
        checkNotNull(toolDataClient, "toolDataClient is null");

        logger.debug("");

        this.toolDataClient = toolDataClient;

    }

    /**
     * Gets details about a given tool data table.
     *
     * @param dataTableId
     *            The id of the tool data table.
     * @return A details object for this workflow.
     * @throws WorkflowException
     *             If there was an issue getting the details of the tool data table.
     */
    public TabularToolDataTable getToolDataTable(String dataTableId) throws WorkflowException {
        checkNotNull(dataTableId, "dataTableId is null");

        try {
            return toolDataClient.showDataTable(dataTableId);
        } catch (RuntimeException e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * Given a TabularToolDataTable and a workflowInputLabel find the corresponding id for this input.
     * @param toolDataTable  The TabularToolDataTable to look in.
     * @param value  Tool Data Table 'value' entry (acts as unique key for the table).
     * @param column  Tool Data Table column to select data from.
     * @return  The entry in the Tool Data Table corresponding to that value/column pair.
     * @throws WorkflowException  If no such data could be found.
     */
    public String getToolData(TabularToolDataTable toolDataTable, String column, String value) throws WorkflowException {
        checkNotNull(toolDataTable, "toolDataTable is null");
        checkNotNull(column, "column is null");
        checkNotNull(value, "value is null");


        if (true) {
            return null;
        } else {
            throw new WorkflowException("Cannot find data for value " + value + " in column " + column);
        }
    }

}
