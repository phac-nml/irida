package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * IT for the clients list page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class ClientsPageIT extends AbstractIridaUIIT {
	private ClientsPage clientsPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		clientsPage = new ClientsPage(driver());
	}

	@Test
	public void testCheckClientsPageNumber() {
		clientsPage.goTo();
		int clientsTableSize = clientsPage.clientsTableSize();
		assertEquals(2, clientsTableSize);
		assertTrue(clientsPage.checkClientExistsInTable("testClient"));
		assertTrue(clientsPage.checkClientExistsInTable("testClient2"));
	}

}
