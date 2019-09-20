package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunsListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunDetailsPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequencingRunDetailsPage(driver());
		page.getDetailsPage(1L);
	}

	@Test
	public void testGetDetails() {
		Map<String, String> runDetails = page.getRunDetails();
		assertEquals("A cool run", runDetails.get("Description"));
		assertEquals("test workflow", runDetails.get("Workflow"));
	}

	@Test
	public void testGetSequencerType() {
		String sequencerType = page.getSequencerType();
		assertEquals("MiSeq", sequencerType);
	}
	
	@Test
	public void testDeleteRun(){
		page.deleteRun();
		SequencingRunsListPage listPage = SequencingRunsListPage.goToPage(driver());
		assertFalse("run should have been deleted", listPage.idDisplayIdInList("1"));
	}
}
