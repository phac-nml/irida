package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;

public class RemoteApiUtilities {
	public static void addRemoteApi(WebDriver driver) {
		CreateRemoteAPIPage page = new CreateRemoteAPIPage(driver);

		String baseUrl = page.getBaseUrl();
		String url = baseUrl + "api";

		page.createRemoteAPIWithDetails("new name", url, "testClient", "testClientSecret");
		assertTrue("client should have been created", page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = new RemoteAPIDetailsPage(driver);

		ApiStatus remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals("api status should be connected", ApiStatus.CONNECTED, remoteApiStatus);
	}
}
