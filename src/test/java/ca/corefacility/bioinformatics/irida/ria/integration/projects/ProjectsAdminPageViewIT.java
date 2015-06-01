package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p> Integration test to ensure that the Projects Page works with Admin priveleges. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectsPageAdminView.xml")
public class ProjectsAdminPageViewIT extends AbstractIridaUIITChromeDriver {
	private ProjectsPage projectsPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		projectsPage = new ProjectsPage(driver());
		projectsPage.toAdminProjectsPage();
	}

	@Test
	public void testLayout() {
		assertEquals("Projects table should be populated by 5 projects", 5, projectsPage.projectsTableSize());
	}
}
