package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectAnalysesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectAnalysesPage projectAnalysesPage;

	@Test
	public void testGetProjectAnalyses() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals("Should have 4 analyses displayed", 4, projectAnalysesPage.getNumberOfAnalysesDisplayed());

		// Test the name filter
		projectAnalysesPage.searchForAnalysisByName("My Pretend Submission");
		assertEquals("Should have 1 Analysis displayed after filtering", 1, projectAnalysesPage.getNumberOfAnalysesDisplayed());
		projectAnalysesPage.clearNameFilter();
		assertEquals("Should have 4 analyses displayed", 4, projectAnalysesPage.getNumberOfAnalysesDisplayed());

		// Test deleting an analysis
		projectAnalysesPage.deleteAnalysis(1);
		assertEquals("Should only be 1 analysis remaining after deletion", 3,
				projectAnalysesPage.getNumberOfAnalysesDisplayed());
	}

	@Test
	public void testGetSharedSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesSharedSingleSampleAnalysisOutputsPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals("Should have 2 shared single sample analysis outputs displayed", 2, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		projectAnalysesPage.searchOutputs("sistr");
		assertEquals("Should have 1 shared single sample analysis outputs displayed after filtering", 1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		projectAnalysesPage.clearSearchOutputs();
		assertEquals("Should have 2 shared single sample analysis outputs displayed after removing filtering", 2, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());

	}

	@Test
	public void testGetAutomatedSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesAutomatedSingleSampleAnalysisOutputsPage(driver(), 1);

		checkTranslations(projectAnalysesPage, ImmutableList.of("project-analyses"), null);
		assertEquals("Should have 1 automated single sample analysis outputs displayed", 1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		projectAnalysesPage.searchOutputs("sistr");
		assertEquals("Should have 0 automated single sample analysis outputs displayed after filtering", 0, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		projectAnalysesPage.clearSearchOutputs();
		assertEquals("Should have 1 automated single sample analysis outputs displayed after removing filtering", 1, projectAnalysesPage.getNumberSingleSampleAnalysisOutputsDisplayed());
	}
}
