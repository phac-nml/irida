package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
public class RemoteAPIDetailsPageIT extends AbstractIridaUIITChromeDriver {
	Long id = 1L;
	String apiName = "a client";
	String apiClient = "client";
	@Test
	public void testVerifyClient() {
		LoginPage.loginAsManager(driver());

		RemoteAPIDetailsPage page = RemoteAPIDetailsPage.gotoDetailsPage(driver(), id);
		String clientName = page.getClientName();
		assertEquals(apiName, clientName);

		String clientId = page.getClientId();
		assertEquals(apiClient, clientId);

		assertFalse(page.isRemoteAPIConnected());

	}

	@Test
	public void testDeleteClient() {
		LoginPage.loginAsManager(driver());
		RemoteAPIsPage listingPage = RemoteAPIsPage.goTo(driver());
		int count = listingPage.remoteApisTableSize();
		RemoteAPIDetailsPage page = RemoteAPIDetailsPage.gotoDetailsPage(driver(), id);
		page.clickDeleteButton();
		listingPage = RemoteAPIsPage.goTo(driver());
		assertEquals(count - 1, listingPage.remoteApisTableSize());
	}

}
