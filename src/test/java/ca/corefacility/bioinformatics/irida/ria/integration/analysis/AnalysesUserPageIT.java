package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysesUserPageIT extends AbstractIridaUIITChromeDriver {

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPageSetUp() {
		AnalysesUserPage page = AnalysesUserPage.initializePage(driver());
		assertEquals(10, page.getNumberOfAnalysesDisplayed(), "Should have 10 analyses displayed originally");

		// Test the name filter
		page.searchForAnalysisByName("My Fake Submission");
		assertEquals(1, page.getNumberOfAnalysesDisplayed(), "Should have 1 Analysis displayed after filtering");
		page.clearNameFilter();
		assertEquals(10, page.getNumberOfAnalysesDisplayed(), "Should have 10 analyses displayed originally");
	}
}
