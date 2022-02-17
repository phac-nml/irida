package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyToolDataTableException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;
import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.sun.jersey.api.client.ClientHandlerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for accessing Galaxy Tool Data Tables.
 *
 */
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GalaxyToolDataServiceIT {

	@Autowired
	private LocalGalaxy localGalaxy;
	private GalaxyInstance galaxyInstanceAdmin;
	private GalaxyToolDataService galaxyToolDataService;

	private static final String INVALID_TOOL_DATA_TABLE_ID = "";
	private static final String VALID_TOOL_DATA_TABLE_ID = "igv_broad_genomes";

	/**
	 * Sets up for tool data table tests.
	 */
	@BeforeEach
	public void setup() {
		galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		ToolDataClient toolDataClient = galaxyInstanceAdmin.getToolDataClient();
		galaxyToolDataService = new GalaxyToolDataService(toolDataClient);

	}

	/**
	 * Tests getting a Galaxy Tool Data Table from the GalaxyToolDataService.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetToolDataTableValid() throws Exception {
		TabularToolDataTable toolDataTable;
		toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
		assertNotNull(toolDataTable);
		assertEquals(VALID_TOOL_DATA_TABLE_ID, toolDataTable.getName());
	}

	/**
	 * Tests failing to find a Galaxy Tool Data Table.
	 * 
	 * @throws GalaxyToolDataTableException
	 */
	@Test
	public void testGetToolDataInvalid() throws GalaxyToolDataTableException {
		assertThrows(ClientHandlerException.class, () -> {
			galaxyToolDataService.getToolDataTable(INVALID_TOOL_DATA_TABLE_ID);
		});
	}
}
