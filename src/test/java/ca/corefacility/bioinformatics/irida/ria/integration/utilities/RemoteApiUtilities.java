package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RemoteApiUtilities {
	protected static String BASE_URL;

	public static void setBaseUrl(String baseUrl) {
		BASE_URL = baseUrl;
	}

	public static void addRemoteApi(WebDriver driver, String clientId, String clientSecret) {
		RemoteAPIsPage page = RemoteAPIsPage.goTo(driver);

		String baseUrl = page.getBaseUrl();
		String url = baseUrl + "api";

		page.createRemoteApi("new name", clientId, clientSecret, url);

		RemoteAPIDetailsPage remoteAPIDetailsPage = RemoteAPIDetailsPage.gotoDetailsPage(driver);

		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		assertTrue(remoteAPIDetailsPage.isRemoteAPIConnected(), "api status should be connected");
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
