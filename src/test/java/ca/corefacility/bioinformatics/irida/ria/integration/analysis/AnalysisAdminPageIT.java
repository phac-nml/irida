package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AnalysesQueue;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the Analysis Listing page when logged in as an administrator.
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisAdminPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testPageSetup() {
		LoginPage.loginAsAdmin(driver());
		AnalysesUserPage page = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals(10, page.getNumberOfAnalysesDisplayed(), "Should have 10 analyses displayed originally");

		// Test the name filter
		page.searchForAnalysisByName("My Really Bad Mistake!");
		assertEquals(1, page.getNumberOfAnalysesDisplayed(), "Should have 1 Analysis displayed after filtering");
		page.clearNameFilter();
		assertEquals(10, page.getNumberOfAnalysesDisplayed(), "Should have 10 analyses displayed originally");

		/*
		Test deleting a analysis
		Need to to start at an offset of 9
		 - elements 0 - 8 are not displayed on the screen.  This table uses ant.design table with an always visible column for the buttons.
		 9 - 17 are the actual element displayed within the overlay of the fixed column.
		 */
		page.deleteAnalysis(9);

		// Still 10 left as there is a total of 13 analyses (10 displayed on each page of table)
		assertEquals(10, page.getNumberOfAnalysesDisplayed(), "Should have 10 analyses displayed after deleting one");

		// Check to make sure the analyses queue is being set up properly
		AnalysesQueue queue = AnalysesQueue.getAnalysesQueue(driver());
		assertEquals(5, queue.getRunningCounts(), "Should have 5 analyses running");
		assertEquals(1, queue.getQueueCounts(), "Should have 1 analysis queued");

		// Test filtering on second page to ensure server side filtering
		page.searchForAnalysisByName("My Fake Submission");
		assertEquals(1, page.getNumberOfAnalysesDisplayed(),
				"Should have 1 Analysis displayed after filtering for item on second page");
	}
}
