package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSamplesPageIT {

	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		this.driver = new PhantomJSDriver();
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testPageSetUp() {
		driver.get(page.URL);
		assertEquals("Project Samples has the correct title", "project Samples", page.getTitle());
		assertEquals("Displays all the samples", 5, page.getDisplayedSampleCount());
		assertEquals("No samples should be originally selected", 0, page.getSelectedSampleCount());
		assertEquals("Displays the ability to view files", 1, page.getCountOfSamplesWithFiles());
		page.clickSelectAllCheckbox();
		assertEquals("All samples should be selected", page.getDisplayedSampleCount(), page.getSelectedSampleCount());
		page.clickFirstSampleCheckbox();
		assertTrue("'selectAll' checkbox is in an indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickFirstSampleCheckbox();
		assertTrue("'selectAll' should now be selected", page.isSelectAllSelected());

		// Show the files area for the first checkbox
		page.openFilesView();
		assertTrue("Should display the files area", page.isFilesAreaDisplayed());
	}
}
