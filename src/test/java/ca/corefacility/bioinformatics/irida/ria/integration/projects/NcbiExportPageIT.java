package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {
	private final NcbiExportPage page = NcbiExportPage.init(driver());

	@Test
	void testCreateNcbiSubmission() throws Exception {
		String SAMPLE_1 = "sample1";
		String SAMPLE_2 = "sample2";
		String SAMPLE_3 = "sample3";
		String BIOPROJECT = "BIOPROJECT-1";
		String NAMESPACE = "NAMESPACE-FOOBAR";
		String ORGANIZATION = "ORGANIZATION-FOOBAR";
		String PROTOCOL = "AMAZING_PROTOCOL";
		String DEFAULT_PROTOCOL = "DEFAULT_PROTOCOL";

		LoginPage.loginAsManager(driver());
		int PROJECT_ID = 1;
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), PROJECT_ID);
		samplesPage.selectSampleByName(SAMPLE_1);
		samplesPage.selectSampleByName(SAMPLE_2);
		samplesPage.selectSampleByName(SAMPLE_3);
		samplesPage.shareExportSamplesToNcbi();

		assertEquals(3, page.getNumberOfSamples(), "Should display three sample panels");

		// Enter BioSample information
		page.enterBioProject(BIOPROJECT);
		page.enterNamespace(NAMESPACE);
		page.enterOrganization(ORGANIZATION);

		// Test default sample settings.
		page.toggleDefaultsPanel();
		page.setDefaultStrategySelect("WGS");
		// Check to see that the defaults set in the samples
		page.openSamplePanelBySampleName(SAMPLE_1);
		assertEquals("WGS", page.getSelectValueForSampleField("strategy"),
				"Sample should have the strategy set automatically");
		page.openSamplePanelBySampleName(SAMPLE_2);
		assertEquals("WGS", page.getSelectValueForSampleField("strategy"),
				"Sample should have the strategy set automatically");
		page.openSamplePanelBySampleName(SAMPLE_3);
		assertEquals("WGS", page.getSelectValueForSampleField("strategy"),
				"Sample should have the strategy set automatically");
		// Update a sample field and make sure the default on doesn't overwrite it.
		page.openSamplePanelBySampleName(SAMPLE_1);
		page.setTextInputForSampleFieldValue("protocol", PROTOCOL);
		assertNotEquals(PROTOCOL, page.getInputValueForDefaultField("protocol"));
		page.setDefaultInputFieldValue("protocol", DEFAULT_PROTOCOL);
		assertNotEquals(DEFAULT_PROTOCOL, page.getInputValueForSampleField("protocol"));

		// Test removing samples
		page.removeSample(SAMPLE_2);
		page.removeSample(SAMPLE_3);
		assertEquals(1, page.getNumberOfSamples(), "Should now be only 1 sample on the page");

		// Check to see if the sample is ready to submit
		assertFalse(page.isSampleValid(SAMPLE_1), "Sample has empty fields and should not be valid to submit");
		page.submitExportForm();
		assertTrue(page.areFormErrorsPresent(), "Form should not have submitted and errors should be displayed");

		// Fill in the rest of the form
		page.setTextInputForSampleFieldValue("biosample", "BIOSAMPLE-001");
		page.setSelectForSampleFieldValue("source", "SYNTHETIC");
		page.setSelectForSampleFieldValue("selection", "CAGE");
		page.setCascaderForSampleField("model", "ILLUMINA", "Illumina Genome Analyzer II");
		page.selectSingleEndSequenceFile("test_file.fastq");
		assertTrue(page.isSampleValid(SAMPLE_1));
		assertFalse(page.areFormErrorsPresent(), "All errors should now be cleared now that the form is valid");

		page.submitExportForm();
		assertTrue(page.isSuccessAlertDisplayed(), "Success notification should be displayed");
		assertTrue(page.isUserRedirectedToProjectSamplesPage(PROJECT_ID),
				"User should be redirected within 5 seconds of submission");
	}
}
