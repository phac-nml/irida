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
 * Testing for launching an assembly pipeline.
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/AssemblyPipelinePageIT.xml")
public class AssemblyPipelinePageIT extends AbstractIridaUIITChromeDriver {
	protected LaunchPipelinePage page;

	@BeforeEach
	public void setUpTest() throws IOException {
		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1);
		samplesPage.selectSampleByName("sample1");
		samplesPage.selectSampleByName("sample2");
		samplesPage.addSelectedSamplesToCart();
	}

	@Test
	public void testPageSetup() {
		CartPage cartPage = new CartPage(driver());
		cartPage.selectAssemblyPipeline();
		assertEquals("Assembly and Annotation Pipeline", page.getPipelineName(),
				"Launch Page should display  the pipeline name");
		assertTrue(page.isLaunchFormDisplayed(), "Launch form should be displayed");
		assertTrue(page.isLaunchDetailsDisplayed(), "Launch details should be displayed");
		assertTrue(page.isLaunchParametersDisplayed(), "Launch parameters should be displayed");
		assertTrue(page.isShareWithSamplesDisplayed(), "Share with samples should not be displayed");
		assertTrue(page.isShareWithProjectsDisplayed(), "Share with projects should be displayed");
		assertFalse(page.isReferenceFilesDisplayed(), "Should be able to select a reference file");
		assertTrue(page.isLaunchFilesDisplayed(), "Should be able to select sample files");
		assertFalse(page.isReferenceFilesRequiredDisplayed(),
				"This pipeline does not need a reference file, so there should be none requested");

		// Test email checkbox
		assertEquals(page.getEmailValue(), "No Email");

		// Test the name input
		page.clearName();
		assertTrue(page.isNameErrorDisplayed(), "There should be an error displayed that a name is required");
		page.updateName("TEST_NAME");
		assertFalse(page.isNameErrorDisplayed(), "Name required warning should be cleared");

		// Make sure the saved pipeline parameter inputs are set up correctly
		page.showSavedParameters();
		assertEquals(20, page.getNumberOfSavedPipelineParameters(), "Assembly Pipeline should have 20 inputs");
		assertFalse(page.isModifiedAlertVisible(), "Should not be displaying modified parameter alert");

		// Test submitting
		page.submit();
		WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
		wait.until(ExpectedConditions.urlMatches("/analysis"));
	}
}
