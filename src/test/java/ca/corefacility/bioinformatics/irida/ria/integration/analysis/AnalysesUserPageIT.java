package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

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
		assertEquals("Should have 8 analyses displayed originally", 8, page.getNumberOfAnalysesDisplayed());

		// Test the name filter
		page.searchForAnalysisByName("My Fake Submission");
		assertEquals("Should have 1 Analysis displayed after filtering", 1, page.getNumberOfAnalysesDisplayed());
		page.clearNameFilter();
		assertEquals("Should have 8 analyses displayed originally", 8, page.getNumberOfAnalysesDisplayed());
	}
}
