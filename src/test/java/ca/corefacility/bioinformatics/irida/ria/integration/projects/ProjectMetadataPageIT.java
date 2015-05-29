package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;
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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
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
public class ProjectMetadataPageIT {
	private final String PAGE_TITLE = "IRIDA Platform - project2 - Metadata";
	private final Long PROJECT_ID_AS_OWNER = 2L;
	private final Long PROJECT_ID_AS_COLLABORATOR = 1L;
	private final String PROJECT_NAME = "project2";
	private final String PROJECT_DESCRIPTION = "This is another interesting project description.";
	private final String PROJECT_ORGANISM = "Salmonella";
	private final String PROJECT_REMOTE_URL = "http://salmonella-wiki.ca";

	private static WebDriver driver;
	private ProjectMetadataPage page;

	@BeforeClass
	public static void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
	}

	@Before
	public void setUpTest() {
		page = new ProjectMetadataPage(driver);
	}

	@After
	public void tearDown() {
		LoginPage.logout(driver);
	}

	@AfterClass
	public static void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void displaysTheProjectMetaData() {
		LoginPage.loginAsUser(driver);
		page.goTo(PROJECT_ID_AS_OWNER);
		assertEquals("Displays the correct page title", PAGE_TITLE, driver.getTitle());
		assertEquals("Displays the correct project name", PROJECT_NAME, page.getDataProjectName());
		assertEquals("Displays the correct description", PROJECT_DESCRIPTION, page.getDataProjectDescription());
		assertEquals("Displays the correct organism", PROJECT_ORGANISM, page.getDataProjectOrganism());
		assertEquals("Displays the correct remoteURL", PROJECT_REMOTE_URL, page.getDataProjectRemoteURL());
		assertTrue("Contains edit metadata button", page.hasEditButton());

		// Should not have edit button on project that is not owner of.
		page.goTo(PROJECT_ID_AS_COLLABORATOR);
		assertFalse("Should not contain the edit medtadata button if they are only a collaborator",
				page.hasEditButton());
	}

}
