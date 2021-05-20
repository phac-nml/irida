package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectUserGroupsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectUserGroupsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testPageAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertFalse("Collaborators should not be able to add a new group to the project",
				page.isAddUserGroupButtonVisible());
	}

	@Test
	public void testPageAsAManager() {
		LoginPage.loginAsManager(driver());
		ProjectUserGroupsPage page = ProjectUserGroupsPage.goToPage(driver(), 1L);
		assertTrue("Managers can see the add user groups button.", page.isAddUserGroupButtonVisible());
		assertEquals("Should be no user groups", 0, page.getNumberOfUserGroups());
		page.addUserGroup("group 1");
		assertEquals("Should be one user groups", 1, page.getNumberOfUserGroups());

		page.removeUserGroups(0);
		assertEquals("Should be no user groups", 0, page.getNumberOfUserGroups());
	}
}
