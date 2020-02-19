package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
public class CreateRemoteAPIPageIT extends AbstractIridaUIITChromeDriver {
	private CreateRemoteAPIPage page;
	private CreateClientPage createClientPage;
	private ClientDetailsPage clientDetailsPage;

	private String clientId = "testClient";
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
		String baseUrl = page.getBaseUrl();
		String url = baseUrl + "api";

		page.createRemoteAPIWithDetails("new name", url, clientId, clientSecret);
		assertTrue("client should have been created", page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = new RemoteAPIDetailsPage(driver());

		ApiStatus remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals("api status should be invalid", ApiStatus.INVALID, remoteApiStatus);
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals("api status should be connected", ApiStatus.CONNECTED, remoteApiStatus);
	}
}
