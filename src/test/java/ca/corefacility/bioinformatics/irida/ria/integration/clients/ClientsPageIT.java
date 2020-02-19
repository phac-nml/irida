package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * IT for the clients list page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class ClientsPageIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testCheckClientsPageNumber() {
		ClientsPage clientsPage = ClientsPage.goTo(driver());
		int clientsTableSize = clientsPage.clientsTableSize();
		assertEquals(2, clientsTableSize);
		assertTrue(clientsPage.checkClientExistsInTable("testClient"));
		assertTrue(clientsPage.checkClientExistsInTable("testClient2"));
	}

}
