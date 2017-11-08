package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;

import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;

/**
 * Unit tests for the GalaxyToolDataManager.
 *
 */
public class GalaxyToolDataServiceTest {

    @Mock private ToolDataClient toolDataClient;
    @Mock private TabularToolDataTable toolDataTable;

    private GalaxyToolDataService galaxyToolDataService;

    private static final String INVALID_TOOL_DATA_TABLE_ID = "";
    private static final String VALID_TOOL_DATA_TABLE_ID = "igv_broad_genomes";
    private static final String VALID_TOOL_DATA_VALUE = "hg38";
    private static final String VALID_TOOL_DATA_COLUMN = "url";

    /**
     * Sets up variables for workflow tests.
     * @throws URISyntaxException
     */
    @Before
    public void setup() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);

        galaxyToolDataService = new GalaxyToolDataService(toolDataClient);

        String toolDataTableId = "igv_broad_genomes";
        TabularToolDataTable toolDataTable = new TabularToolDataTable();
        toolDataTables = new HashMap<String, TabularToolDataTable>();
        toolDataTables.put(toolDataTableId, toolDataTable);

        when(toolDataClient.showToolData(VALID_TOOL_DATA_TABLE_ID)).thenReturn(toolDataTable);

    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test
    public void testGetToolDataValid() throws WorkflowException {
        TabularToolDataTable toolDataTable = toolDataClient.showDataTable(VALID_TOOL_DATA_TABLE_ID);
        assertEquals("http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg38.genome",
                galaxyToolDataService.getToolData(toolDataTable, VALID_TOOL_DATA_VALUE, VALID_TOOL_DATA_COLUMN));
    }

    /**
     * Tests failing to find a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test(expected=WorkflowException.class)
    public void testGetToolDataInvalid() throws WorkflowException {
        TabularToolDataTable toolDataTable = toolDataClient.showDataTable(INVALID_TOOL_DATA_TABLE_ID);
    }
}
