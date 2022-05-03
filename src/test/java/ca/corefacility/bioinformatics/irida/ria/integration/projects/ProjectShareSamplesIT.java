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
	public void testShareSamples() {
		LoginPage.loginAsManager(driver());

		// SHARING SINGLE SAMPLE
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.shareSamples();

		assertFalse(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isShareButtonDisabled(),
				"Share button should be enabled after selecting a project");
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Shared 1 Sample");

		// MOVING MULTIPLE SAMPLES
		samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName("sample554sg5");
		samplesPage.selectSampleByName("sample5ddfg4");
		samplesPage.selectSampleByName("sample57567");
		samplesPage.shareSamples();

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
		samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.shareSamples();

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
		samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample_5_fg_22");
		samplesPage.selectSampleByName("sample-5-fg-22");
		samplesPage.selectSampleByName("sample5dt5");
		samplesPage.selectSampleByName("sample55422r");
		samplesPage.shareSamples();
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
}
