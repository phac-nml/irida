package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectMembersPageIT extends AbstractIridaUIITChromeDriver {

	private static final ImmutableList<String> COLLABORATORS_NAMES = ImmutableList.of("Mr. Manager", "test User");

	private final List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of("href", "/projects", "text", "Projects"),
			ImmutableMap.of("href", "/projects/1", "text", "project"),
			ImmutableMap.of("href", "/projects/1/settings", "text", "Settings"));

	@Test
	public void testCanManagePageSetUp() {
		LoginPage.loginAsManager(driver());
		ProjectMembersPage page = ProjectMembersPage.goTo(driver());
		assertEquals("Check for proper translation in title", "Members", page.getPageHeaderTitle());
		assertEquals("Should be 2 members in the project", 2, page.getNumberOfMembers());
		assertTrue("Add Members button should be visible", page.isAddMemberBtnVisible());

		// Test remove user from project
		page.removeUser(1);
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Should not be able to remove the manager
		page.removeManager(0);
		assertTrue(page.isUpdateMemberErrorNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Test Add user to project
		page.addUserToProject("test");
		assertEquals("Should be 2 members in the project", 2, page.getNumberOfMembers());

		// Tye updating the users role
		page.updateUserRole(0, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
	}

	@Test
	public void testCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectMembersPage page = ProjectMembersPage.goTo(driver());
		assertFalse("Add Members button should not be visible", page.isAddMemberBtnVisible());
	}

	@Test
	public void testRemoteProjectManagerPageSetup() {
		LoginPage.loginAsAdmin(driver());

		CreateClientPage createClientPage;
		ProjectSyncPage page;

		String clientId = "myClient";
		String clientSecret;

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
		assertFalse("URL should not be empty", url.isEmpty());
		page.submitProject();

		String pathTokens[] = driver().getCurrentUrl().split("/");
		Long projectId = Long.valueOf(pathTokens[pathTokens.length-1]);

		ProjectMembersPage remoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertEquals("Should be 1 members in the project", 1, remoteProjectMembersPage.getNumberOfMembers());
		remoteProjectMembersPage.addUserToProject("Mr. Manager");
		remoteProjectMembersPage.updateUserRole(0, ProjectRole.PROJECT_OWNER.toString());
		assertEquals("Should be 2 members in the project", 2, remoteProjectMembersPage.getNumberOfMembers());

		LoginPage.loginAsManager(driver());

		ProjectDetailsPage remoteProjectDetailsPage = ProjectDetailsPage.goTo(driver(), projectId);
		String dataProjectName = remoteProjectDetailsPage.getProjectName();
		assertEquals("Should be on the remote project", dataProjectName, name);

		ProjectMembersPage managerRemoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertTrue("Add member button should be visible", managerRemoteProjectMembersPage.isAddMemberBtnVisible());

		managerRemoteProjectMembersPage.addUserToProject("testUser");
		assertEquals("Should be 3 members in the project", 3, remoteProjectMembersPage.getNumberOfMembers());
		managerRemoteProjectMembersPage.removeUser(0);
		assertEquals("Should be 2 members in the project", 2, remoteProjectMembersPage.getNumberOfMembers());
	}

}
