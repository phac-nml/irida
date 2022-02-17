package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * IT for the client details page
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class CreateClientPageIT extends AbstractIridaUIITChromeDriver {
	private CreateClientPage page;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new CreateClientPage(driver());
		page.goTo();
	}

	@Test
	public void testCreateGoodClient() {
		page.createClientWithDetails("newClient", "password", null, true, false);
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithExistingId() {
		page.createClientWithDetails("testClient", "password", null, true, false);
		assertFalse(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithNoScope() {
		page.createClientWithDetails("testClient", "password", null, false, false);
		assertFalse(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithSpace() {
		page.createClientWithDetails("newClient ", "password", null, true, false);
		assertFalse(page.checkSuccess(), "should have failed due to space");
	}

	@Test
	public void testCreateAuthCodeClient() {
		page.createClientWithDetails("newClient", "authorization_code", "http://irida.ca", true, false);
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testCreateAuthCodeClientWithoutRedirect() {
		page.createClientWithDetails("newClient", "authorization_code", null, true, false);
		assertFalse(page.checkSuccess(), "should have failed due to no redirect URI");
	}

}
