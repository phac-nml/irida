package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesSelectionPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class PipelinesSelectionPageIT extends AbstractIridaUIIT {
	private PipelinesSelectionPage pipelinesSelectionPage;

	@Before
	public void setUpTest() {
		pipelinesSelectionPage = new PipelinesSelectionPage(driver());
	}

	@Test
	public void testPageSetup() {
		LoginPage.loginAsUser(driver());
		pipelinesSelectionPage.goToPage();
		assertTrue(pipelinesSelectionPage.arePipelinesDisplayed());
	}
}
