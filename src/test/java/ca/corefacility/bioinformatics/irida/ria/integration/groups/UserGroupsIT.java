package ca.corefacility.bioinformatics.irida.ria.integration.groups;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsListingPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/GroupsPageIT.xml")
public class UserGroupsIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testUserGroupsAsManager() {
		LoginPage.loginAsManager(driver());

		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoPage();
		assertEquals("Should have 2 user groups", 2, listingPage.getNumberOfExistingUserGroups());

		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertFalse("Does not redirect to admin panel user details page", driver().getCurrentUrl().contains("/admin/groups"));
		assertTrue("Redirects user to main app user details page", driver().getCurrentUrl().contains("/groups"));

		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals("Should be on the new user groups page", GROUP_NAME, detailsPage.getUserGroupName());

		// Test adding a group member
		assertEquals("Should be 1 group member", 1, detailsPage.getNumberOfMembers());
		detailsPage.addGroupMember("third", "Collaborator");
		assertEquals("Should be 2 group members", 2, detailsPage.getNumberOfMembers());

		// Test updating group name
		final String UPDATED_NAME = "FOOBAR";
		detailsPage.updateUserGroupName(UPDATED_NAME);
		assertEquals("Name should have been properly changed", UPDATED_NAME, detailsPage.getUserGroupName());

		listingPage.gotoPage();
		assertEquals("Should now be 3 groups", 3, listingPage.getNumberOfExistingUserGroups());

		// Test removing a group
		detailsPage.gotoPage(2);
		final String PRE_DELETION_URL = driver().getCurrentUrl();
		detailsPage.deleteGroup();
		listingPage.validateRouteChange(PRE_DELETION_URL);
		assertFalse("Does not redirect to admin panel user groups page", driver().getCurrentUrl().endsWith("/admin/groups"));
		assertTrue("Redirects user to main app user groups page", driver().getCurrentUrl().endsWith("/groups"));
		assertEquals("Should have 2 user groups", 2, listingPage.getNumberOfExistingUserGroups());
	}

	@Test
	public void testUserGroupsAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoAdminPage();
		assertEquals("Should have 2 user groups", 2, listingPage.getNumberOfExistingUserGroups());

		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		final String PRE_CREATION_URL = driver().getCurrentUrl();
		listingPage.createNewUserGroup(GROUP_NAME);
		listingPage.validateRouteChange(PRE_CREATION_URL);
		assertTrue("Redirects user to main app user details page", driver().getCurrentUrl().contains("/admin/groups"));

		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals("Should be on the new user groups page", GROUP_NAME, detailsPage.getUserGroupName());

		// Test adding a group member
		assertEquals("Should be 1 group member", 1, detailsPage.getNumberOfMembers());
		detailsPage.addGroupMember("third", "Collaborator");
		assertEquals("Should be 2 group members", 2, detailsPage.getNumberOfMembers());

		// Test updating group name
		final String UPDATED_NAME = "FOOBAR";
		detailsPage.updateUserGroupName(UPDATED_NAME);
		assertEquals("Name should have been properly changed", UPDATED_NAME, detailsPage.getUserGroupName());

		listingPage.gotoAdminPage();
		assertEquals("Should now be 3 groups", 3, listingPage.getNumberOfExistingUserGroups());

		// Test removing a group
		detailsPage.gotoAdminPage(2);
		final String PRE_DELETION_URL = driver().getCurrentUrl();
		detailsPage.deleteGroup();
		listingPage.validateRouteChange(PRE_DELETION_URL);
		assertTrue("Redirects user to admin panel user groups page", driver().getCurrentUrl().endsWith("/admin/groups"));
		assertEquals("Should have 2 user groups", 2, listingPage.getNumberOfExistingUserGroups());
	}
}
