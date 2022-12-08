package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.SearchResultPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for global search
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class SearchResultPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testGlobalSearchAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		SearchResultPage searchResultPage = new SearchResultPage(driver());
		final String query = "2";
		searchResultPage.enterSearchQueryInNavBar(query);
		assertTrue(driver().getCurrentUrl().contains("search?query=" + query));
		assertEquals(query, searchResultPage.getSearchInputQuery());
		int numRows = searchResultPage.getTotalNumberOfProjectsInTable();
		assertEquals(2, numRows, "Expected number of projects");
		assertEquals(numRows, searchResultPage.getTotalNumberOfProjectsByBadge());
		assertEquals(1, searchResultPage.getTotalNumberOfSamplesByBadge(), "Expected total number of samples");

		assertTrue(searchResultPage.isAdminSearchTypeDisplayed(), "Admins should have the ability to switch between global and personal projects");
		searchResultPage.selectAdminPersonalProject();
		assertEquals(0, searchResultPage.getTotalNumberOfProjectsByBadge(), "Admin has no projects containing the search term " + query);
		assertEquals(0, searchResultPage.getTotalNumberOfSamplesByBadge(), "Admin has no samples containing the search term " + query);

		String newQuery = "1";
		searchResultPage.enterNewSearchQuery(newQuery);
		assertEquals(1, searchResultPage.getTotalNumberOfProjectsByBadge(), "Admin has 1 projects containing the search term " + newQuery);
		assertEquals(1, searchResultPage.getTotalNumberOfSamplesByBadge(), "Admin has 1 samples containing the search term " + newQuery);
	}

	@Test
	public void testGlobalSearchAsUser() {
		LoginPage.loginAsUser(driver());
		SearchResultPage searchResultPage = new SearchResultPage(driver());
		final String query = "2";
		searchResultPage.enterSearchQueryInNavBar(query);
		assertTrue(driver().getCurrentUrl().contains("search?query=" + query));
		assertEquals(query, searchResultPage.getSearchInputQuery());
		int numRows = searchResultPage.getTotalNumberOfProjectsInTable();
		assertEquals(1, numRows, "Expected number of projects");
		assertEquals(numRows, searchResultPage.getTotalNumberOfProjectsByBadge());
		assertEquals(1, searchResultPage.getTotalNumberOfSamplesByBadge(), "Expected total number of samples");
		assertFalse(searchResultPage.isAdminSearchTypeDisplayed(), "Users should not have the ability to switch between global and personal projects");

	}
}
