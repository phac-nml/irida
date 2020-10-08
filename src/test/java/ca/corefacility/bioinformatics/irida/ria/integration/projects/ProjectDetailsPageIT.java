package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDetailsPageIT extends AbstractIridaUIITChromeDriver {

	private ProjectDetailsPage page;

	@Test
	public void testProjectDetailsAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectDetailsPage page = ProjectDetailsPage.goTo(driver(), 1L);

		assertEquals("Should have the correct project name", "project", page.getProjectName());
		assertEquals("Displays the correct description", "This is an interesting project description.",
				page.getProjectDescription());
		Long PROJECT_ID_AS_COLLABORATOR = 1L;
		assertEquals("Displays the correct identifier", PROJECT_ID_AS_COLLABORATOR, page.getProjectId());
		assertEquals("Displays the correct organism", "E. coli", page.getProjectOrganism());
	}

	@Test
	public void testProjectDetailsAsManager() {
		LoginPage.loginAsAdmin(driver());
		Long PROJECT_ID_AS_MANAGER = 2L;
		ProjectDetailsPage page = ProjectDetailsPage.goTo(driver(), PROJECT_ID_AS_MANAGER);
		assertEquals("Should have the correct project name", "project2", page.getProjectName());
		assertEquals("Displays the correct description", "This is another interesting project description.", page.getProjectDescription());
		assertEquals("Displays the correct identifier", PROJECT_ID_AS_MANAGER, page.getProjectId());
	}
}
