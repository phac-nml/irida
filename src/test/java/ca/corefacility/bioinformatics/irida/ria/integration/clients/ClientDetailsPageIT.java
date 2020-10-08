package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
public class ClientDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private ClientDetailsPage page;

	Long id = 1L;
	String clientId = "testClient";

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new ClientDetailsPage(driver());
		page.goToPage(id);
	}

	@Test
	public void testCheckClientsPageNumber() {
		assertTrue(page.verifyClient(id, clientId));

	}

	@Test
	public void testDeleteClient() {
		page.clickDeleteButton();
		page.confirmDelete();
		assertTrue(page.checkDeleteSuccess());
	}

}
