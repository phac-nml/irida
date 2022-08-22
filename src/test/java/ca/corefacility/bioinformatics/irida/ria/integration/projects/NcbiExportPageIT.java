package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {
	private final NcbiExportPage page = NcbiExportPage.init(driver());

	@Test
	void testCreateNcbiSubmission() {
		String SAMPLE_1 = "sample1";
		String SAMPLE_2 = "sample2";
		String SAMPLE_3 = "sample3";
		String BIOPROJECT = "BIOPROJECT-1";
		String NAMESPACE = "NAMESPACE-FOOBAR";
		String ORGANIZATION = "ORGANIZATION-FOOBAR";

		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName(SAMPLE_1);
		samplesPage.selectSampleByName(SAMPLE_2);
		samplesPage.selectSampleByName(SAMPLE_3);
		samplesPage.shareExportSamplesToNcbi();

		assertEquals(3, page.getNumberOfSamples(), "Should display three sample panels");

		// Enter BioSample information
		page.enterBioProject(BIOPROJECT);
		page.enterNamespace(NAMESPACE);
		page.enterOrganization(ORGANIZATION);
	}
}
