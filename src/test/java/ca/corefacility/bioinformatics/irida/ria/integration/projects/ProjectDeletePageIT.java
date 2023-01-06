package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDeletePage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for deleting a project
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDeletePageIT extends AbstractIridaUIITChromeDriver {

	private static final Long PROJECT_ID = 6L;
	private static final String PROJECT_NAME = "project ABCD";

	private ProjectDeletePage page;

	@Test
	public void deleteProjectAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		ProjectsPage projectsPage = ProjectsPage.goToProjectsPage(driver(), true);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals(1, projectsPage.getNumberOfProjects(), "should be 1 project with name " + PROJECT_NAME);

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse(page.canClickDelete(), "delete button should be disabled");

		page.clickConfirm();

		assertTrue(page.canClickDelete(), "delete button should be enabled");

		page.deleteProject();

		projectsPage = ProjectsPage.goToProjectsPage(driver(), true);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals(0, projectsPage.getNumberOfProjects(), "project should no longer exist");
	}

	@Test
	public void deleteProjectAsOwner() {
		LoginPage.loginAsManager(driver());

		ProjectsPage projectsPage = ProjectsPage.goToProjectsPage(driver(), false);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals(projectsPage.getNumberOfProjects(), 1, "should be 1 project with name " + PROJECT_NAME);

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse(page.canClickDelete(), "delete button should be disabled");

		page.clickConfirm();

		assertTrue(page.canClickDelete(), "delete button should be enabled");

		page.deleteProject();

		projectsPage = ProjectsPage.goToProjectsPage(driver(), false);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals(0, projectsPage.getNumberOfProjects(), "project should no longer exist");
	}

}
