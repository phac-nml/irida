package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.EditClientPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.*;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class EditClientPageIT extends AbstractIridaUIIT {
	private static final String ORIGINAL_SECRET = "xxxxxxxx";

	@Override
	public WebDriver driverToUse() {
		return new ChromeDriver();
	}

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testCreateGoodClient() {
		EditClientPage page = EditClientPage.goToEditPage(driver(), 1L);
		page.editClient(null, true, true, false);
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testEditClientWithNewSecret() {
		EditClientPage page = EditClientPage.goToEditPage(driver(), 1L);
		page.editClient(null, true, true, true);
		assertTrue(page.checkSuccess());
		ClientDetailsPage detailsPage = new ClientDetailsPage(driver());
		String newSecret = detailsPage.getClientSecret();
		assertNotEquals(ORIGINAL_SECRET, newSecret);
	}

	@Test
	public void testClientsWithoutScope() {
		EditClientPage page = EditClientPage.goToEditPage(driver(), 1L);
		page.editClient(null, false, false, false);
		assertFalse(page.checkSuccess());
	}

}
