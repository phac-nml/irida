package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RemoteApiUtilities {
	protected static final String BASE_URL =
			System.getProperty("server.base.url", "http://localhost:" + System.getProperty("server.port", "8080")) + "/";

	public static void addRemoteApi(WebDriver driver, String clientId, String clientSecret) {
		CreateRemoteAPIPage page = new CreateRemoteAPIPage(driver);

		String baseUrl = page.getBaseUrl();
		String url = baseUrl + "api";

		page.createRemoteAPIWithDetails("new name", url, clientId, clientSecret);

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
