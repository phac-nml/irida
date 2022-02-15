package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminClientsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/CreateRemoteApisIT.xml")
public class CreateRemoteAPIPageIT extends AbstractIridaUIITChromeDriver {
	private CreateRemoteAPIPage page;

	private final String clientId = "testClient";
	private String clientSecret;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		AdminClientsPage clientsPage = AdminClientsPage.goTo(driver());

		String redirectUrl = RemoteApiUtilities.getRedirectLocation();

		clientsPage.createClientWithDetails(clientId, "authorization_code", redirectUrl, AdminClientsPage.READ_YES,
				AdminClientsPage.WRITE_NO);

		clientSecret = clientsPage.getClientSecret(clientId);

		page = new CreateRemoteAPIPage(driver());
		page.goTo();
	}

	@Test
	public void testCreateRemoteApi() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret");
		assertTrue(page.checkSuccess(), "remote api should be created");
	}

	@Test
	public void testCreateClientWithDuplicateURI() {
		page.createRemoteAPIWithDetails("new name", "http://nowhere", "newClient", "newSecret");
		assertFalse(page.checkSuccess(), "client should not have been created");
	}

	@Test
	public void testCreateClientWithSpacesInClientID() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient ", "newSecret");
		assertFalse(page.checkSuccess(), "client should not have been created");
	}

	@Test
	public void testCreateClientWithSpacesInClientSecret() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret ");
		assertFalse(page.checkSuccess(), "client should not have been created");
	}
	
	@Test
	public void testCreateClientWithSpacesInFront() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", " newSecret");
		assertFalse(page.checkSuccess(), "client should not have been created");
	}

	@Test
	public void testAndConnectToClient() {
		page.createRemoteAPIWithDetails("new name", page.getBaseUrl()  + "/api", clientId, clientSecret);
		assertTrue(page.checkSuccess(), "client should have been created");

		RemoteAPIDetailsPage remoteAPIDetailsPage = RemoteAPIDetailsPage.gotoDetailsPage(driver());

		assertFalse(remoteAPIDetailsPage.isRemoteAPIConnected(), "API status should not be connect");
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		assertTrue(page.checkSuccess(), "API status is now connected");
	}
}
