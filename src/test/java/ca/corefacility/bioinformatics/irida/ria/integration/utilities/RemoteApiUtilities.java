package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoteApiUtilities {
	protected static final String BASE_URL =
			System.getProperty("server.base.url", "http://localhost:" + System.getProperty("jetty.port", "8080")) + "/";

	public static void addRemoteApi(WebDriver driver, String clientId, String clientSecret) {
		CreateRemoteAPIPage page = new CreateRemoteAPIPage(driver);

		String baseUrl = page.getBaseUrl();
		String url = baseUrl + "api";

		page.createRemoteAPIWithDetails("new name", url, clientId, clientSecret);
		assertTrue("client should have been created", page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = new RemoteAPIDetailsPage(driver);

		ApiStatus remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals("api status should be connected", ApiStatus.CONNECTED, remoteApiStatus);
	}

	/**
	 * Get the location of the OAuth redirect URI for this test server.
	 *
	 * @return The oauth/authorization/token location for this server
	 */
	public static String getRedirectLocation() {
		return BASE_URL + "api/oauth/authorization/token";
	}
}
