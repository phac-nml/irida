package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminClientsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectUserGroupsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectUserGroupsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testPageAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertFalse(page.isAddUserGroupButtonVisible(),
				"Collaborators should not be able to add a new group to the project");
	}

	@Test
	public void testPageAsAManager() {
		LoginPage.loginAsManager(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertTrue(page.isAddUserGroupButtonVisible(), "Managers can see the add user groups button.");
		assertEquals(0, page.getNumberOfUserGroups(), "Should be no user groups");
		page.addUserGroup("group 1");
		assertEquals(1, page.getNumberOfUserGroups(), "Should be one user groups");

		page.removeUserGroups(0);
		assertEquals(0, page.getNumberOfUserGroups(), "Should be no user groups");
	}

	@Test
	public void testRemoteProjectPageAsAManager() {
		LoginPage.loginAsAdmin(driver());

		AdminClientsPage clientsPage;
		ProjectSyncPage page;

		String clientId = "myClient";
		String clientSecret;

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

		String[] pathTokens = driver().getCurrentUrl().split("/");
		Long projectId = Long.valueOf(pathTokens[pathTokens.length - 1]);

		ProjectMembersPage remoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertEquals(1, remoteProjectMembersPage.getNumberOfMembers(), "Should be 1 members in the project");
		remoteProjectMembersPage.addUserToProject("Mr. Manager", ProjectRole.PROJECT_OWNER.toString());
		assertEquals(2, remoteProjectMembersPage.getNumberOfMembers(), "Should be 2 members in the project");

		LoginPage.loginAsManager(driver());
		ProjectUserGroupsPage projectUserGroupsPage = ProjectUserGroupsPage.goToPage(driver(), projectId);
		assertTrue(projectUserGroupsPage.isAddUserGroupButtonVisible(), "Managers can see the add user groups button.");
		assertEquals(0, projectUserGroupsPage.getNumberOfUserGroups(), "Should be no user groups");
		projectUserGroupsPage.addUserGroup("group 1");
		assertEquals(1, projectUserGroupsPage.getNumberOfUserGroups(), "Should be one user groups");

		projectUserGroupsPage.removeUserGroups(0);
		assertEquals(0, projectUserGroupsPage.getNumberOfUserGroups(), "Should be no user groups");
	}
}
