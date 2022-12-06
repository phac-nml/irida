package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class AssociatedProjectsPageIT extends AbstractIridaUIITChromeDriver {
	Long PROJECT_ID = 1L;

	@Test
	public void hasTheCorrectAssociatedProjects() {
		LoginPage.loginAsManager(driver());
		AssociatedProjectPage page = AssociatedProjectPage.goToPage(driver(), PROJECT_ID);
		assertEquals(6, page.getNumberOfTotalProjectsDisplayed(), "Should display all the available projects");
		assertEquals(2, page.getNumberOfAssociatedProject(), "Has the correct number of associated projects");

		// Test associating another project
		page.toggleProjectAssociation(2);
		assertEquals(3, page.getNumberOfAssociatedProject(), "There should now be another associated project");
		// Make sure it is associated even after page refresh
		driver().navigate().refresh();
		assertEquals(3, page.getNumberOfAssociatedProject(), "There should still be 3 projects selected");

		// Test un-associating a project
		page.toggleProjectAssociation(0);
		assertEquals(2, page.getNumberOfAssociatedProject(), "There should now be one less associated project");
		// Make sure it is associated even after page refresh
		driver().navigate().refresh();
		assertEquals(2, page.getNumberOfAssociatedProject(), "There should still be 2 projects selected");

	}
}
