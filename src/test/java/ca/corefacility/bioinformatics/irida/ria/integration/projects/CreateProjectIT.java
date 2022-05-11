package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.CreateProjectComponent;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class CreateProjectIT extends AbstractIridaUIITChromeDriver {

	@BeforeEach
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
		assertEquals("The project name must be at least 5 characters long", createComponent.getNameWarning(), "Should have a name length error");
		createComponent.enterProjectName("");
		assertEquals("A name is required for every project", createComponent.getNameWarning(),
				"Should display a required error");
		createComponent.enterProjectName("TE&#*");
		assertEquals("A project name can only have letters, numbers, spaces, _ and -.",
				createComponent.getNameWarning(), "Should display a invalid character warning");

		// Test correct
		createComponent.enterProjectName(name);
		createComponent.enterProjectDescription(description);
		createComponent.goToNextStep();
		assertTrue(createComponent.isNoSamplesMessageDisplayed(), "Should be a message explaining no samples");
		createComponent.goToNextStep();
		assertTrue(createComponent.isNoSamplesSelectedMessageDisplayed(), "Should be a message explaining no samples were selected so there is no metadata available");
		createComponent.submitProject();
		assertTrue(driver().getTitle().contains(name));

		// Go to the settings page to make sure things were set properly.
		driver().get(driver().getCurrentUrl() + "/settings");

		ProjectDetailsPage detailsPage = ProjectDetailsPage.initElements(driver());
		assertEquals(name, detailsPage.getProjectName());
		assertEquals(description, detailsPage.getProjectDescription());
	}

	@Test
	public void testCreateWithUniqueOrganismName() {
		String name = "TESTING PROJECT NAME";
		String organism = "My very unique organism";
		ProjectsPage.goToProjectsPage(driver(), false);
		CreateProjectComponent createComponent = CreateProjectComponent.initializeComponent(driver());
		createComponent.displayForm();

		createComponent.enterProjectName(name);
		createComponent.enterOrganism(organism);
		createComponent.goToNextStep();
		createComponent.goToNextStep();
		createComponent.submitProject();

		// Go to the settings page to make sure things were set properly.
		driver().get(driver().getCurrentUrl() + "/settings");
		ProjectDetailsPage detailsPage = ProjectDetailsPage.initElements(driver());
		assertEquals(organism, detailsPage.getProjectOrganism(), "Should have a custom organism name");
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
		createComponent.goToNextStep();
		assertTrue(createComponent.correctMetadataFieldDataDisplayed(), "The correct metadata field labels, current restrictions, and target restrictions should be displayed");
		createComponent.submitProject();
		assertTrue(driver().getTitle().contains(name));
		assertEquals("Showing 1 to 1 of 1 entries", samplesPage.getTableInfo(), "Should be 1 sample on the page");

		// Go to the settings -> metadata page to make sure metadata fields and restrictions were set correctly.
		driver().get(driver().getCurrentUrl() + "/settings/metadata/fields");

		assertTrue(createComponent.correctMetadataFieldDataDisplayedForNewProject(), "The metadata fields should be copied over to the new project");
	}
}
