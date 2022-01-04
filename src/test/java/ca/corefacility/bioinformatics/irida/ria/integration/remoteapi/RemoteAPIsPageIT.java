package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
public class RemoteAPIsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testRemoteAPIPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		RemoteAPIsPage apisPage = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = apisPage.remoteApisTableSize();
		assertEquals(2, clientsTableSize);
		assertTrue("Should be able to see client details", apisPage.canSeeRemoteDetails());
		assertTrue(apisPage.checkRemoteApiExistsInTable("a client"));
		assertTrue(apisPage.checkRemoteApiExistsInTable("another client"));
	}

	@Test
	public void testRemoteAPIPageAsUser() {
		LoginPage.loginAsUser(driver());
		RemoteAPIsPage apisPage = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = apisPage.remoteApisTableSize();
		assertEquals(2, clientsTableSize);
		assertFalse("Should not be able to link to client details", apisPage.canSeeRemoteDetails());
		assertTrue(apisPage.checkRemoteApiExistsInTable("a client"));
		assertTrue(apisPage.checkRemoteApiExistsInTable("another client"));
	}

	@Test
	public void testCreateRemoteAPI() {
		LoginPage.loginAsAdmin(driver());
		RemoteAPIsPage page = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = page.remoteApisTableSize();
		assertEquals(2, clientsTableSize);

		page.openAddRemoteModal();
		page.enterApiDetails("FOOBAR", "", "FLDSK", "https://example.com");
	}
}
