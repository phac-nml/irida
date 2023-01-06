package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectAnalysesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectAnalysesPage projectAnalysesPage;

	@Test
	public void testGetProjectAnalyses() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals(4, projectAnalysesPage.getNumberOfAnalysesDisplayed(), "Should have 4 analyses displayed");

		// Test the name filter
		projectAnalysesPage.searchForAnalysisByName("My Pretend Submission");
		assertEquals(1, projectAnalysesPage.getNumberOfAnalysesDisplayed(), "Should have 1 Analysis displayed after filtering");
		projectAnalysesPage.clearNameFilter();
		assertEquals(4, projectAnalysesPage.getNumberOfAnalysesDisplayed(), "Should have 4 analyses displayed");

		// Test deleting an analysis
		projectAnalysesPage.deleteAnalysis(1);
		assertEquals(3, projectAnalysesPage.getNumberOfAnalysesDisplayed(),
				"Should only be 3 analysis remaining after deletion");
	}

	@Test
	public void testGetSharedSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesSharedSingleSampleAnalysisOutputsPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals(2, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 2 shared single sample analysis outputs displayed");

		projectAnalysesPage.searchOutputs("sistr");
		assertEquals(1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 1 shared single sample analysis outputs displayed after filtering");

		projectAnalysesPage.clearSearchOutputs();
		assertEquals(2, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 2 shared single sample analysis outputs displayed after removing filtering");

	}

	@Test
	public void testGetAutomatedSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesAutomatedSingleSampleAnalysisOutputsPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals(1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 1 automated single sample analysis outputs displayed");

		projectAnalysesPage.searchOutputs("sistr");
		assertEquals(0, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 0 automated single sample analysis outputs displayed after filtering");

		projectAnalysesPage.clearSearchOutputs();
		assertEquals(1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 1 automated single sample analysis outputs displayed after removing filtering");
	}
}
