package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisAdminPageIT extends AbstractIridaUIIT {

	@Override
	public WebDriver driverToUse() {
		return new ChromeDriver();
	}

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPageSetup() {
		AnalysesUserPage userPage = AnalysesUserPage.initializePage(driver());
		assertEquals("Admin has not personal analysis", 8, userPage.getNumberOfAnalyses());

		AnalysesUserPage adminPage = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should be 8 analyses displayed on the page", 9, adminPage.getNumberOfAnalyses());
	}

	@Test
	public void testAdvancedFilters() {
		AnalysesUserPage page = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should be 9 analyses displayed on the page", 9, page.getNumberOfAnalyses());

		page.filterByState("New");
		assertEquals("Should be 1 analysis in the state of 'NEW'", 1, page.getNumberOfAnalyses());

		page.filterByState("Completed");
		assertEquals("Should be 2 analysis in the state of 'COMPLETED'", 2, page.getNumberOfAnalyses());

		page.filterByState("Prepared");
		assertTrue("Should display a message that there are no analyses available", page.isNoAnalysesMessageDisplayed());

		// Clear
		page.clearFilter();
		assertEquals("Should be 9 analyses displayed on the page", 9, page.getNumberOfAnalyses());

		page.filterByDateEarly("06 Nov 2013");
		assertEquals("Should be 3 analyses after filtering by date earliest", 3, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();
		page.filterBySubmitter("test");
		assertEquals("Should only be one submission send by 'test' user", 1, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();

		page.filterByDateLate("06 Jan 2014");
		assertEquals("Should be 8 analyses after filtering by date earliest", 8, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();
		page.filterByType("Phylogenomics Pipeline");
		assertEquals("Should be 7 analyses aftering filtering by type", 7, page.getNumberOfAnalyses());
	}
}
