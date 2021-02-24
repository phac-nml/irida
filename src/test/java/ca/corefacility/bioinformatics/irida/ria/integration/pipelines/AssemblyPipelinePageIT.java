package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.LaunchPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * Testing for launching an assembly pipeline.
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/AssemblyPipelinePageIT.xml")
public class AssemblyPipelinePageIT extends AbstractIridaUIITChromeDriver {
	protected LaunchPipelinePage page;

	@Before
	public void setUpTest() throws IOException {
		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(1);
		samplesPage.addSelectedSamplesToCart();
	}

	@Test
	public void testPageSetup() {
		CartPage cartPage = new CartPage(driver());
		cartPage.selectAssemblyPipeline();
		assertEquals("Launch Page should display  the pipeline name", "Assembly and Annotation Pipeline", page.getPipelineName());
		assertTrue("Launch form should be displayed", page.isLaunchFormDisplayed());
		assertTrue("Launch details should be displayed", page.isLaunchDetailsDisplayed());
		assertTrue("Launch parameters should be displayed", page.isLaunchParametersDisplayed());
		assertTrue("Share with samples should not be displayed", page.isShareWithSamplesDisplayed());
		assertTrue("Share with projects should be displayed", page.isShareWithProjectsDisplayed());
		assertFalse("Should be able to select a reference file", page.isReferenceFilesDisplayed());
		assertTrue("Should be able to select sample files", page.isLaunchFilesDisplayed());
		assertFalse("This pipeline does not need a reference file, so there should be none requested", page.isReferenceFilesRequiredDisplayed());

		// Test email checkbox
		assertEquals("No Email", page.getEmailValue());

		// Test the name input
		page.clearName();
		assertTrue("There should be an error displayed that a name is required", page.isNameErrorDisplayed());
		page.updateName("TEST_NAME");
		assertFalse("Name required warning should be cleared", page.isNameErrorDisplayed());

		// Make sure the saved pipeline parameter inputs are set up correctly
		page.showSavedParameters();
		assertEquals("Assembly Pipeline should have 20 inputs", 20, page.getNumberOfSavedPipelineParameters());
		assertFalse("Should not be displaying modified parameter alert", page.isModifiedAlertVisible());

		// Test submitting
		page.submit();
		WebDriverWait wait = new WebDriverWait(driver(), 5);
		wait.until(ExpectedConditions.urlMatches("/analysis"));
	}
}
