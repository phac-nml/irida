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
	@Test
	void testCreateNcbiSubmission() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSampleByName("sample1");
		samplesPage.selectSampleByName("sample2");
		samplesPage.selectSampleByName("sample3");

		NcbiExportPage page = NcbiExportPage.goToPage(driver(), 1);
		assertEquals(3, page.getNumberOfSamples(), "Should display three sample panels");
	}
}
