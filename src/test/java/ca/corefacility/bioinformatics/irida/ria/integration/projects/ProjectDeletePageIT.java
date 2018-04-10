package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDeletePage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

		ProjectsPage projectsPage = new ProjectsPage(driver());
		projectsPage.toAdminProjectsPage();
		projectsPage.doSearch(PROJECT_NAME);
		assertEquals("should be 1 project with name " + PROJECT_NAME, 1, projectsPage.projectsTableSize());

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse("delete button should be disabled", page.canClickDelete());

		page.clickConfirm();

		assertTrue("delete button should be enabled", page.canClickDelete());

		page.deleteProject();

		projectsPage.toAdminProjectsPage();
		projectsPage.doSearch(PROJECT_NAME);
		assertEquals("project should no longer exist", 0, projectsPage.projectsTableSize());
	}

	@Test
	public void deleteProjectAsOwner() {
		LoginPage.loginAsManager(driver());

		ProjectsPage projectsPage = new ProjectsPage(driver());
		projectsPage.toUserProjectsPage();
		projectsPage.doSearch(PROJECT_NAME);
		assertEquals("should be 1 project with name " + PROJECT_NAME, 1, projectsPage.projectsTableSize());

		page = ProjectDeletePage.goTo(driver(), PROJECT_ID);

		assertFalse("delete button should be disabled", page.canClickDelete());

		page.clickConfirm();

		assertTrue("delete button should be enabled", page.canClickDelete());

		page.deleteProject();

		projectsPage.toUserProjectsPage();
		projectsPage.doSearch(PROJECT_NAME);
		assertEquals("project should no longer exist", 0, projectsPage.projectsTableSize());
	}

}
