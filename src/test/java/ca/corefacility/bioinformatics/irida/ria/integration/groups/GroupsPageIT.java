package ca.corefacility.bioinformatics.irida.ria.integration.groups;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.GroupsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/GroupsPageIT.xml")
public class GroupsPageIT extends AbstractIridaUIITChromeDriver {
	private GroupsPage groupsPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		groupsPage = new GroupsPage(driver());
	}

	@Test
	public void testAddGroup() {
		final String groupName = "new group";
		final String groupDesc = "this is a new group";

		groupsPage = GroupsPage.goToCreateGroupPage(driver());
		groupsPage.createGroup(groupName, groupDesc);
		groupsPage.goToGroupsPage();
		List<String> groupNames = groupsPage.getGroupNames();
		assertTrue(groupNames.contains(groupName));
	}

	@Test
	public void testAddGroupMember() {
		final int groupId = 1;
		final int anotherGroupId = 2;
		final String searchTermUc = "Te";
		final String searchTermLc = "te";
		final String role = "GROUP_MEMBER";
		groupsPage = GroupsPage.goToGroupPage(driver(), groupId);
		groupsPage.addGroupMember(searchTermUc, role);
		assertTrue("Noty success should be displayed", groupsPage.checkSuccessNotificationStatus());
		groupsPage = GroupsPage.goToGroupPage(driver(), anotherGroupId);
		groupsPage.addGroupMember(searchTermLc, role);
		assertTrue("Noty success should be displayed", groupsPage.checkSuccessNotificationStatus());
	}

	@Test
	public void testGroupDeletionWhenLinkedToProjects() {
		groupsPage = GroupsPage.goToCreateGroupPage(driver());
		groupsPage.goToGroupsPage();
		groupsPage.removeUserGroupWithProjectLinks();
		assertTrue("Noty success should be displayed", groupsPage.checkSuccessNotificationStatus());
	}
}
