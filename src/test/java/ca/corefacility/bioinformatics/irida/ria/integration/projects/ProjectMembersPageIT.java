package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminClientsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectMembersPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testCanManagePageSetUp() {
		LoginPage.loginAsManager(driver());
		ProjectMembersPage page = ProjectMembersPage.goTo(driver());
		assertEquals("Members", page.getPageHeaderTitle(), "Check for proper translation in title");
		assertEquals(10, page.getNumberOfMembers(), "Should be 10 members in the project");
		assertTrue(page.isAddMemberBtnVisible(), "Add Members button should be visible");

		// Ensure that paging works.
		page.goToNextTablePage();
		assertEquals(1, page.getNumberOfMembers(), "Should be on page 1 of the table");

		page.goToPreviousTablePage();
		assertEquals(10, page.getNumberOfMembers(), "Should be on page 10 of the table");

		// Test remove user from project
		page.removeUser(1);
		assertTrue(page.isNotificationDisplayed());
		page.removeUser(3);
		assertEquals(9, page.getNumberOfMembers(), "Should be 9 members in the project");

		// Test sorting by name
		page.sortByNameColumn();
		assertEquals(9, page.getNumberOfMembers(), "Should be 9 members in the project");

		// Should not be able to remove the manager so the remove button should be disabled
		page.searchByUsername("Mr. Manager");
		assertEquals(1, page.getNumberOfMembers(), "Should be 1 member after filtering");
		// A manager has a metadata role of Level 4 and cannot be modified
		assertFalse(page.userMetadataRoleSelectEnabled(0));
		assertFalse(page.lastManagerRemoveButtonEnabled(0));
		page.searchByUsername("");
		assertEquals(9, page.getNumberOfMembers(), "Should be 9 members in the project");

		// Test Add user to project
		page.addUserToProject("twelfth", ProjectRole.PROJECT_USER.toString());
		assertEquals(10, page.getNumberOfMembers(), "Should be 10 members in the project");

		// Try updating the users role to owner
		page.updateUserRole(1, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(page.isNotificationDisplayed());

		// Change first manager back to a collaborator
		page.updateUserRole(1, ProjectRole.PROJECT_USER.toString());
		assertTrue(page.isNotificationDisplayed());

		// Try updating the users metadata role
		page.updateMetadataRole(1, ProjectMetadataRole.LEVEL_2.toString());
		assertTrue(page.isNotificationDisplayed());
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

		AdminClientsPage clientsPage = AdminClientsPage.goTo(driver());
		ProjectSyncPage page;

		String clientId = "myClient";
		String clientSecret;

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
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
		assertTrue(remoteProjectMembersPage.isNotificationDisplayed());
		assertEquals(2, remoteProjectMembersPage.getNumberOfMembers(), "Should be 2 members in the project");

		LoginPage.loginAsManager(driver());

		ProjectDetailsPage remoteProjectDetailsPage = ProjectDetailsPage.goTo(driver(), projectId);
		String dataProjectName = remoteProjectDetailsPage.getProjectName();
		assertEquals(dataProjectName, name, "Should be on the remote project");

		ProjectMembersPage managerRemoteProjectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), projectId);
		assertTrue(managerRemoteProjectMembersPage.isAddMemberBtnVisible(), "Add member button should be visible");

		managerRemoteProjectMembersPage.addUserToProject("testUser", ProjectRole.PROJECT_USER.toString());
		assertTrue(remoteProjectMembersPage.isNotificationDisplayed());
		assertEquals(3, remoteProjectMembersPage.getNumberOfMembers(), "Should be 3 members in the project");
		managerRemoteProjectMembersPage.removeUser(1);
		assertTrue(remoteProjectMembersPage.isNotificationDisplayed());
		assertEquals(2, remoteProjectMembersPage.getNumberOfMembers(), "Should be 2 members in the project");
	}

}
