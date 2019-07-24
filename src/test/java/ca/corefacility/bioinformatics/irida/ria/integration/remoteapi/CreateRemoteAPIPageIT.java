package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;
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

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new CreateRemoteAPIPage(driver());
	}

	@Test
	public void testCreateGoodClient() {
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret");
		assertTrue("client should be created", page.checkSuccess());
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

		page.createRemoteAPIWithDetails("new name", url, "testClient", "testClientSecret");
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
