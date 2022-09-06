package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.LaunchPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing for launching a biohansel pipeline.
 */

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/BioHanselPipelinePageIT.xml")
public class BioHanselPipelinePageIT extends AbstractIridaUIITChromeDriver {
	protected LaunchPipelinePage page;

	@BeforeEach
	public void setUpTest() throws IOException {
		driver().manage().window().maximize();
		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample1");
		samplesPage.selectSampleByName("sample2");
		samplesPage.addSelectedSamplesToCart();
	}

	@Test
	public void testPageSetup() {
		CartPage cartPage = new CartPage(driver());
		cartPage.selectBiohanselPipeline();
		assertEquals("bio_hansel Pipeline", page.getPipelineName(), "Launch Page should display the pipeline name");
		assertTrue(page.isLaunchFormDisplayed(), "Launch form should be displayed");
		assertTrue(page.isLaunchDetailsDisplayed(), "Launch details should be displayed");
		assertTrue(page.isLaunchParametersDisplayed(), "Launch parameters should be displayed");
		assertTrue(page.isShareWithSamplesDisplayed(), "Share with samples should be displayed");
		assertTrue(page.isShareWithProjectsDisplayed(), "Share with projects should be displayed");
		assertFalse(page.isReferenceFilesDisplayed(), "Should not be able to select a reference file");
		assertTrue(page.isLaunchFilesDisplayed(), "Should be able to select sample files");
		assertFalse(page.isReferenceFilesRequiredDisplayed(), "Should not have an alert showing that reference files were not found");

		// Test the name input
		page.clearName();
		assertTrue(page.isNameErrorDisplayed(), "There should be an error displayed that a name is required");
		page.updateName("TEST_NAME");
		assertFalse(page.isNameErrorDisplayed(), "Name required warning should be cleared");

		// Test email checkbox
		assertEquals(page.getEmailValue(), "No Email");

		// Make sure the saved pipeline parameter inputs are set up correctly
		assertEquals(0, page.getNumberOfSavedPipelineParameters(), "No saved parameters should be initially displayed");
		page.showSavedParameters();
		assertEquals(7, page.getNumberOfSavedPipelineParameters(), "Phylogenomics Pipeline should have 7 inputs");
		assertFalse(page.isModifiedAlertVisible(), "Should not be displaying modified parameter alert");

		String MINIMUM_COVERAGE_PARAMETER = "bio_hansel-1-kmer_vals.kmer_min";
		String originalMinimumPercentCoverageValue = page.getSavedParameterValue(MINIMUM_COVERAGE_PARAMETER);
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER,"123456");
		assertTrue(page.isModifiedAlertVisible(), "Modified parameters alert should be displayed.");
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER, originalMinimumPercentCoverageValue);
		assertFalse(page.isModifiedAlertVisible(), "Modified alert should go way once the value is the same as the original");

		// Test saving modified parameters
		String newCoverage = "123";
		String newTileFrequencyThreshold = "456";
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER, newCoverage);
		String LOW_COVERAGE_DEPTH_FREQ_PARAMETER = "bio_hansel9-qc_vals.low_cov_depth_freq";
		page.updateSavedParameterValue(LOW_COVERAGE_DEPTH_FREQ_PARAMETER, newTileFrequencyThreshold);
		assertTrue(page.isModifiedAlertVisible(), "Modified parameters alert should be displayed.");
		final String newModifiedTemplateName = "FOOBAR";
		page.saveModifiedTemplateAs(newModifiedTemplateName);
		assertEquals(newModifiedTemplateName, page.getSelectedParametersTemplateName(), "Then newly template should be selected");

		// Test submitting
		page.submit();
		assertFalse(page.isRequiredParameterErrorDisplayed(), "Subtyping scheme should be selected and not display an error");
		WebDriverWait wait = new WebDriverWait(driver(), 5);
		wait.until(ExpectedConditions.urlMatches("/analysis"));
	}
}
