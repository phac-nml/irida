package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AnalysesQueue;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

/**
 * Test for the Analysis Listing page when logged in as an administrator.
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisAdminPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testPageSetup() {
		LoginPage.loginAsAdmin(driver());
		AnalysesUserPage page = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should have 10 analyses displayed originally", 10, page.getNumberOfAnalysesDisplayed());

		// Test the name filter
		page.searchForAnalysisByName("My Really Bad Mistake!");
		assertEquals("Should have 1 Analysis displayed after filtering", 1, page.getNumberOfAnalysesDisplayed());
		page.clearNameFilter();
		assertEquals("Should have 10 analyses displayed originally", 10, page.getNumberOfAnalysesDisplayed());

		/*
		Test deleting a analysis
		Need to to start at an offset of 9
		 - elements 0 - 8 are not displayed on the screen.  This table uses ant.design table with an always visible column for the buttons.
		 9 - 17 are the actual element displayed within the overlay of the fixed column.
		 */
		page.deleteAnalysis(9);

		// Still 10 left as there is a total of 13 analyses (10 displayed on each page of table)
		assertEquals("Should have 10 analyses displayed after deleting one", 10, page.getNumberOfAnalysesDisplayed());

		// Check to make sure the analyses queue is being set up properly
		AnalysesQueue queue = AnalysesQueue.getAnalysesQueue(driver());
		assertEquals("Should have 5 analyses running", 5, queue.getRunningCounts());
		assertEquals("Should have 1 analysis queued", 1, queue.getQueueCounts());

		// Test filtering on second page to ensure server side filtering
		page.searchForAnalysisByName("My Fake Submission");
		assertEquals("Should have 1 Analysis displayed after filtering for item on second page", 1,
				page.getNumberOfAnalysesDisplayed());
	}
}
