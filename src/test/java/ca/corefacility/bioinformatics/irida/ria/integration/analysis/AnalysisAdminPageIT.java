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
		assertEquals("Should have 9 analyses displayed originally", 9, page.getNumberOfAnalysesDisplayed());

		// Test the name filter
		page.searchForAnalysisByName("My Fake Submission");
		assertEquals("Should have 1 Analysis displayed after filtering", 1, page.getNumberOfAnalysesDisplayed());
		page.clearNameFilter();
		assertEquals("Should have 9 analyses displayed originally", 9, page.getNumberOfAnalysesDisplayed());

		/*
		Test deleting an analysis
		Need to start at an offset of 9
		 - elements 0 - 8 are not displayed on the screen.  This table uses ant.design table with an always visible column for the buttons.
		 9 - 17 are the actual element displayed within the overlay of the fixed column.
		 */
		page.deleteAnalysis(9);
		assertEquals("Should have 8 analyses displayed after deleting one", 8, page.getNumberOfAnalysesDisplayed());

		// Check to make sure the analyses queue is being set up properly
		AnalysesQueue queue = AnalysesQueue.getAnalysesQueue(driver());
		assertEquals("Should have 5 analyses running", 5, queue.getRunningCounts());
		assertEquals("Should have 1 analysis queued", 1, queue.getQueueCounts());
	}
}
