package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.BasicPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsProcessingPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for the project settings processing page
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSettingsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testCreateAutomatedPipeline() {
		Long projectId = 1L;

		LoginPage.loginAsAdmin(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);
		assertEquals("should be 1 automated analyses", 1, processingPage.countAutomatedAnalyses());

		assertTrue("create analysis button should be visible", processingPage.isCreateAnalysisButtonVisible());
		processingPage.clickCreateAnalysis();

		CartPage cartPage = CartPage.initPage(driver());
		assertTrue("Should be able to see pipelines for auto analysis", cartPage.onPipelinesView());
		cartPage.selectFirstPipeline();

		BasicPipelinePage pipelinePage = new BasicPipelinePage(driver());
		pipelinePage.clickLaunchPipelineBtn();

		assertTrue("Pipeline should say it's been created", pipelinePage.isPipelineSubmittedMessageShown());

		processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals("should be 2 automated analysis", 2, processingPage.countAutomatedAnalyses());
	}

	@Test
	public void testRemoveAutomatedPipeline() {
		Long projectId = 1L;

		LoginPage.loginAsAdmin(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);
		assertEquals("should be 1 automated analyses", 1, processingPage.countAutomatedAnalyses());

		processingPage.removeFirstAnalysis();

		processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals("should be no automated analyses", 0, processingPage.countAutomatedAnalyses());

	}

	@Test
	public void testProcessingPageAsRegularUser() {
		Long projectId = 1L;
		LoginPage.loginAsUser(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);
		assertEquals("should be 1 automated analyses", 1, processingPage.countAutomatedAnalyses());

		assertFalse("create analysis button should NOT be visible", processingPage.isCreateAnalysisButtonVisible());
	}

}
