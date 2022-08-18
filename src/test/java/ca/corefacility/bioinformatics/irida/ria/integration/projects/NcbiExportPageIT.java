package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {
	private NcbiExportPage page = NcbiExportPage.init(driver());
	@Test
	void testCreateNcbiSubmission() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample1");
		samplesPage.selectSampleByName("sample2");
		samplesPage.selectSampleByName("sample3");
		samplesPage.shareExportSamplesToNcbi();

		assertEquals(3, page.getNumberOfSamples(), "Should display three sample panels");

		// Enter BioSample information
		page.enterBioProject("BIOPROJECT-1");
		page.enterNamespace("IRIDA");
		page.enterOrganization("NML-PHAC");
	}
}
