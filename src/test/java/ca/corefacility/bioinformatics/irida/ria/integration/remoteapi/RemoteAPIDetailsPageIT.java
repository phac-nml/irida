package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * IT for the client details page
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
public class RemoteAPIDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private RemoteAPIDetailsPage page;

	Long id = 1L;
	String apiName = "a client";
	String apiClient = "client";

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());

		page = new RemoteAPIDetailsPage(driver(), id);
	}

	@Test
	public void testVerifyClient() {
		String clientName = page.getClientName();
		assertEquals(apiName, clientName);

		String clientId = page.getClientId();
		assertEquals(apiClient, clientId);

		ApiStatus remoteApiStatus = page.getRemoteApiStatus();
		assertEquals(ApiStatus.INVALID, remoteApiStatus);

	}

	@Test
	public void testDeleteClient() {
		page.clickDeleteButton();
		page.confirmDelete();
		assertTrue(page.checkDeleteSuccess());
	}

}
