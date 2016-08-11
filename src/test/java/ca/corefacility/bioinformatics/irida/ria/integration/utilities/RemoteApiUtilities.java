package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;

public class RemoteApiUtilities {
	private static final Logger logger = LoggerFactory.getLogger(RemoteApiUtilities.class);

	public static void addRemoteApi(WebDriver driver) {
		CreateRemoteAPIPage page = new CreateRemoteAPIPage(driver);

		String applicationPort = page.getApplicationPort();
		String url = "http://localhost:" + applicationPort + "/api";

		logger.debug("ABOUT TO CREATE REMOTE API WITH DETAILS");
		page.createRemoteAPIWithDetails("new name", url, "testClient", "testClientSecret");
		assertTrue("client should have been created", page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = new RemoteAPIDetailsPage(driver);

		ApiStatus remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();

		logger.debug("REMOTE API UTILTIES: CLICKING CONNECT");
		remoteAPIDetailsPage.clickConnect();
		logger.debug("REMOTE API UTILITIES: CENNECT CLICKED");
		remoteAPIDetailsPage.clickAuthorize();
		logger.debug("AUTHORIZE CLICKED");

		remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals("api status should be connected", ApiStatus.CONNECTED, remoteApiStatus);
	}
}
