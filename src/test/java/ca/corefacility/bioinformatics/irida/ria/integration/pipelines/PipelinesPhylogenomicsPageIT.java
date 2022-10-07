package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;
import java.time.Duration;

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
 * <p>
 * Testing for launching a phylogenomics pipeline.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/PipelinePhylogenomicsView.xml")
public class PipelinesPhylogenomicsPageIT extends AbstractIridaUIITChromeDriver {
	protected LaunchPipelinePage page;

	@BeforeEach
	public void setUpTest() throws IOException {
		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		LoginPage.loginAsUser(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1);
		samplesPage.selectSampleByName("sample1");
		samplesPage.selectSampleByName("sample2");
		samplesPage.addSelectedSamplesToCart();
	}

	@Test
	public void testPageSetup() {
		CartPage cartPage = new CartPage(driver());
		cartPage.selectPhylogenomicsPipeline();
		assertEquals("SNVPhyl Phylogenomics Pipeline", page.getPipelineName(),
				"Launch Page should display  the pipeline name");
		assertTrue(page.isLaunchFormDisplayed(), "Launch form should be displayed");
		assertTrue(page.isLaunchDetailsDisplayed(), "Launch details should be displayed");
		assertTrue(page.isLaunchParametersDisplayed(), "Launch parameters should be displayed");
		assertFalse(page.isShareWithSamplesDisplayed(), "Share with samples should not be displayed");
		assertTrue(page.isShareWithProjectsDisplayed(), "Share with projects should be displayed");
		assertTrue(page.isReferenceFilesDisplayed(), "Should be able to select a reference file");
		assertTrue(page.isLaunchFilesDisplayed(), "Should be able to select sample files");
		assertTrue(page.isReferenceFilesRequiredDisplayed(),
				"Should have an alert showing that reference files were not found");
		assertFalse(page.isReferenceFilesRequiredErrorDisplayed(),
				"Reference file error should only be shown after trying to submit");

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
		assertEquals(8, page.getNumberOfSavedPipelineParameters(), "Phylogenomics Pipeline should have 8 inputs");
		assertFalse(page.isModifiedAlertVisible(), "Should not be displaying modified parameter alert");

		String MINIMUM_COVERAGE_PARAMETER = "minimum-percent-coverage";
		String originalMinimumPercentCoverageValue = page.getSavedParameterValue(MINIMUM_COVERAGE_PARAMETER);
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER, "123456");
		assertTrue(page.isModifiedAlertVisible(), "Modified parameters alert should be displayed.");
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER, originalMinimumPercentCoverageValue);
		assertFalse(page.isModifiedAlertVisible(),
				"Modified alert should go way once the value is the same as the original");

		// Test saving modified parameters
		String newCoverage = "123";
		String newRepeat = "456";
		page.updateSavedParameterValue(MINIMUM_COVERAGE_PARAMETER, newCoverage);
		String REPEAT_MINIMUM_LENGTH_PARAMETER = "repeat-minimum-length";
		page.updateSavedParameterValue(REPEAT_MINIMUM_LENGTH_PARAMETER, newRepeat);
		assertTrue(page.isModifiedAlertVisible(), "Modified parameters alert should be displayed.");
		final String newModifiedTemplateName = "FOOBAR";
		page.saveModifiedTemplateAs(newModifiedTemplateName);
		assertEquals(newModifiedTemplateName, page.getSelectedParametersTemplateName(),
				"Then newly template should be selected");

		// Test submitting
		page.submit();
		assertTrue(page.isReferenceFilesRequiredErrorDisplayed(),
				"Should display warning that a reference file is required");

		page.uploadReferenceFile();
		assertFalse(page.isReferenceFilesRequiredDisplayed(), "Now a reference file should exist");
		page.submit();
		WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
		wait.until(ExpectedConditions.urlMatches("/analysis/"));
	}
}
