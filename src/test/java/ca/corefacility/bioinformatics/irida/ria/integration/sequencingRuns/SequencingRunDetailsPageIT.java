package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequencingRunDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunDetailsPage page;

	@Override
	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		page = new SequencingRunDetailsPage(driver());
		page.getDetailsPage(1L);
	}

	@Test
	public void testGetDetails() {
		Map<String, String> runDetails = page.getRunDetails();
		assertEquals("A cool run", runDetails.get("Description"), "Description should exist.");
		assertEquals("test workflow", runDetails.get("Workflow"), "Workflow should exist.");
	}

	@Test
	public void testGetFilesCount() {
		assertEquals(3, page.getTableSize(), "File table should be populated.");
	}

	@Test
	public void testGetFile() {
		assertTrue(page.rowExists("FileThatMayNotExist1"), "File should exist.");
	}

}
