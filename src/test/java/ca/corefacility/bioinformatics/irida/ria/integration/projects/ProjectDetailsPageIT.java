package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectDetailsPage;
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
public class ProjectDetailsPageIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDetailsPageIT.class);
	public static final Long PROJECT_ID = 1L;
	public static final String PROJECT_NAME = "project";
	public static final String PROJECT_OWNER = "Mr. Manager";
	public static final String PROJECT_CREATED_DATE = "12 Jul 2013";
	public static final String PROJECT_ORGANISM = "E. coli";

	private static WebDriver driver;
	private ProjectDetailsPage detailsPage;

	@BeforeClass
	public static void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
	}

	@Before
	public void setUpTest() {
		detailsPage = new ProjectDetailsPage(driver);
		LoginPage.loginAsManager(driver);
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
	public void hasCorrectMetaData() {
		detailsPage.goTo(PROJECT_ID);
		logger.debug("Testing: hasCorrectMetaDate");
		assertEquals("Page should show correct title", PROJECT_NAME, detailsPage.getPageTitle());
		assertEquals("Should have the organism displayed", PROJECT_ORGANISM, detailsPage.getOrganism());
		assertEquals("Should have the correct date format for creation date", PROJECT_CREATED_DATE,
				detailsPage.getCreatedDate());
	}

	@Test
	public void testDisplaysProjectEvent() {
		List<WebElement> events = detailsPage.getEvents();
		assertEquals(1, events.size());
		WebElement next = events.iterator().next();
		String className = next.getAttribute("class");
		assertTrue(className.contains("user-role-event"));
	}
}
