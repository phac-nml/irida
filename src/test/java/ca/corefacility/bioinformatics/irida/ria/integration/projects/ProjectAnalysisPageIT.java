package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectAnalysesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectAnalysesPage projectAnalysesPage;

	@Test
	public void testGetProjectAnalyses() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesPage(driver(), 1);

		assertEquals("Should have 2 analyses displayed", 2, projectAnalysesPage.getNumberOfAnalysesDisplayed());

		// Test the name filter
		projectAnalysesPage.searchForAnalysisByName("My Pretend Submission");
		assertEquals("Should have 1 Analysis displayed after filtering", 1, projectAnalysesPage.getNumberOfAnalysesDisplayed());
		projectAnalysesPage.clearNameFilter();
		assertEquals("Should have 2 analyses displayed", 2, projectAnalysesPage.getNumberOfAnalysesDisplayed());

		// Test deleting an analysis
		projectAnalysesPage.deleteAnalysis(0);
		assertEquals("Should only be 1 analysis remaining after deletion", 1,
				projectAnalysesPage.getNumberOfAnalysesDisplayed());
	}
}
