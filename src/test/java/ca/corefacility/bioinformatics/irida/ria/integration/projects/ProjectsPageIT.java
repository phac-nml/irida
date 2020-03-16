package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testProjectsPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());

		ProjectsPage page = ProjectsPage.goToProjectsPage(driver(), true);
		checkTranslations(page, ImmutableList.of("projects"), "Projects");

		assertEquals("Should be 8 projects", 8, page.getNumberOfProjects());
		assertFalse("Projects name should not be sorted originally", page.isTableSortedByProjectNamesAscending());
		page.sortTableByProjectName();
		assertTrue("Project names should now be sorted", page.isTableSortedByProjectNamesAscending());

		page.sortTableByProjectName();
		assertTrue("Project names should be sorted reverse.", page.isTableSortedByProjectNamesDescending());

		page.searchTableForProjectName("project EFGH");
		assertEquals("Should only be 1 project visible", 1, page.getNumberOfProjects());
	}

	@Test
	public void testProjectsPageAsUser() {
		LoginPage.loginAsUser(driver());
		ProjectsPage page = ProjectsPage.goToProjectsPage(driver(), true);
		assertEquals("Should be on the error page", driver().getTitle(), "IRIDA Platform - Access Denied");

		page = ProjectsPage.goToProjectsPage(driver(), false);
		checkTranslations(page, ImmutableList.of("projects"), "Projects");
		assertEquals("Should be 2 projects on the page", 2, page.getNumberOfProjects());

	}
}
