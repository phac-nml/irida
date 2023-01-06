package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.LaunchPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsProcessingPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the project settings processing page
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSettingsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testCreateAutomatedPipeline() {
		Long projectId = 1L;

		LoginPage.loginAsAdmin(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals(1, processingPage.countAutomatedAnalyses(), "should be 1 automated analyses");

		assertTrue(processingPage.isCreateAnalysisButtonVisible(), "create analysis button should be visible");
		processingPage.clickCreateAnalysis();

		processingPage.selectAutomatedTemplateByIndex(0);

		LaunchPipelinePage page = LaunchPipelinePage.init(driver());
		assertEquals("Assembly and Annotation Pipeline", page.getPipelineName(), "Should be on the Assembly Pipeline");
	}

	@Test
	public void testRemoveAutomatedPipeline() {
		Long projectId = 1L;

		LoginPage.loginAsAdmin(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);
		assertEquals(1, processingPage.countAutomatedAnalyses(), "should be 1 automated analyses");

		processingPage.removeFirstAnalysis();

		processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals(0, processingPage.countAutomatedAnalyses(), "should be no automated analyses");

	}

	@Test
	public void testProcessingPageAsRegularUser() {
		Long projectId = 1L;
		LoginPage.loginAsUser(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);
		assertEquals(1, processingPage.countAutomatedAnalyses(), "should be 1 automated analyses");
		assertFalse(processingPage.isCreateAnalysisButtonVisible(), "create analysis button should NOT be visible");
	}

}
