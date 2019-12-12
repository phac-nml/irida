package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.SearchResultPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;

/**
 * Test class for global search
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class SearchResultPageIT extends AbstractIridaUIITChromeDriver {
	private SearchResultPage page;

	@Test
	public void testSampleSearch() {
		LoginPage.loginAsUser(driver());
		page = SearchResultPage.initPage(driver());

		page.globalSearch("samp", false);
		page = SearchResultPage.initPage(driver());
		checkTranslations(page, ImmutableList.of("search"), null);

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be 2 samples", 2, sampleCount);
		assertEquals("should be no projects", 0, projectCount);

	}

	@Test
	public void testProjectSearch() {
		LoginPage.loginAsUser(driver());
		page = SearchResultPage.initPage(driver());

		page.globalSearch("project2", false);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be no samples", 0, sampleCount);
		assertEquals("should be 1 project", 1, projectCount);

		page.globalSearch("ABCD", false);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		sampleCount = page.getSampleCount();
		projectCount = page.getProjectCount();

		assertEquals("should be no samples", 0, sampleCount);
		assertEquals("should be no projects", 0, projectCount);
	}

	@Test
	public void testAdminSampleSearch() {
		LoginPage.loginAsAdmin(driver());
		page = SearchResultPage.initPage(driver());

		page.globalSearch("samp", true);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be 2 samples", 2, sampleCount);
		assertEquals("should be no projects", 0, projectCount);
	}

	@Test
	public void testAdminSampleSearchById() {
		LoginPage.loginAsAdmin(driver());
		page = SearchResultPage.initPage(driver());

		page.globalSearch("1", true);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be 2 samples", 2, sampleCount);
	}

	@Test
	public void testAdminProjectSearch() {
		LoginPage.loginAsAdmin(driver());
		page = SearchResultPage.initPage(driver());

		page.globalSearch("project2", true);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be no samples", 0, sampleCount);
		assertEquals("should be 1 project", 1, projectCount);

		page.globalSearch("ABCD", true);
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		sampleCount = page.getSampleCount();
		projectCount = page.getProjectCount();

		assertEquals("should be no samples", 0, sampleCount);
		assertEquals("should be 1 project", 1, projectCount);
	}

}
