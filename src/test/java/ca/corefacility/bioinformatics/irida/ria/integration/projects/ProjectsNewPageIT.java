package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsNewPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectsNewPageIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsNewPageIT.class);
	private WebDriver driver;
	private ProjectsNewPage page;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
		LoginPage.loginAsManager(driver);
		page = new ProjectsNewPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testCreateNewProjectForm() {
		logger.debug("Testing: CreateNewProjectFrom");
		page.goToPage();
		assertEquals("Should have the correct page title", "IRIDA Platform - Create a New Project", driver.getTitle());

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
		assertTrue("Redirects to the project metadata page", driver.getCurrentUrl().contains("/metadata"));
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
