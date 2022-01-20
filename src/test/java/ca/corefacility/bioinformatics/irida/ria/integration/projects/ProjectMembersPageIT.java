package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.*;

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
		assertEquals("Members", page.getPageHeaderTitle(), "Check for proper translation in title");
		assertEquals(2, page.getNumberOfMembers(), "Should be 2 members in the project");
		assertTrue(page.isAddMemberBtnVisible(), "Add Members button should be visible");

		// Test remove user from project
		page.removeUser(1);
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
		assertEquals(1, page.getNumberOfMembers(), "Should be 1 member in the project");

		// Should not be able to remove the manager
		page.removeManager(0);
		assertTrue(page.isUpdateMemberErrorNotificationDisplayed());
		assertEquals(1, page.getNumberOfMembers(), "Should be 1 member in the project");

		// Test Add user to project
		page.addUserToProject("test");
		assertEquals(2, page.getNumberOfMembers(), "Should be 2 members in the project");

		// Tye updating the users role
		page.updateUserRole(0, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
	}

	@Test
	public void testCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectMembersPage page = ProjectMembersPage.goTo(driver());
		assertFalse(page.isAddMemberBtnVisible(), "Add Members button should not be visible");
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
		assertFalse(url.isEmpty(), "URL should not be empty");
		page.submitProject();

		String pathTokens[] = driver().getCurrentUrl().split("/");
		Long projectId = Long.valueOf(pathTokens[pathTokens.length-1]);

		ProjectMembersPage remoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertEquals(1, remoteProjectMembersPage.getNumberOfMembers(), "Should be 1 members in the project");
		remoteProjectMembersPage.addUserToProject("Mr. Manager");
		remoteProjectMembersPage.updateUserRole(0, ProjectRole.PROJECT_OWNER.toString());
		assertEquals(2, remoteProjectMembersPage.getNumberOfMembers(), "Should be 2 members in the project");

		LoginPage.loginAsManager(driver());

		ProjectDetailsPage remoteProjectDetailsPage = ProjectDetailsPage.goTo(driver(), projectId);
		String dataProjectName = remoteProjectDetailsPage.getProjectName();
		assertEquals(dataProjectName, name, "Should be on the remote project");

		ProjectMembersPage managerRemoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertTrue(managerRemoteProjectMembersPage.isAddMemberBtnVisible(), "Add member button should be visible");

		managerRemoteProjectMembersPage.addUserToProject("testUser");
		assertEquals(3, remoteProjectMembersPage.getNumberOfMembers(), "Should be 3 members in the project");
		managerRemoteProjectMembersPage.removeUser(0);
		assertEquals(2, remoteProjectMembersPage.getNumberOfMembers(), "Should be 2 members in the project");
	}

}
