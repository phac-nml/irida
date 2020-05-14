package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class AssociatedProjectsPageIT extends AbstractIridaUIITChromeDriver {
	Long PROJECT_ID = 1L;

	@Test
	public void hasTheCorrectAssociatedProjects() {
		LoginPage.loginAsManager(driver());
		AssociatedProjectPage page = AssociatedProjectPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Should display all the available projects", 6, page.getNumberOfTotalProjectsDisplayed());
		assertEquals("Has the correct number of associated projects", 2, page.getNumberOfAssociatedProject());

		// Test associating another project
		page.toggleProjectAssociation(2);
		assertEquals("There should now be another associated project", 3, page.getNumberOfAssociatedProject());
		// Make sure it is associated even after page refresh
		driver().navigate().refresh();
		assertEquals("There should still be 3 projects selected", 3, page.getNumberOfAssociatedProject());

		// Test un-associating a project
		page.toggleProjectAssociation(0);
		assertEquals("There should now be one less associated project", 2, page.getNumberOfAssociatedProject());
		// Make sure it is associated even after page refresh
		driver().navigate().refresh();
		assertEquals("There should still be 2 projects selected", 2, page.getNumberOfAssociatedProject());


	}
}
