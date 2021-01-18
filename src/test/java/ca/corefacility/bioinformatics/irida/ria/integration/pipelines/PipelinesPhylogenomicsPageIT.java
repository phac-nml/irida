package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.LaunchPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * <p>
 * Testing for launching a phylogenomics pipeline.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/PipelinePhylogenomicsView.xml")
public class PipelinesPhylogenomicsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(PipelinesPhylogenomicsPageIT.class);
	private CartPage cartPage;

	private LaunchPipelinePage page;

	@Before
	public void setUpTest() throws IOException {
		cartPage = new CartPage(driver());

		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	@Test
	public void testPageSetup() {
		assertEquals("Launch Page should display  the pipeline name", "SNVPhyl Phylogenomics Pipeline", page.getPipelineName());
		assertTrue("Launch form should be displayed", page.isLaunchFormDisplayed());
		assertTrue("Launch details should be displayed", page.isLaunchDetailsDisplayed());
		assertTrue("Launch parameters should be displayed", page.isLaunchParametersDisplayed());
		assertFalse("Share with samples should not be displayed", page.isShareWithSamplesDisplayed());
		assertTrue("Share with projects should be displayed", page.isShareWithProjectsDisplayed());
		assertTrue("Should be able to select a reference file", page.isReferenceFilesDisplayed());
		assertTrue("Should be able to select sample files", page.isLaunchFilesDisplayed());

		// Test the name input
		page.clearName();
		assertTrue("There should be an error displayed that a name is required", page.isNameErrorDisplayed());
		page.updateName("TEST_NAME");
		assertFalse("Name required warning should be cleared", page.isNameErrorDisplayed());
	}

	private void addSamplesToCart() {
		LoginPage.loginAsUser(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(1);
		samplesPage.addSelectedSamplesToCart();
		cartPage.selectPhylogenomicsPipeline();
	}
}
