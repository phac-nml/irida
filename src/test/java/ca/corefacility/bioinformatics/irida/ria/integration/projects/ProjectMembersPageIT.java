package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Should not be able to remove the manager
		page.removeManager(0);
		assertTrue(page.isUpdateMemberErrorNotificationDisplayed());
		assertEquals("Should be 1 member in the project", 1, page.getNumberOfMembers());

		// Test Add user to project
		page.addUserToProject("test");
		assertEquals("Should be 2 members in the project", 2, page.getNumberOfMembers());

		// Tye updating the users role
		page.updateUserRole(0, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(page.isUpdateMemberSuccessNotificationDisplayed());
	}
}
