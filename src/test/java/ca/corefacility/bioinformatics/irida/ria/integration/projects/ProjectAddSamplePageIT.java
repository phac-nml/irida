package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectAddSamplePage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Integration Test for creating a new sample.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectsAddSampleView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectAddSamplePageIT {
	private String NAME_ERROR_TOO_SHORT = "a";
	private String NAME_WITH_INVALID_CHARACTERS = "My Sample Name";
	private String NAME_VALID = "Sample00010-11";
	private WebDriver driver;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testSampleCreation() {
		ProjectAddSamplePage page = ProjectAddSamplePage.gotoAsProjectManager(driver);
		assertFalse("Create button should be disabled", page.isCreateButtonEnabled());

		// Test sending a too short of name
		page.enterSampleName(NAME_ERROR_TOO_SHORT);
		assertFalse("Create button should be disabled", page.isCreateButtonEnabled());
		assertTrue("Minimum Length for name error should be displayed", page.isMinLengthNameErrorVisible());

		// Test clearing the name
		page.enterSampleName("");
		assertFalse("Create button should be disabled", page.isCreateButtonEnabled());
		assertTrue("Required name error should be visible", page.isRequiredNameErrorVisible());

		// Test invalid characters
		page.enterSampleName(NAME_WITH_INVALID_CHARACTERS);
		assertFalse("Create button should be disabled", page.isCreateButtonEnabled());
		assertTrue("Invalid Characters in name error should be visible", page.isInvalidCharactersInNameVisible());

		// Create a valid sample
		page.enterSampleName(NAME_VALID);
		assertTrue("Create button should be enabled", page.isCreateButtonEnabled());
		page.createSample();
		assertTrue("Should redirect to sample files page.", driver.getCurrentUrl().contains("/sequenceFiles"));
	}
}
