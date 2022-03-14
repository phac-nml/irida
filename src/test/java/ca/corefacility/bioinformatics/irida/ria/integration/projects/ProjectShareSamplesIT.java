package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ShareSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectShareSamplesIT extends AbstractIridaUIITChromeDriver {
	private ShareSamplesPage shareSamplesPage = ShareSamplesPage.initPage(driver());

	@Test
	public void testShareSamples() {
		LoginPage.loginAsManager(driver());

		// SHARING SINGLE SAMPLE

		addOneSample();
		assertFalse(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be enabled after selecting a project");
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Shared 1 Sample");

		// MOVING MULTIPLE SAMPLES

		addMultipleSamples();
		assertFalse(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isSomeSamplesWarningDisplayed(),
				"Should display a warning that some samples cannot be copied");
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isMoveMultipleSuccessDisplayed(),
				"Successful move multiple message should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Moved Samples");

		// MOVE SINGLE SAMPLE

		addOneSample();
		assertFalse(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project4");
		assertTrue(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be enabled after selecting a project");
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Moved 1 Sample");

		// SHARING MULTIPLE SAMPLES

		addMultipleSamples();
		assertFalse(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be enabled after selecting a project");

		assertEquals(4, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should be 4 samples displayed");

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
