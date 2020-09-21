package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectRemoteSettingsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.jsonldjava.shaded.com.google.common.collect.ImmutableList;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * <p>
 * Integration test to ensure that the Remote Project Synchronization Settings page
 * is displayed with the correct elements.
 * </p>
 */

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectRemoteSettingsIT extends AbstractIridaUIITChromeDriver {
	CreateClientPage createClientPage;
	ProjectSyncPage page;

	String clientId = "myClient";
	String clientSecret;

	@Test
	public void testRemoteProjectSettings() {
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation
				();
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
		assertFalse("URL should not be empty", url.isEmpty());
		page.submitProject();

		ProjectDetailsPage projectDetailsPage = ProjectDetailsPage.initElements(driver());
		String dataProjectName = projectDetailsPage.getProjectName();
		assertEquals("Should be on the remote project page", dataProjectName, name);

		ProjectRemoteSettingsPage remoteSettingsPage = ProjectRemoteSettingsPage.initElements(driver());
		final Long projectId = remoteSettingsPage.getProjectId();
		remoteSettingsPage.goTo(driver(), projectId);

		checkTranslations(remoteSettingsPage, ImmutableList.of("project-remote"), "Project Synchronization Settings");

		assertTrue("Sync Now button should be displayed", remoteSettingsPage.syncNowButtonDisplayed());

		// Remote project is marked for synchronization so the sync now button should be disabled
		assertFalse("Sync now button should be enabled", remoteSettingsPage.syncNowButtonEnabled());

		assertTrue("Sync frequency select dropdown should be displayed", remoteSettingsPage.syncFrequencySelectDisplayed());
		assertTrue("Become Sync User button should not be on the page", remoteSettingsPage.syncUserButtonNotDisplayed());
	}
}
