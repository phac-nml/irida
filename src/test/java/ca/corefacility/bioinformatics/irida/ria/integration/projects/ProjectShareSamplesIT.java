package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ShareSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectShareSamples.xml")
public class ProjectShareSamplesIT extends AbstractIridaUIITChromeDriver {
	private ShareSamplesPage shareSamplesPage = ShareSamplesPage.initPage(driver());

	@Test
	public void testShareSamplesAsManager() {

		LoginPage.loginAsManager(driver());

		// SHARING SINGLE SAMPLE
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1L);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.shareSamples();

		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"The next button should not be enabled when going to the page");
		shareSamplesPage.searchForProject("3");
		assertThat(shareSamplesPage.getProjectSelectText()).contains("ID: 3");
		assertTrue(shareSamplesPage.isNextButtonEnabled(), "Next button should be enabled");
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
		samplesPage = ProjectSamplesPage.goToPage(driver(), 1L);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName("sample554sg5");
		samplesPage.selectSampleByName("sample5ddfg4");
		samplesPage.selectSampleByName("sample57567");
		samplesPage.shareSamples();

		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		shareSamplesPage.gotToNextStep();
		assertEquals(3, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should display the 3 samples selected");
		assertTrue(shareSamplesPage.isSomeSamplesSameIdsWarningDisplayed(),
				"Should display an expandable warning which lists the samples that will not be copied over as the samples with the same identifiers already exist in the target project");
		shareSamplesPage.expandSameSampleIdsWarning();
		assertEquals(1, shareSamplesPage.numberOfSamplesWithSameIds(),
				"There should be one sample listed which exists in the target project with the same identifier");

		assertFalse(shareSamplesPage.isSomeSamplesSameNamesWarningDisplayed(),
				"Shouldn't display an expandable warning which lists the samples that will not be copied over as the samples with the same names but different identifiers exist in the target project");
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isMoveMultipleSuccessDisplayed(),
				"Successful move multiple message should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Moved Samples");

		// MOVE SINGLE SAMPLE
		samplesPage = ProjectSamplesPage.goToPage(driver(), 1L);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.shareSamples();

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
		samplesPage = ProjectSamplesPage.goToPage(driver(), 1L);
		samplesPage.selectSampleByName("sample_5_fg_22");
		samplesPage.selectSampleByName("sample-5-fg-22");
		samplesPage.selectSampleByName("sample5dt5");
		samplesPage.selectSampleByName("sample55422r");
		samplesPage.shareSamples();
		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project2");
		shareSamplesPage.gotToNextStep();
		assertEquals(4, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should be 4 samples displayed");
		shareSamplesPage.gotToNextStep();
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isSuccessResultDisplayed(), "Success result should be displayed");
		assertEquals(shareSamplesPage.getSuccessTitle(), "Successfully Shared Samples");

		samplesPage = ProjectSamplesPage.goToPage(driver(), 2L);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.shareSamples();
		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"Share button should be disabled without a project selected");
		shareSamplesPage.searchForProject("project8");
		shareSamplesPage.gotToNextStep();

		assertFalse(shareSamplesPage.isSomeSamplesSameIdsWarningDisplayed(),
				"Shouldn't display an expandable warning which lists the samples that will not be copied over as the samples with the same identifiers already exist in the target project");
		assertTrue(shareSamplesPage.isSomeSamplesSameNamesWarningDisplayed(),
				"Should display an expandable warning which lists the samples that will not be copied over as the samples with the same names but different identifiers exist in the target project");
		shareSamplesPage.expandSameSampleNamesWarning();
		assertEquals(1, shareSamplesPage.numberOfSamplesWithSameNames(),
				"There should be one sample listed which exists in the target project with the same name and different identifier");
	}

	@Test
	void testSharingWithALockedSample() {
		final String LOCKED_SAMPLE_NAME = "sample5fdgr";

		LoginPage.loginAsManager(driver());

		// SHARING SINGLE SAMPLE
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1L);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName(LOCKED_SAMPLE_NAME);
		samplesPage.shareSamples();

		assertFalse(shareSamplesPage.isNextButtonEnabled(),
				"The next button should not be enabled when going to the page");
		shareSamplesPage.searchForProject("3");
		assertThat(shareSamplesPage.getProjectSelectText()).contains("ID: 3");
		assertTrue(shareSamplesPage.isNextButtonEnabled(), "Next button should be enabled");
		shareSamplesPage.searchForProject("project2");
		assertTrue(shareSamplesPage.isNextButtonEnabled(), "Next button should be enabled");
		shareSamplesPage.gotToNextStep();

		assertEquals(1, shareSamplesPage.getNumberOfSamplesDisplayed(), "Should display the one sample");
		assertEquals(1, shareSamplesPage.getNumberOfLockedSamplesDisplayed(), "Should have 1 locked sample");
		assertTrue(shareSamplesPage.isPreviousButtonEnabled(),
				"Since on the second step, the previous button should be enabled");
		shareSamplesPage.gotToNextStep();
		assertEquals(0, shareSamplesPage.getNumberOfSharedMetadataEntries(), "Should have no fields to share");
		shareSamplesPage.submitShareRequest();
		assertTrue(shareSamplesPage.isShareSingleSuccessDisplayed(), "Success message should be displayed");

	}
}
