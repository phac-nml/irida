package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsNewPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsNewPageIT extends AbstractIridaUIIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsNewPageIT.class);
	private ProjectsNewPage page;

	@Override
	public WebDriver driverToUse() {
		return new ChromeDriver();
	}

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new ProjectsNewPage(driver());
	}

	@Test
	public void testCreateNewProjectForm() {
		logger.debug("Testing: CreateNewProjectFrom");
		page.goToPage();
		assertEquals("Should have the correct page title", "IRIDA Platform - Create a New Project", driver().getTitle());

		// Start with just submitting the empty form
		page.submitForm("", "", "", "");
		assertTrue("Should show a required error.", page.isErrorNameRequiredDisplayed());

		// Clear the error by adding a name
		page.setName("Random Name");
		assertFalse("Error Field should be gone", page.formHasErrors());

		// Let's try adding a bad url
		page.goToPage();
		page.setRemoteURL("red dog");
		assertTrue("Should have a bad url error", page.isErrorUrlDisplayed());

		// Let add a good url
		page.setRemoteURL("http://google.com");
		assertFalse("URL Error Field should be gone", page.formHasErrors());

		// Create the project
		page.goToPage();
		page.submitForm("test project name", "", "", "");
		page.clickSubmit();
		assertTrue("Redirects to the project metadata page", driver().getCurrentUrl().contains("/metadata"));
	}

	@Test
	public void testCustomTaxa() {
		page.goToPage();
		page.setOrganism("something new");
		assertTrue("warning should be displayed", page.isNewOrganismWarningDisplayed());

		page.setOrganism(ProjectsNewPage.EXISTING_TAXA);
		assertFalse("warning should not be displayed", page.isNewOrganismWarningDisplayed());
	}
}
