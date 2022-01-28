package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunsListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunDetailsPage page;

	@Override
	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequencingRunDetailsPage(driver());
		page.getDetailsPage(1L);
	}

	@Test
	public void testGetDetails() {
		Map<String, String> runDetails = page.getRunDetails();
		assertEquals(runDetails.get("Description"), "A cool run");
		assertEquals(runDetails.get("Workflow"), "test workflow");
	}

	@Test
	public void testGetSequencerType() {
		String sequencerType = page.getSequencerType();
		assertEquals(sequencerType, "miseq");
	}

	/**
	 * TODO: This should be deleted after merging analysis branch.
	 */
	@Test
	public void testDeleteRun() {
		page.deleteRun();
		SequencingRunsListPage listPage = SequencingRunsListPage.goToPage(driver());
		assertFalse(listPage.idDisplayIdInList("1"), "run should have been deleted");
	}
}
