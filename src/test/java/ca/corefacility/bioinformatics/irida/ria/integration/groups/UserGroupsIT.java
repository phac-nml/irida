package ca.corefacility.bioinformatics.irida.ria.integration.groups;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.groups.UserGroupsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/GroupsPageIT.xml")
public class UserGroupsIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testAddGroupMember() {
		LoginPage.loginAsManager(driver());
		final int groupId = 1;
		final String searchTermUc = "Te";
		final String searchTermLc = "third";
		final String role = "GROUP_MEMBER";

		UserGroupsPage userGroupsPage = UserGroupsPage.goToGroupsPage(driver());
		userGroupsPage.goToGroupPage(driver(), groupId);
		int initialCount = userGroupsPage.getNumberOfMembers();
		userGroupsPage.addGroupMember(searchTermUc, role);
		assertEquals("Should have another member on the project", initialCount + 1, userGroupsPage.getNumberOfMembers());

		userGroupsPage.addGroupMember(searchTermLc, role);
		assertEquals("Should be another group member", initialCount + 2, userGroupsPage.getNumberOfMembers());
	}
}
