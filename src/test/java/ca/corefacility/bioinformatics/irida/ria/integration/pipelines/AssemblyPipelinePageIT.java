package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesAssemblyPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesSelectionPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

/**
 * <p>
 * Testing for launching a phylogenomics pipeline.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/PipelineAssemblyView.xml")
public class AssemblyPipelinePageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AssemblyPipelinePageIT.class);
	private PipelinesAssemblyPage page;

	@Before
	public void setUpTest() {
		page = new PipelinesAssemblyPage(driver());
	}

	@Test
	public void testPageSetup() {
		addSamplesToCartManager();

		logger.info("Checking Assembly Page Setup.");
		assertEquals("Should display the correct number of samples.", 1, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPipelineSubmission() {
		addSamplesToCartManager();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed when the pipeline is submitted",
				page.isPipelineSubmittedMessageShown());
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
	}

	@Test
	public void testCheckPipelineStatusAfterSubmit() {
		addSamplesToCartManager();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
		page.clickSeePipeline();

		assertTrue("Should be on analysis page", driver().getCurrentUrl().endsWith("/analysis"));
	}

	@Test
	public void testShareResultsWithSamples() {
		addSamplesToCartManager();

		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testShareResultsWithSamples";
		page.setNameForAnalysisPipeline(analysisName);

		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
		page.clickShareResultsWithSamples();
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());

		// verify that the appropriate flag gets set for sharing results with samples
		asAdmin().expect()
				.body("resource.resources.find{it.name == '" + analysisName + "_sample1'}.updateSamples", equalTo(true))
				.when().get("/api/analysisSubmissions");
	}

	@Test
	public void testNoShareResultsWithSamples() {
		addSamplesToCartManager();

		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testNoShareResultsWithSamples";
		page.setNameForAnalysisPipeline(analysisName);

		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());

		asAdmin().expect().body("resource.resources.find{it.name == '" + analysisName + "_sample1'}.updateSamples",
				equalTo(false)).when().get("/api/analysisSubmissions");
	}

	@Test
	public void testUserNoShareResultsWithSamples() {
		addSamplesToCartUser();

		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testUserNoShareResultsWithSamples";
		page.setNameForAnalysisPipeline(analysisName);

		assertFalse("Share Results with Samples checkbox should not exist", page.existsShareResultsWithSamples());
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());

		asAdmin().expect().body("resource.resources.find{it.name == '" + analysisName + "_sample1'}.updateSamples",
				equalTo(false)).when().get("/api/analysisSubmissions");
	}

	private void addSamplesToCartUser() {
		LoginPage.loginAsUser(driver());
		addSamplesToCart();
	}

	private void addSamplesToCartManager() {
		LoginPage.loginAsManager(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.addSelectedSamplesToCart();
		PipelinesSelectionPage.goToAssemblyPipelinePipeline(driver());
	}
}
