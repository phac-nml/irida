package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportsListingPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportsListingPageIT.xml")
public class NcbiExportsListingPageIT extends AbstractIridaUIITChromeDriver {
	@Test
	public void pageSetup() {
		LoginPage.loginAsAdmin(driver());
		NcbiExportsListingPage page = NcbiExportsListingPage.goTo(driver());
		assertEquals("Should be 1 entry in the table", 1, page.getNumberOfBioSampleIdsDisplayed());
	}
}
