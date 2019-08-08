package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisAdminPageIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPageSetup() {
		AnalysesUserPage userPage = AnalysesUserPage.initializePage(driver());
	}
}
