package ca.corefacility.bioinformatics.irida.ria.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.SearchResultPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class SearchResultPageTest extends AbstractIridaUIITChromeDriver {
	private SearchResultPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsUser(driver());

		page = SearchResultPage.initPage(driver());
	}

	@Test
	public void searchSamples() {
		page.globalSearch("samp");
		page = SearchResultPage.initPage(driver());

		page.waitForSearchResults();
		int sampleCount = page.getSampleCount();
		int projectCount = page.getProjectCount();

		assertEquals("should be 1 sample", 1, sampleCount);
		assertEquals("should be 1 no projects", 0, projectCount);
	}
}
