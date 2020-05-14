package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesPhylogenomicsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Testing for launching a phylogenomics pipeline.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/PipelinePhylogenomicsView.xml")
public class PipelinesPhylogenomicsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(PipelinesPhylogenomicsPageIT.class);
	private PipelinesPhylogenomicsPage page;
	private CartPage cartPage;

	@Before
	public void setUpTest() throws IOException {
		page = new PipelinesPhylogenomicsPage(driver());
		cartPage = new CartPage(driver());
	}

	@Test
	public void testPageSetup() {
		addSamplesToCart();

		logger.info("Checking Phylogenomics Page Setup.");
		assertEquals("Should display the correct number of reference files in the select input.", 2,
				page.getReferenceFileCount());
		assertEquals("Should display the correct number of samples.", 2, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testSubmitWithTransientReferenceFile() throws IOException {
		LoginPage.loginAsUser(driver());

		// Add sample from a project that user is a "Project User" and has no
		// reference files.
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 2);
		samplesPage.selectSample(0);
		samplesPage.addSelectedSamplesToCart();

		cartPage.selectPhylogenomicsPipeline();
		assertTrue("Should display a warning to the user that there are no reference files.",
				page.isNoReferenceWarningDisplayed());
		String fileName = page.selectReferenceFile();
		assertTrue("Page should display reference file name.", page.isReferenceFileNameDisplayed(fileName));
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
	public void testRemoveSample() {
		addSamplesToCart();

		int numberOfSamplesDisplayed = page.getNumberOfSamplesDisplayed();

		page.removeFirstSample();
		int laterNumber = page.getNumberOfSamplesDisplayed();

		assertEquals("should have 1 less sample than before", numberOfSamplesDisplayed - 1, laterNumber);
		assertEquals("cart samples count should equal samples on page", laterNumber, page.getCartCount());
	}

	@Test
	public void testModifyParameters() {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value
		String value = page.getSNVAbundanceRatio();
		String newValue = "10";
		page.setSNVAbundanceRatio(newValue);
		assertEquals("Should not have the same value as the default after being changed", newValue,
				page.getSNVAbundanceRatio());
		page.clickSetDefaultSNVAbundanceRatio();
		assertEquals("Value should be reset to the default value", value, page.getSNVAbundanceRatio());
	}

	@Test
	public void testModifyParametersAgain() throws InterruptedException {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value for the ALternative Allele Fraction
		String newValue = "10";
		page.setSNVAbundanceRatio(newValue);
		assertEquals("Should be set to the new value.", newValue, page.getSNVAbundanceRatio());

		page.clickUseParametersButton();

		// open the dialog again and make sure that the changed values are still
		// there:
		page.clickPipelineParametersBtn();
		assertEquals("snv abundance ratio should have the same value as the new value after being changed",
				newValue, page.getSNVAbundanceRatio());
	}

	@Test
	public void testModifyAndSaveParameters() {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value for the ALternative Allele Fraction
		String newValue = "10";
		final String savedParametersName = "Saved parameters name.";
		page.setSNVAbundanceRatio(newValue);
		assertEquals("Should have updated snv abundance ratio value to new value.", newValue,
				page.getSNVAbundanceRatio());
		page.clickSaveParameters();
		assertTrue("Page should have shown name for parameters field with selected parameters name.",
				page.isNameForParametersVisible());
		page.setNameForSavedParameters(savedParametersName);
		page.clickUseParametersButton();
		assertEquals("Selected parameter set should be the saved one.", savedParametersName,
				page.getSelectedParameterSet());
		// now test that we can run the pipeline
		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
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
