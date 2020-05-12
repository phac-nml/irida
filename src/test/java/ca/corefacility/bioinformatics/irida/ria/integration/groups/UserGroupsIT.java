package ca.corefacility.bioinformatics.irida.ria.integration.groups;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsListingPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/GroupsPageIT.xml")
public class UserGroupsIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testUserGroups() {
		LoginPage.loginAsManager(driver());

		// Test listing user groups
		UserGroupsListingPage listingPage = UserGroupsListingPage.initPage(driver());
		listingPage.gotoPage();
		assertEquals("Should have 2 user groups", 2, listingPage.getNumberOfExistingUserGroups());

		// Test creating a new group
		final String GROUP_NAME = "NEW_GROUP";
		listingPage.createNewUserGroup(GROUP_NAME);
		UserGroupsDetailsPage detailsPage = UserGroupsDetailsPage.initPage(driver());
		assertEquals("Should be on the new user groups page", GROUP_NAME, detailsPage.getUserGroupName());

		// Test removing a group
		listingPage.gotoPage();
		assertEquals("Should now be 3 groups", 3, listingPage.getNumberOfExistingUserGroups());
	}
}
