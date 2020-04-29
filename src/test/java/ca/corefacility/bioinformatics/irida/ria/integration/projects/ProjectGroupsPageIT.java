package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectGroupsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

/**
 * Integration testing to ensure the Project Groups page is working
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectGroupsPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testProjectGroupsPageAsManager() {
		LoginPage.loginAsManager(driver());
		ProjectGroupsPage page = ProjectGroupsPage.goTo(driver());

		assertEquals("Should have one existing group", 1, page.getNumberOfGroups());

		/*
		 Test remove a group
		 */
		page.removeGroup();
		assertEquals("Should have no groups", 0, page.getNumberOfGroups());

		/*
		Test adding a group.
		 */
		page.addGroup("group");
		assertEquals("Should have one existing group", 1, page.getNumberOfGroups());
	}
}
