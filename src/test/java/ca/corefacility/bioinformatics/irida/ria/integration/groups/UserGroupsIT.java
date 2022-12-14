package ca.corefacility.bioinformatics.irida.ria.integration.groups;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsListingPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectUserGroupsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/GroupsPageIT.xml")
public class UserGroupsIT extends AbstractIridaUIITChromeDriver {

	@Test
	@Disabled
	public void testUserGroupsAsManager() {
		LoginPage.loginAsManager(driver());

		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoPage();
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");

		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertFalse(driver().getCurrentUrl().contains("/admin/groups"), "Does not redirect to admin panel user details page");
		assertTrue(driver().getCurrentUrl().contains("/groups"), "Redirects user to main app user details page");

		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals(GROUP_NAME, detailsPage.getUserGroupName(), "Should be on the new user groups page");

		// Test adding a group member
		assertEquals(1, detailsPage.getNumberOfMembers(), "Should be 1 group member");
		detailsPage.addGroupMember("third", "Collaborator");
		assertEquals(2, detailsPage.getNumberOfMembers(), "Should be 2 group members");

		// Test updating group name
		final String UPDATED_NAME = "FOOBAR";
		detailsPage.updateUserGroupName(UPDATED_NAME);
		assertEquals(UPDATED_NAME, detailsPage.getUserGroupName(), "Name should have been properly changed");

		listingPage.gotoPage();
		assertEquals(3, listingPage.getNumberOfExistingUserGroups(), "Should now be 3 groups");

		// Test removing a group
		detailsPage.gotoPage(2);
		final String PRE_DELETION_URL = driver().getCurrentUrl();
		detailsPage.deleteGroup();
		listingPage.validateRouteChange(PRE_DELETION_URL);
		assertFalse(driver().getCurrentUrl().endsWith("/admin/groups"), "Does not redirect to admin panel user groups page");
		assertTrue(driver().getCurrentUrl().endsWith("/groups"), "Redirects user to main app user groups page");
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");
	}

	@Test
	@Disabled
	public void testUserGroupsAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoAdminPage();
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");

		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertTrue(driver().getCurrentUrl().contains("/admin/groups"), "Redirects user to main app user details page");

		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals(GROUP_NAME, detailsPage.getUserGroupName(), "Should be on the new user groups page");

		// Test adding a group member
		assertEquals(1, detailsPage.getNumberOfMembers(), "Should be 1 group member");
		detailsPage.addGroupMember("third", "Collaborator");
		assertEquals(2, detailsPage.getNumberOfMembers(), "Should be 2 group members");

		// Test updating group name
		final String UPDATED_NAME = "FOOBAR";
		detailsPage.updateUserGroupName(UPDATED_NAME);
		assertEquals(UPDATED_NAME, detailsPage.getUserGroupName(), "Name should have been properly changed");

		listingPage.gotoAdminPage();
		assertEquals(3, listingPage.getNumberOfExistingUserGroups(), "Should now be 3 groups");

		// Test removing a group
		detailsPage.gotoAdminPage(2);
		final String PRE_DELETION_URL = driver().getCurrentUrl();
		detailsPage.deleteGroup();
		listingPage.validateRouteChange(PRE_DELETION_URL);
		assertTrue(driver().getCurrentUrl().endsWith("/admin/groups"), "Redirects user to admin panel user groups page");
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");
	}

	@Test
	public void testAddGroupMemberWhenManagerOnMemberProjectAsCollaborator() {
		Long PROJECT_ID = 9L;
		// Login as a user and add manager as collaborator on project
		LoginPage.loginAsUser(driver());
		ProjectMembersPage projectMembersPage = ProjectMembersPage.goToRemoteProject(driver(), PROJECT_ID);
		// Add manager as collaborator on project
		projectMembersPage.addUserToProject("mrtest");
		LoginPage.logout(driver());

		// Login as manager and create a new group
		LoginPage.loginAsManager(driver());
		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoPage();
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");
		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertTrue(driver().getCurrentUrl().contains("/groups"), "Redirects user to main app user details page");
		String currUrl = driver().getCurrentUrl();
		int NEW_GROUP_ID = Integer.parseInt(driver().getCurrentUrl().substring(currUrl.lastIndexOf("/") + 1));
		LoginPage.logout(driver());

		// Login as user and add this new group to the project
		LoginPage.loginAsUser(driver());
		ProjectUserGroupsPage projectUserGroupsPage = ProjectUserGroupsPage.goToPage(driver(), PROJECT_ID);
		projectUserGroupsPage.addUserGroup(GROUP_NAME);
		LoginPage.logout(driver());

		// Login as manager and add user to the new group
		LoginPage.loginAsManager(driver());
		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		detailsPage.gotoPage(NEW_GROUP_ID);
		assertEquals(GROUP_NAME, detailsPage.getUserGroupName(), "Should be on the new user groups page");
		assertEquals(1, detailsPage.getNumberOfMembers(), "Should be 1 group member");
		detailsPage.addGroupMember("testUser", "GROUP_MEMBER");
		assertEquals(2, detailsPage.getNumberOfMembers(), "Should be 2 group members");
	}

	@Test
	public void testAddUserGroupMemberWithSelectedRole() {
		LoginPage.loginAsManager(driver());

		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoPage();
		assertEquals(2, listingPage.getNumberOfExistingUserGroups(), "Should have 2 user groups");

		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertFalse(driver().getCurrentUrl().contains("/admin/groups"), "Does not redirect to admin panel user details page");
		assertTrue(driver().getCurrentUrl().contains("/groups"), "Redirects user to main app user details page");

		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals(GROUP_NAME, detailsPage.getUserGroupName(), "Should be on the new user groups page");
		assertEquals(1, detailsPage.getNumberOfMembers(), "Should be 1 group member");
		assertEquals("Owner", detailsPage.getUserGroupMemberRole(0));

		// Add as group user
		detailsPage.addGroupMember("testUser", "GROUP_MEMBER");
		assertEquals(2, detailsPage.getNumberOfMembers(), "Should be 2 group members");
		assertEquals("Member", detailsPage.getUserGroupMemberRole(1));

		// Add as group owner
		detailsPage.addGroupMember("testUser2", "GROUP_OWNER");
		assertEquals(3, detailsPage.getNumberOfMembers(), "Should be 3 group members");
		assertEquals("Owner", detailsPage.getUserGroupMemberRole(2));
	}
}
