package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectRemoteSettingsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Remote Project Synchronization Settings page
 * is displayed with the correct elements.
 * </p>
 */

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectRemoteSettingsIT extends AbstractIridaUIITChromeDriver {
	ProjectSyncPage page;

	String clientId = "myClient";
	String clientSecret;

	@Test
	public void testRemoteProjectSettings() {
		CreateClientPage createClientPage;
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
		createClientPage = new CreateClientPage(driver());
		createClientPage.goTo();
		createClientPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, true, false);
		ClientDetailsPage detailsPage = new ClientDetailsPage(driver());
		clientSecret = detailsPage.getClientSecret();

		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
		page = ProjectSyncPage.goTo(driver());
		page.selectApi(0);
		final String name = "project";

		page.selectProjectInListing(name);

		String url = page.getProjectUrl();
		assertFalse(url.isEmpty(), "URL should not be empty");
		page.submitProject();

		ProjectSamplesPage samplesPage = ProjectSamplesPage.initPage(driver());
		assertEquals(name, samplesPage.getProjectName(), "Should have the correct project name");

		ProjectRemoteSettingsPage remoteSettingsPage = ProjectRemoteSettingsPage.initElements(driver());
		final Long projectId = remoteSettingsPage.getProjectId();
		ProjectRemoteSettingsPage.goTo(driver(), projectId);

		assertTrue(remoteSettingsPage.syncNowButtonDisplayed(), "Sync Now button should be displayed");

		// Remote project is marked for synchronization so the sync now button should be disabled
		assertFalse(remoteSettingsPage.syncNowButtonEnabled(), "Sync now button should be enabled");

		assertTrue(remoteSettingsPage.syncFrequencySelectDisplayed(), "Sync frequency select dropdown should be displayed");
		assertTrue(remoteSettingsPage.syncUserButtonNotDisplayed(), "Become Sync User button should not be on the page");
	}
}
