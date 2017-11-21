package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;

import com.github.jmchilton.blend4j.galaxy.ToolDataClient;
import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;

/**
 * Unit tests for the GalaxyToolDataManager.
 *
 */
public class GalaxyToolDataServiceTest {

    @Mock private ToolDataClient toolDataClient;

    private GalaxyToolDataService galaxyToolDataService;

    private static final String INVALID_TOOL_DATA_TABLE_ID = "";
    private static final String VALID_TOOL_DATA_TABLE_ID = "igv_broad_genomes";
    private static final String VALID_TOOL_DATA_VALUE = "hg38";
    private static final String VALID_TOOL_DATA_COLUMN = "url";
    private static final List<String> VALID_TOOL_DATA_COLUMN_FIELDS = new ArrayList<>(
            Arrays.asList(
                    "",
                    ""
            )
    );


    /**
     * Sets up variables for workflow tests.
     * @throws URISyntaxException
     */
    @Before
    public void setup() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);

        galaxyToolDataService = new GalaxyToolDataService(toolDataClient);
    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test
    public void testGetToolDataTableValid() throws WorkflowException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (WorkflowException e) {
            throw e;
        }
        assertNotNull(toolDataTable);
        assertEquals(VALID_TOOL_DATA_TABLE_ID, toolDataTable.getName());
    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test
    public void testGetToolDataFieldValid() throws WorkflowException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (WorkflowException e) {
            throw e;
        }
        assertEquals("http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg38.genome",
                galaxyToolDataService.getToolDataField(toolDataTable, VALID_TOOL_DATA_VALUE, VALID_TOOL_DATA_COLUMN));
    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test
    public void testGetToolDataColumnValid() throws WorkflowException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (WorkflowException e) {
            throw e;
        }
        assertEquals(VALID_TOOL_DATA_COLUMN_FIELDS,
                galaxyToolDataService.getToolDataColumn(toolDataTable, VALID_TOOL_DATA_COLUMN));
    }

    /**
     * Tests failing to find a valid workflow input id from a workflow details.
     * @throws WorkflowException
     */
    @Test(expected=WorkflowException.class)
    public void testGetToolDataInvalid() throws WorkflowException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(INVALID_TOOL_DATA_TABLE_ID);
        } catch (WorkflowException e) {
            throw e;
        }
    }
}
