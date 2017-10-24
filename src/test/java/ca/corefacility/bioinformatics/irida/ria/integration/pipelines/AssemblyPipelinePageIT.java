package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesAssemblyPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesPhylogenomicsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesSelectionPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectEditPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

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
		addSamplesToCart();

		logger.info("Checking Assembly Page Setup.");
		assertEquals("Should display the correct number of samples.", 2, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPipelineSubmission() {
		addSamplesToCart();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed when the pipeline is submitted", page.isPipelineSubmittedMessageShown());
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
	}

	@Test
	public void testCheckPipelineStatusAfterSubmit() {
		addSamplesToCart();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
		page.clickSeePipeline();

		assertTrue("Should be on analysis page", driver().getCurrentUrl().endsWith("/analysis"));
	}
	
	@Test
	public void testShareResultsWithSamples() {
		addSamplesToCart();

//		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
		page.clickShareResultsWithSamples();
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
	}
	
	@Test
	public void testNoShareResultsWithSamples() {
		addSamplesToCart();

		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
	}

	private void addSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(1);
		samplesPage.addSelectedSamplesToCart();
		PipelinesSelectionPage.goToAssemblyPipelinePipeline(driver());
	}
}
