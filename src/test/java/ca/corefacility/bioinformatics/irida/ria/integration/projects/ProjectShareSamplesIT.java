package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ShareSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectShareSamplesIT extends AbstractIridaUIITChromeDriver {
	private ShareSamplesPage shareSamplesPage = ShareSamplesPage.initPage(driver());

	@Test
	public void testShareSamplesAsManager() {

		LoginPage.loginAsManager(driver());
		ProjectSamplesPage projectSamplesPage = ProjectSamplesPage.gotToPage(driver(), 1);

		// SHARING SINGLE SAMPLE
		addOneSample();
		assertFalse(shareSamplesPage.isNextButtonEnabled(), "");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isNextButtonEnabled(), "Next button should be enabled");
		shareSamplesPage.gotToNextStep();

		assertEquals(1, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should display the one sample");
		assertTrue(shareSamplesPage.isPreviousButtonEnabled(),
				"Since on the second step, the previous button should be enabled");
		shareSamplesPage.gotToNextStep();
		assertEquals(0, shareSamplesPage.getNumberOfSharedMetadataEntries(), "Should have no fields to share");
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isShareSingleSuccessDisplayed(), "Success message should be displayed");

		// MOVING MULTIPLE SAMPLES

		addMultipleSamples();
		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		shareSamplesPage.gotToNextStep();
		assertEquals(3, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should display the 3 samples selected");
		assertTrue(shareSamplesPage.isSomeSamplesWarningDisplayed(),
				"Should display a warning that some samples cannot be copied");
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isMoveMultipleSuccessDisplayed(),
				"Successful move multiple message should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Moved Samples");

		// MOVE SINGLE SAMPLE

		addOneSample();
		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project4");
		assertTrue(shareSamplesPage.isNextButtonEnabled(), "Share button should be enabled after selecting a project");
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Moved 1 Sample");

		// SHARING MULTIPLE SAMPLES

		addMultipleSamples();
		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		shareSamplesPage.gotToNextStep();
		assertEquals(4, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should be 4 samples displayed");
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Shared Samples");

	}

	private void addOneSample() {
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.shareSamples();
	}

	private void addMultipleSamples() {
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(1);
		samplesPage.selectSample(2);
		samplesPage.selectSample(3);
		samplesPage.shareSamples();
	}
}
