package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertFalse("Share button should be disabled without a project selected",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.searchForProject("project2");
		Assert.assertTrue("Share button should be enabled after selecting a project",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.submitShareRequest();
		Assert.assertTrue("Success result should be displayed", shareSamplesPage.isSuccessResultDisplayed());
		Assert.assertEquals("Successfully Shared 1 Sample", shareSamplesPage.getSuccessTitle());

		// MOVING MULTIPLE SAMPLES

		addMultipleSamples();
		Assert.assertFalse("Share button should be disabled without a project selected",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.searchForProject("project2");
		Assert.assertTrue("Should display a warning that some samples cannot be copied",
				shareSamplesPage.isSomeSamplesWarningDisplayed());
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.submitShareRequest();
		Assert.assertTrue("Successful move multiple message should be displayed",
				shareSamplesPage.isMoveMultipleSuccessDisplayed());
		Assert.assertEquals("Successfully Moved Samples", shareSamplesPage.getSuccessTitle());

		// MOVE SINGLE SAMPLE

		addOneSample();
		Assert.assertFalse("Share button should be disabled without a project selected",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.searchForProject("project4");
		Assert.assertTrue("Share button should be enabled after selecting a project",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.selectMoveCheckbox();
		shareSamplesPage.submitShareRequest();
		Assert.assertTrue("Success result should be displayed", shareSamplesPage.isSuccessResultDisplayed());
		Assert.assertEquals("Successfully Moved 1 Sample", shareSamplesPage.getSuccessTitle());

		// SHARING MULTIPLE SAMPLES

		addMultipleSamples();
		Assert.assertFalse("Share button should be disabled without a project selected",
				shareSamplesPage.isShareButtonDisabled());
		shareSamplesPage.searchForProject("project2");
		Assert.assertTrue("Share button should be enabled after selecting a project",
				shareSamplesPage.isShareButtonDisabled());

		Assert.assertEquals("Should be 4 samples displayed", 4, shareSamplesPage.getNumberOfSamplesDisplayed());

		shareSamplesPage.submitShareRequest();
		Assert.assertTrue("Success result should be displayed", shareSamplesPage.isSuccessResultDisplayed());
		Assert.assertEquals("Successfully Shared Samples", shareSamplesPage.getSuccessTitle());

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
