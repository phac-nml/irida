package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.BasicPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsProcessingPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSettingsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testCreateAutomatedPipeline() {
		Long projectId = 1L;

		LoginPage.loginAsAdmin(driver());
		ProjectSettingsProcessingPage processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals("should be no automated analyses", 0, processingPage.countAutomatedAnalyses());
		processingPage.clickCreateAnalysis();

		CartPage cartPage = CartPage.initPage(driver());
		assertTrue("Should be able to see pipelines for auto analysis", cartPage.onPipelinesView());
		cartPage.selectFirstPipeline();

		BasicPipelinePage pipelinePage = new BasicPipelinePage(driver());
		pipelinePage.clickLaunchPipelineBtn();

		assertTrue("Pipeline should say it's been created", pipelinePage.isPipelineSubmittedMessageShown());

		processingPage = ProjectSettingsProcessingPage.goToPage(driver(), projectId);

		assertEquals("should be 1 automated analysis", 1, processingPage.countAutomatedAnalyses());

	}

}
