package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsNewPage;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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

import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectsNewPageIT {
	private WebDriver driver;
	private ProjectsNewPage page;

	@Before
	public void setUp() {
		driver = new ChromeDriver();
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();
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
		// Start with just submitting the empty form
		page.submitForm("", "", "", "");
		String error = page.getErrors().get(0);
		assertTrue("Should show a required error.", error.contains("required"));

		// Clear the error by adding a name
		page.setName("Random Name");
		assertTrue("Error Field should be gone", page.checkForErrors());

		// Let's try adding a bad url
		page.setURL("red dog");
		String urlError = page.getErrors().get(0);
		assertTrue("Should show url error", urlError.contains("enter a valid URL"));

        // Let add a good url
        page.setURL("http://google.com");
        assertTrue("URL Error Field should be gone", page.checkForErrors());

        // Create the project
        page.submit();
        assertTrue("Redirects to the project metadata page", driver.getCurrentUrl().contains("/metadata"));
    }
}
