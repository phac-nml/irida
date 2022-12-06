package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportsListingPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportsListingPageIT.xml")
public class NcbiExportsListingPageIT extends AbstractIridaUIITChromeDriver {
	@Test
	public void pageSetup() {
		LoginPage.loginAsAdmin(driver());
		NcbiExportsListingPage page = NcbiExportsListingPage.goTo(driver());
		assertEquals(1, page.getNumberOfBioSampleIdsDisplayed(), "Should be 1 entry in the table");
	}
}
