package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectMembersPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectMembersPage membersPage;

	private static final ImmutableList<String> COLLABORATORS_NAMES = ImmutableList.of("Mr. Manager", "test User");

	private List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of(
					"href", "/projects",
					"text", "Projects"
			),
			ImmutableMap.of(
					"href", "/projects/" + 1,
					"text", "1"
			),
			ImmutableMap.of(
					"href", "/projects/1/members",
					"text", "Members"
			)
	);

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		membersPage = new ProjectMembersPage(driver());
	}

	@Test
	public void testPageSetUp() {
		assertEquals("Page h1 tag is properly set.", "project Members", membersPage.getTitle());
		List<String> names = membersPage.getProjectMembersNames();
		for (String name : names) {
			assertTrue("Has the correct members names", COLLABORATORS_NAMES.contains(name));
		}
		membersPage.checkBreadCrumbs(BREADCRUMBS);
	}

	@Test
	public void testRemoveUser() {
		membersPage.clickRemoveUserButton(2L);
		membersPage.clickModialPopupButton();
		List<String> userNames = membersPage.getProjectMembersNames();
		assertEquals(1, userNames.size());
	}

	@Test
	public void testEditRole() {
		Long userid = 2L;
		membersPage.clickEditButton(userid);
		assertTrue("Role select dropdowns should be visible", membersPage.roleSelectDisplayed(userid));
		membersPage.setRoleForUser(2L, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(membersPage.notySuccessDisplayed());
		assertTrue("Role span display should be visible", membersPage.roleSpanDisplayed(userid));
	}

	@Test
	public void testAddUserToProject() {
		String username = "third guy";
		membersPage.clickAddMember();
		membersPage.addUserToProject(3L, ProjectRole.PROJECT_USER);
		assertTrue("Noty success should be displayed", membersPage.notySuccessDisplayed());

		List<String> projectMembersNames = membersPage.getProjectMembersNames();
		assertTrue(projectMembersNames.contains(username));
	}

	@Test
	public void testProjectEventCreated() {
		ProjectDetailsPage detailsPage = new ProjectDetailsPage(driver());

		String username = "third guy";
		membersPage.clickAddMember();
		membersPage.addUserToProject(3L, ProjectRole.PROJECT_USER);
		detailsPage.goTo(1L);

		List<WebElement> events = detailsPage.getEvents();
		assertEquals(2, events.size());
		WebElement mostRecentEvent = events.iterator().next();
		String classes = mostRecentEvent.getAttribute("class");
		assertTrue("event should be a user-role-event", classes.contains("user-role-event"));
		assertTrue("event should contain the user name", mostRecentEvent.getText().contains(username));
	}
}
