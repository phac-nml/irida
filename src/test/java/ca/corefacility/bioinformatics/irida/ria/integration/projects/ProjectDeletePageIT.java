package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDeletePage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * Test class for deleting a project
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDeletePageIT extends AbstractIridaUIITChromeDriver {

	private static Long PROJECT_ID = 6L;
	private static String PROJECT_NAME = "project ABCD";

	private ProjectDeletePage page;

	@Test
	public void deleteProjectAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		ProjectsPage projectsPage = ProjectsPage.goToProjectsPage(driver(), true);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals("should be 1 project with name " + PROJECT_NAME, 1, projectsPage.getNumberOfProjects());

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse("delete button should be disabled", page.canClickDelete());

		page.clickConfirm();

		assertTrue("delete button should be enabled", page.canClickDelete());

		page.deleteProject();

		projectsPage = ProjectsPage.goToProjectsPage(driver(), true);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals("project should no longer exist", 0, projectsPage.getNumberOfProjects());
	}

	@Test
	public void deleteProjectAsOwner() {
		LoginPage.loginAsManager(driver());

		ProjectsPage projectsPage = ProjectsPage.goToProjectsPage(driver(), false);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals("should be 1 project with name " + PROJECT_NAME, 1, projectsPage.getNumberOfProjects());

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse("delete button should be disabled", page.canClickDelete());

		page.clickConfirm();

		assertTrue("delete button should be enabled", page.canClickDelete());

		page.deleteProject();

		projectsPage = ProjectsPage.goToProjectsPage(driver(), false);
		projectsPage.searchTableForProjectName(PROJECT_NAME);
		assertEquals("project should no longer exist", 0, projectsPage.getNumberOfProjects());
	}

}
