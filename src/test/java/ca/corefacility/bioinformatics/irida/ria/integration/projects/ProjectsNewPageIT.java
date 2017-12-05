package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsNewPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsNewPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsNewPageIT.class);
	private ProjectsNewPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new ProjectsNewPage(driver());
	}

	@Test
	public void testCreateNewProjectForm() {
		logger.debug("Testing: CreateNewProjectFrom");
		page.goToPage();
		assertEquals("Should have the correct page title", "IRIDA Platform - Create a New Project",
				driver().getTitle());

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

		ProjectMetadataPage metadataPage = new ProjectMetadataPage(driver());
		assertTrue("Should be on metadata page which has edit button", metadataPage.hasEditButton());
	}

	@Test
	public void testCustomTaxa() {
		page.goToPage();
		page.setOrganism("something new");
		assertTrue("warning should be displayed", page.isNewOrganismWarningDisplayed());

		page.setOrganism(ProjectsNewPage.EXISTING_TAXA);
		assertFalse("warning should not be displayed", page.isNewOrganismWarningDisplayed());
	}

	@Test
	@Ignore
	public void testProjectFromCart() {
		// get samples from original project and add to cart
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		List<String> originalSamples = samplesPage.getSampleNamesOnPage();
		samplesPage.selectAllSamples();
		samplesPage.addSelectedSamplesToCart();

		// create project
		page.goToPageWithCart();
		page.setName("newProjectWithSamples");
		page.clickSubmit();

		ProjectMetadataPage metadataPage = new ProjectMetadataPage(driver());
		assertTrue("Should be on metadata page which has edit buttong", metadataPage.hasEditButton());
		Long projectId = metadataPage.getProjectId();

		// check if samples match
		samplesPage = ProjectSamplesPage.gotToPage(driver(), projectId.intValue());

		List<String> sampleNames = samplesPage.getSampleNamesOnPage();
		assertFalse("Should have samples", sampleNames.isEmpty());

		assertEquals("should have the same samples as the other project", originalSamples, sampleNames);
	}
}
