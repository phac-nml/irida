package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	public void testCreateRemoteAPI() {
		LoginPage.loginAsAdmin(driver());
		RemoteAPIsPage page = RemoteAPIsPage.goTo(driver());
		int clientsTableSize = page.remoteApisTableSize();
		assertEquals(2, clientsTableSize);

		page.openAddRemoteModal();
		page.enterApiDetails("FOOBAR", "", "FLDSK", "https://example.com");
		page.submitCreateFormWithErrors();

		assertEquals("Should contain 1 error", 1, page.getCreateErrors().size());
		page.enterApiDetails("FOOBAR", "32dsrf32rsdf3w323", "FLDSK", "https://example.com");
		page.submitCreateForm();
		assertEquals("Should be 3 clients in table", 3, page.remoteApisTableSize());
	}
}
