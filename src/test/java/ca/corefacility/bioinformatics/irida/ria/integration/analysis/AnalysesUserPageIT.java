package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysesUserPageIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPageSetUp() {
		AnalysesUserPage page = AnalysesUserPage.initializePage(driver());
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());
		page.filterByName("submission");
		assertEquals("Should be 6 analyses displayed on the page after filtering by name", 6,
				page.getNumberOfAnalyses());
		assertEquals("Should be 2 download buttons displayed, one for each completed analysis", 2, page.getNumberOfDownloadBtns());

		assertEquals("Should display progress bars with percent complete for everything except error state", 6,
				page.getNumberOfProgressBars());
		assertEquals("Should display 90% complete", "90%", page.getPercentComplete(1));
		assertEquals("Should display 100% complete", "5%", page.getPercentComplete(2));
	}

	@Test
	public void testAdvancedFilters() {
		AnalysesUserPage page = AnalysesUserPage.initializePage(driver());
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());

		page.filterByState("New");
		assertEquals("Should be 1 analysis in the state of 'NEW'", 1, page.getNumberOfAnalyses());

		page.filterByState("Completed");
		assertEquals("Should be 2 analysis in the state of 'COMPLETED'", 2, page.getNumberOfAnalyses());

		page.filterByState("Prepared");
		assertTrue("Should display a message that there are no analyses available", page.isNoAnalysesMessageDisplayed());

		// Clear
		page.clearFilter();
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());

		page.filterByDateEarly("06 Nov 2013");
		assertEquals("Should be 3 analyses after filtering by date earliest", 3, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();

		page.filterByDateLate("06 Jan 2014");
		assertEquals("Should be 7 analyses after filtering by date earliest", 7, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();
		page.filterByType("Phylogenomics Pipeline");
		assertEquals("Should be 6 analyses aftering filtering by type", 6, page.getNumberOfAnalyses());
	}
}
