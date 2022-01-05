package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectUserGroupsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectUserGroupsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testPageAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertFalse("Collaborators should not be able to add a new group to the project",
				page.isAddUserGroupButtonVisible());
	}

	@Test
	public void testPageAsAManager() {
		LoginPage.loginAsManager(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertTrue("Managers can see the add user groups button.", page.isAddUserGroupButtonVisible());
		assertEquals("Should be no user groups", 0, page.getNumberOfUserGroups());
		page.addUserGroup("group 1");
		assertEquals("Should be one user groups", 1, page.getNumberOfUserGroups());

		page.removeUserGroups(0);
		assertEquals("Should be no user groups", 0, page.getNumberOfUserGroups());
	}

	@Test
	public void testRemoteProjectPageAsAManager() {
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
		ProjectUserGroupsPage projectUserGroupsPage = ProjectUserGroupsPage.goToPage(driver(), projectId);
		assertTrue("Managers can see the add user groups button.", projectUserGroupsPage.isAddUserGroupButtonVisible());
		assertEquals("Should be no user groups", 0, projectUserGroupsPage.getNumberOfUserGroups());
		projectUserGroupsPage.addUserGroup("group 1");
		assertEquals("Should be one user groups", 1, projectUserGroupsPage.getNumberOfUserGroups());

		projectUserGroupsPage.removeUserGroups(0);
		assertEquals("Should be no user groups", 0, projectUserGroupsPage.getNumberOfUserGroups());
	}
}
