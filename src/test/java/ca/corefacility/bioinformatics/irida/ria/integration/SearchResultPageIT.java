package ca.corefacility.bioinformatics.irida.ria.integration;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.SearchResultPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for global search
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class SearchResultPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testGlobalSearchAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		SearchResultPage searchResultPage = new SearchResultPage(driver());
		final String query = "samp";
		searchResultPage.enterSearchQueryInNavBar(query);
		assertTrue(driver().getCurrentUrl().contains("search?query=" + query));
	}

//	@Test
//	public void testSampleSearch() {
//		LoginPage.loginAsUser(driver());
//		page = SearchResultPage.initPage(driver());
//
//		page.globalSearch("samp", false);
//		page = SearchResultPage.initPage(driver());
//		checkTranslations(page, ImmutableList.of("search"), null);
//
//		page.waitForSearchResults();
//		int sampleCount = page.getSampleCount();
//		int projectCount = page.getProjectCount();
//
//		assertEquals(2, sampleCount, "should be 2 samples");
//		assertEquals(0, projectCount, "should be no projects");
//
//	}
//
//	@Test
//	public void testProjectSearch() {
//		LoginPage.loginAsUser(driver());
//		page = SearchResultPage.initPage(driver());
//
//		page.globalSearch("project2", false);
//		page = SearchResultPage.initPage(driver());
//
//		page.waitForSearchResults();
//		int sampleCount = page.getSampleCount();
//		int projectCount = page.getProjectCount();
//
//		assertEquals(0, sampleCount, "should be no samples");
//		assertEquals(1, projectCount, "should be 1 project");
//
//		page.globalSearch("ABCD", false);
//		page = SearchResultPage.initPage(driver());
//
//		page.waitForSearchResults();
//		sampleCount = page.getSampleCount();
//		projectCount = page.getProjectCount();
//
//		assertEquals(0, sampleCount, "should be no samples");
//		assertEquals(0, projectCount, "should be no projects");
//	}
}
