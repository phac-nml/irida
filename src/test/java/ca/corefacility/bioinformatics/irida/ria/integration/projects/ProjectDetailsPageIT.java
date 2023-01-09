package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDetailsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testProjectDetailsAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectDetailsPage page = ProjectDetailsPage.goTo(driver(), 1L);

		assertEquals("project", page.getProjectName(), "Should have the correct project name");
		assertEquals("This is an interesting project description.", page.getProjectDescription(),
				"Displays the correct description");
		Long PROJECT_ID_AS_COLLABORATOR = 1L;
		assertEquals(PROJECT_ID_AS_COLLABORATOR, page.getProjectId(), "Displays the correct identifier");
		assertEquals("E. coli", page.getProjectOrganism(), "Displays the correct organism");
	}

	@Test
	public void testProjectDetailsAsManager() {
		LoginPage.loginAsAdmin(driver());
		Long PROJECT_ID_AS_MANAGER = 2L;
		ProjectDetailsPage page = ProjectDetailsPage.goTo(driver(), PROJECT_ID_AS_MANAGER);
		assertEquals("project2", page.getProjectName(), "Should have the correct project name");
		assertEquals("This is another interesting project description.", page.getProjectDescription(), "Displays the correct description");
		assertEquals(PROJECT_ID_AS_MANAGER, page.getProjectId(), "Displays the correct identifier");
	}
}
