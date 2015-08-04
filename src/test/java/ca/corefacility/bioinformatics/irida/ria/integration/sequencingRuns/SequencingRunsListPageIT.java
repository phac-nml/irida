package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunsListPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Ordering;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunsListPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunsListPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequencingRunsListPage(driver());
		page.goTo();
	}

	@Test
	public void testList() {
		List<Long> displayedIds = page.getDisplayedIds();
		assertEquals(2, displayedIds.size());

		assertTrue("Should be ordered newest to oldest", Ordering.natural().reverse().isOrdered(displayedIds));
	}

}
