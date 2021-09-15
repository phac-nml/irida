package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.CreateProjectComponent;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class CreateProjectIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void login() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testCreateProject() {
		String name = "TESTING PROJECT NAME";
		String description = "Bacon ipsum dolor amet flank corned beef sirloin chislic ground round andouille jowl";
		ProjectsPage.goToProjectsPage(driver(), false);
		CreateProjectComponent createComponent = CreateProjectComponent.initializeComponent(driver());
		createComponent.displayForm();

		// Test invalid entries
		createComponent.enterProjectName("SMA");
		Assert.assertEquals("Should have a name length error", "The project name must be at least 5 characters long", createComponent.getNameWarning());
		createComponent.enterProjectName("");
		Assert.assertEquals("Should display a required error", "A name is required for every project", createComponent.getNameWarning());
		createComponent.enterProjectName("TE\0*");
		Assert.assertEquals("Should display a invalid character warning", "A project name can only have letters, numbers, spaces, _ and -.", createComponent.getNameWarning());

		// Test correct
		createComponent.enterProjectName(name);
		createComponent.enterProjectDescription(description);
		createComponent.goToNextStep();
		Assert.assertTrue("Should be a message explaining no samples", createComponent.isNoSamplesMessageDisplayed());
		createComponent.submitProject();
		Assert.assertTrue(driver().getTitle().contains(name));

		// Go to the settings page to make sure things were set properly.
		driver().get(driver().getCurrentUrl() + "/settings/details");

		ProjectDetailsPage detailsPage = ProjectDetailsPage.initElements(driver());
		Assert.assertEquals(name, detailsPage.getProjectName());
		Assert.assertEquals(description, detailsPage.getProjectDescription());
	}

	@Test
	public void testCreateProjectWithSamples() {
		String name = "TESTING PROJECT NAME";

		// Add some samples
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.addSelectedSamplesToCart();

		ProjectsPage.goToProjectsPage(driver(), false);
		CreateProjectComponent createComponent = CreateProjectComponent.initializeComponent(driver());
		createComponent.displayForm();
		createComponent.enterProjectName(name);
		createComponent.goToNextStep();
		createComponent.selectAllSamples();

		createComponent.submitProject();
		Assert.assertTrue(driver().getTitle().contains(name));
		Assert.assertEquals("Should be 1 sample on the page", "Showing 1 to 1 of 1 entries", samplesPage.getTableInfo());
	}
}
