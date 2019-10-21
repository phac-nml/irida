package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import java.util.List;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunsListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Ordering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunsListPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testList() {
		LoginPage.loginAsManager(driver());
		SequencingRunsListPage page = SequencingRunsListPage.goToPage(driver());
		List<Long> displayedIds = page.getDisplayedIds();
		assertEquals(2, displayedIds.size());

		assertTrue("Should be ordered newest to oldest", Ordering.natural().reverse().isOrdered(displayedIds));
	}

}
