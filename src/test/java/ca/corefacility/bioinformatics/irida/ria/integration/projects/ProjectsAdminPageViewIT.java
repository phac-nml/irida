package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p> Integration test to ensure that the Projects Page works with Admin priveleges. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectsPageAdminView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectsAdminPageViewIT {
	private WebDriver driver;
	private ProjectsPage projectsPage;

	@Before
	public void setup() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsAdmin(driver);
		projectsPage = new ProjectsPage(driver);
		projectsPage.toAdminProjectsPage();
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
		}
	}

	@Test
	public void testLayout() {
		assertEquals("Projects table should be populated by 5 projects", 5, projectsPage.projectsTableSize());
		assertTrue("Admin should have checkboxes to select projects",
				projectsPage.adminShouldBeAbleToSelectViaCheckboxes());
	}

	@Test
	public void testAdminCheckboxes() {
		assertEquals("No checkboxes should be selected by default", 0, projectsPage.adminGetSelectedCheckboxCount());
		projectsPage.adminSelectHeaderCheckbox();
		assertEquals("All checkboxes should be selected", 5, projectsPage.adminGetSelectedCheckboxCount());
		projectsPage.adminSelectHeaderCheckbox();
		assertEquals("No checkboxes should be checked when the header checkbox in unchecked", 0,
				projectsPage.adminGetSelectedCheckboxCount());
		projectsPage.adminSelectFirstCheckbox();
		assertTrue("Select All checkbox should be in an indeterminate state.",
				projectsPage.adminIsSelectAllCheckboxIntermediateState());
		projectsPage.adminSelectFirstCheckbox();
		assertFalse("Select All should now be in a normal state",
				projectsPage.adminIsSelectAllCheckboxIntermediateState());
	}
}
