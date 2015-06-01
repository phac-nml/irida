package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class CreateClientPageIT extends AbstractIridaUIIT {
	private CreateClientPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new CreateClientPage(driver());
	}

	@Test
	public void testCreateGoodClient() {
		page.createClientWithDetails("newClient", "password", true, false);
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithExistingId() {
		page.createClientWithDetails("testClient", "password", true, false);
		assertFalse(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithNoScope() {
		page.createClientWithDetails("testClient", "password", false, false);
		assertFalse(page.checkSuccess());
	}

}
