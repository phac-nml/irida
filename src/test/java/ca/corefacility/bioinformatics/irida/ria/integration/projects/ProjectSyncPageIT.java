package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminClientsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSyncPageIT extends AbstractIridaUIITChromeDriver {

	AdminClientsPage clientsPage;
	ProjectSyncPage page;

	String clientId = "myClient";
	String clientSecret;

	@Test
	public void testSyncProject() {
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
		clientsPage = AdminClientsPage.goTo(driver());
		clientsPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, AdminClientsPage.READ_YES,
				AdminClientsPage.WRITE_NO);
		clientSecret = clientsPage.getClientSecret(clientId);

		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
		page = ProjectSyncPage.goTo(driver());
		page.selectApi(0);
		final String name = "project";
		page.selectProjectInListing(name);

		String url = page.getProjectUrl();
		assertFalse(url.isEmpty(), "URL should not be empty");
		page.submitProject();

		ProjectDetailsPage projectDetailsPage = ProjectDetailsPage.initElements(driver());
		String dataProjectName = projectDetailsPage.getProjectName();
		assertEquals(dataProjectName, name, "Should be on the remote project page");
	}

	@Test
	public void testSyncProjectNotExist() {
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
		clientsPage = AdminClientsPage.goTo(driver());
		clientsPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, AdminClientsPage.READ_YES,
				AdminClientsPage.WRITE_NO);
		clientSecret = clientsPage.getClientSecret(clientId);
		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
		LoginPage.logout(driver());

		LoginPage.loginAsManager(driver());

		RemoteAPIsPage remoteAPIsPage = RemoteAPIsPage.goToUserPage(driver());
		remoteAPIsPage.connectToRemoteAPI("new name");

		page = ProjectSyncPage.goTo(driver());
		page.selectApi(0);
		final String name = "project";
		page.selectProjectInListing(name);

		page.clickSetUrlManuallyCheckbox();
		String url = page.getProjectUrl() + "11";
		page.setRemoteProjectUrl(url);
		page.submitProject();

		// The project does not exist so a resource not found error should be displayed
		assertTrue(page.isResourceNotFoundErrorMessageDisplayed());
	}

	@Test
	public void testSyncProjectAccessDenied() {
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
		clientsPage = AdminClientsPage.goTo(driver());
		clientsPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, AdminClientsPage.READ_YES,
				AdminClientsPage.WRITE_NO);
		clientSecret = clientsPage.getClientSecret(clientId);
		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
		LoginPage.logout(driver());

		LoginPage.loginAsManager(driver());

		RemoteAPIsPage remoteAPIsPage = RemoteAPIsPage.goToUserPage(driver());
		remoteAPIsPage.connectToRemoteAPI("new name");
		page = ProjectSyncPage.goTo(driver());
		page.selectApi(0);
		final String name = "project";
		page.selectProjectInListing(name);

		page.clickSetUrlManuallyCheckbox();
		String url = page.getProjectUrl().replaceFirst("[^/]*$", "9");
		page.setRemoteProjectUrl(url);
		page.submitProject();

		// The project exists but the currently logged in user is not on the project, so an access denied error should be displayed
		assertTrue(page.isAccessDeniedErrorMessageDisplayed());
	}
}
