package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
public class RemoteAPIsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testRemoteAPIPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		RemoteAPIsPage apisPage = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = apisPage.remoteApisTableSize();
		assertEquals(2, clientsTableSize);
		assertTrue(apisPage.canSeeConnectButton(), "Should be able to see client details");
		assertTrue(apisPage.checkRemoteApiExistsInTable("a client"));
		assertTrue(apisPage.checkRemoteApiExistsInTable("another client"));
	}

	@Test
	public void testRemoteAPIPageAsUser() {
		LoginPage.loginAsUser(driver());
		RemoteAPIsPage apisPage = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = apisPage.remoteApisTableSize();
		assertEquals(2, clientsTableSize);
		assertFalse(apisPage.canSeeConnectButton(), "Should not be able to link to client details");
		assertTrue(apisPage.checkRemoteApiExistsInTable("a client"));
		assertTrue(apisPage.checkRemoteApiExistsInTable("another client"));
	}
}
