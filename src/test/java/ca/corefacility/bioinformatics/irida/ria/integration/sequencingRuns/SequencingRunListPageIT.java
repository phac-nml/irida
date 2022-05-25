package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequencingRunListPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunListPage page;

	@Override
	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		page = new SequencingRunListPage(driver());
		page.goToPage();
	}

	@Test
	public void testGetRunsCount() {
		assertEquals(2, page.getTableSize(), "Run table should be populated.");
	}

	@Test
	public void testDeleteRun() {
		String runId = "1";
		page.deleteRun(runId);
		assertFalse(page.rowExists(runId), "Run should have been deleted.");
	}

}
