package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectMembersPageIT {
	private WebDriver driver;
	private ProjectMembersPage membersPage;

	private static final ImmutableList<String> COLLABORATORS_NAMES = ImmutableList.of("Mr. Manager", "test User");

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsAdmin(driver);
		membersPage = new ProjectMembersPage(driver);
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetUp() {
		assertEquals("Page h1 tag is properly set.", "project Members", membersPage.getTitle());
		List<String> names = membersPage.getProjectMembersNames();
		for (String name : names) {
			assertTrue("Has the correct members names", COLLABORATORS_NAMES.contains(name));
		}
	}

	@Test
	public void testRemoveUser() {
		membersPage.clickRemoveUserButton(2l);
		membersPage.clickModialPopupButton();
		List<String> userNames = membersPage.getProjectMembersNames();
		assertEquals(1, userNames.size());
	}

	@Test
	public void testEditRole() {
		Long userid = 2l;
		membersPage.clickEditButton(userid);
		assertTrue("Role select dropdowns should be visible", membersPage.roleSelectDisplayed(userid));
		membersPage.setRoleForUser(2l, ProjectRole.PROJECT_OWNER.toString());
		assertTrue(membersPage.notySuccessDisplayed());
		assertTrue("Role span display should be visible", membersPage.roleSpanDisplayed(userid));
	}

	@Test
	public void testAddUserToProject() {
		String username = "third guy";
		membersPage.clickAddMember();
		membersPage.addUserToProject(3l, ProjectRole.PROJECT_USER);
		assertTrue("Noty success should be displayed", membersPage.notySuccessDisplayed());

		List<String> projectMembersNames = membersPage.getProjectMembersNames();
		assertTrue(projectMembersNames.contains(username));
	}

	@Test
	public void testProjectEventCreated() {
		ProjectDetailsPage detailsPage = new ProjectDetailsPage(driver);

		String username = "third guy";
		membersPage.clickAddMember();
		membersPage.addUserToProject(3l, ProjectRole.PROJECT_USER);
		detailsPage.goTo(1l);

		List<WebElement> events = detailsPage.getEvents();
		assertEquals(2, events.size());
		WebElement mostRecentEvent = events.iterator().next();
		String classes = mostRecentEvent.getAttribute("class");
		assertTrue("event should be a user-role-event", classes.contains("user-role-event"));
		assertTrue("event should contain the user name", mostRecentEvent.getText().contains(username));
	}
}
