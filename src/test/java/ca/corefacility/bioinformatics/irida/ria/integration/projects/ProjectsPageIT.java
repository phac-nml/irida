package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testProjectsPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		ProjectsPage page = ProjectsPage.goToProjectsPage(driver(), true);
		checkTranslations(page, ImmutableList.of("projects"), "Projects\nCreate New Project");

		assertEquals(8, page.getNumberOfProjects(), "Should be 8 projects");
		List<String> projectNames = page.getProjectsSortListByColumnName();
		assertFalse(Ordering.natural().isOrdered(projectNames),
				"Projects name should not be sorted originally");
		page.sortProjectTableBy();
		projectNames = page.getProjectsSortListByColumnName();
		assertTrue(Ordering.natural().isOrdered(projectNames),
				"Project names should now be sorted");

		page.sortProjectTableBy();
		projectNames = page.getProjectsSortListByColumnName();
		assertTrue(Ordering.natural().reverse().isOrdered(projectNames),
				"Project names should be sorted reverse.");

		page.searchTableForProjectName("project EFGH");
		assertEquals(1, page.getNumberOfProjects(), "Should only be 1 project visible");

		assertTrue(page.createNewButtonVisible(), "There should be a Create New Project button visible");
	}

	@Test
	public void testProjectsPageAsUser() {
		LoginPage.loginAsUser(driver());
		ProjectsPage page = ProjectsPage.goToProjectsPage(driver(), true);
		assertEquals(driver().getTitle(), "IRIDA Platform - Access Denied", "Should be on the error page");

		page = ProjectsPage.goToProjectsPage(driver(), false);
		checkTranslations(page, ImmutableList.of("projects"), "Projects\nCreate New Project");
		assertEquals(2, page.getNumberOfProjects(), "Should be 2 projects on the page");

		assertTrue(page.createNewButtonVisible(), "There should be a Create New Project button visible");
	}
}
