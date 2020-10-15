package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/CreateRemoteApisIT.xml")
public class CreateRemoteAPIPageIT extends AbstractIridaUIITChromeDriver {
	private CreateRemoteAPIPage page;
	private CreateClientPage createClientPage;
	private ClientDetailsPage clientDetailsPage;

	private final String clientId = "testClient";
	private String clientSecret;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new CreateRemoteAPIPage(driver());
		createClientPage = new CreateClientPage(driver());

		String redirectUrl = RemoteApiUtilities.getRedirectLocation();

		createClientPage.goTo();
		createClientPage.createClientWithDetails(clientId, "authorization_code", redirectUrl, true, false);
		clientDetailsPage = new ClientDetailsPage(driver());

		clientSecret = clientDetailsPage.getClientSecret();

		page.goTo();
	}

	@Test
	public void testCreateRemoteApi() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret");
		assertTrue("remote api should be created", page.checkSuccess());
	}

	@Test
	public void testCreateClientWithDuplicateURI() {
		page.createRemoteAPIWithDetails("new name", "http://nowhere", "newClient", "newSecret");
		assertFalse("client should not have been created", page.checkSuccess());
	}

	@Test
	public void testCreateClientWithSpacesInClientID() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient ", "newSecret");
		assertFalse("client should not have been created", page.checkSuccess());
	}

	@Test
	public void testCreateClientWithSpacesInClientSecret() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret ");
		assertFalse("client should not have been created", page.checkSuccess());
	}
	
	@Test
	public void testCreateClientWithSpacesInFront() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", " newSecret");
		assertFalse("client should not have been created", page.checkSuccess());
	}

	@Test
	public void testAndConnectToClient() {
		page.createRemoteAPIWithDetails("new name", page.getBaseUrl()  + "/api", clientId, clientSecret);
		assertTrue("client should have been created", page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = RemoteAPIDetailsPage.gotoDetailsPage(driver());

		assertFalse("API status should not be connect",  remoteAPIDetailsPage.isRemoteAPIConnected());
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		assertTrue("API status is now connected", page.checkSuccess());
	}
}
