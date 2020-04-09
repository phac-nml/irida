package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;

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

	private List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of("href", "/projects", "text", "Projects"),
			ImmutableMap.of("href", "/projects/1", "text", "project"),
			ImmutableMap.of("href", "/projects/1/settings", "text", "Settings"));

	@Test
	public void testCanManagePageSetUp() {
		LoginPage.loginAsManager(driver());
		ProjectMembersPage page = ProjectMembersPage.goTo(driver());
		assertEquals("Check for proper translation in title", "Members", page.getPageHeaderTitle());
		assertEquals("Should be 2 members in the project", 2, page.getNumberOfMembers());

		// Test remove user from project
		page.removeUser(1);
		assertTrue(page.isRemoveMemberSuccessNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Should not be able to remove the manager
		page.removeManager(0);
		assertTrue(page.isRemoveMemberErrorNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Test Add user to project
		page.addUserToProject("test");
	}

//	@Test
//	public void testEditRole() {
//		membersPage.setRoleForUser(2L, ProjectRole.PROJECT_OWNER.toString());
//		assertTrue("should display success message after updating role.", membersPage.checkSuccessNotification());
//	}
//
//	@Test
//	public void testAddUserToProject() {
//		String username = "third guy";
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(username, ProjectRole.PROJECT_USER);
//		assertTrue("Noty success should be displayed", membersPage.checkSuccessNotification());
//
//		List<String> projectMembersNames = membersPage.getProjectMembersNames();
//		assertTrue(projectMembersNames.contains(username));
//	}
//
//	@Test
//	public void testGroupManagement() {
//		final String groupName = "group 1";
//		membersPage.goToGroupsPage();
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(groupName, ProjectRole.PROJECT_USER);
//		assertTrue("Noty success should be displayed", membersPage.checkSuccessNotification());
//		membersPage.setRoleForUser(1L, ProjectRole.PROJECT_OWNER.toString());
//		assertTrue("should display success message after updating role.", membersPage.checkSuccessNotification());
//		membersPage.clickRemoveUserButton(1L);
//		membersPage.clickModalPopupButton();
//		List<String> groupNames = membersPage.getProjectMembersNames();
//		assertEquals("should be no groups left after clicking delete.", 0, groupNames.size());
//	}
//
//	@Test
//	public void testGroupManagementProjectManager() {
//		final String groupName = "group 1";
//		String username = "third guy";
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(username, ProjectRole.PROJECT_OWNER);
//		assertTrue("Noty success should be displayed", membersPage.checkSuccessNotification());
//		LoginPage.logout(driver());
//		LoginPage.loginAsAnotherUser(driver());
//		membersPage.goToGroupsPage();
//		assertTrue("Add Group button should be displayed", membersPage.addGroupButtonDisplayed());
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(groupName, ProjectRole.PROJECT_USER);
//		/*
//			As the user is a manager on the project they should have the
//			ability to add user groups to the project
//		*/
//		assertTrue("Noty success should be displayed", membersPage.checkSuccessNotification());
//	}
//
//	@Test
//	public void testGroupManagementProjectUser() {
//		String username = "third guy";
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(username, ProjectRole.PROJECT_USER);
//		assertTrue("Noty success should be displayed", membersPage.checkSuccessNotification());
//		LoginPage.logout(driver());
//
//		LoginPage.loginAsAnotherUser(driver());
//		membersPage.goToGroupsPage();
//		/*
//			As the user is a collaborator on the project they should not have
//			ability to add user groups to the project
//		*/
//		assertFalse("Add Group button should not be displayed", membersPage.addGroupButtonDisplayed());
//	}
//
//	@Test
//	public void testProjectEventCreated() {
//		ProjectDetailsPage detailsPage = new ProjectDetailsPage(driver());
//
//		String username = "third guy";
//		membersPage.clickAddMember();
//		membersPage.addUserToProject(username, ProjectRole.PROJECT_USER);
//		detailsPage.goTo(1L);
//
//		List<WebElement> events = detailsPage.getEvents();
//		assertEquals(2, events.size());
//		WebElement mostRecentEvent = events.iterator()
//				.next();
//		String classes = mostRecentEvent.getAttribute("class");
//		assertTrue("event should be a user-role-event", classes.contains("user-role-event"));
//		assertTrue("event should contain the user name", mostRecentEvent.getText()
//				.contains(username));
//	}
}
