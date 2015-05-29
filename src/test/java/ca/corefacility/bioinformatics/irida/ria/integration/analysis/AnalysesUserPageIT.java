package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysesUserPageIT {
	private static WebDriver driver;

	@BeforeClass
	public static void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
	}

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver);
	}

	@After
	public void tearDown() {
		LoginPage.logout(driver);
	}

	@AfterClass
	public static void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetUp() {
		AnalysesUserPage page = AnalysesUserPage.initializePage(driver);
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());
		page.filterByName("submission");
		assertEquals("Should be 6 analyses displayed on the page after filtering by name", 6,
				page.getNumberOfAnalyses());
		assertEquals("Should be 2 download buttons displayed, one for each completed analysis", 2, page.getNumberOfDownloadBtns());

		assertEquals("Should display progress bars with percent complete for everything except error state", 6,
				page.getNumberOfProgressBars());
		assertEquals("Should display 90% complete", "90%", page.getPercentComplete(1));
		assertEquals("Should display 100% complete", "5%", page.getPercentComplete(2));
	}

	@Test
	public void testAdvancedFilters() {
		AnalysesUserPage page = AnalysesUserPage.initializePage(driver);
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());

		page.filterByState("New");
		assertEquals("Should be 1 analysis in the state of 'NEW'", 1, page.getNumberOfAnalyses());

		page.filterByState("Completed");
		assertEquals("Should be 2 analysis in the state of 'COMPLETED'", 2, page.getNumberOfAnalyses());

		page.filterByState("Prepared");
		assertTrue("Should display a message that there are no analyses available", page.isNoAnalysesMessageDisplayed());

		// Clear
		page.clearFilter();
		assertEquals("Should be 8 analyses displayed on the page", 8, page.getNumberOfAnalyses());

		page.filterByDateEarly("06 Nov 2013");
		assertEquals("Should be 3 analyses after filtering by date earliest", 3, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();

		page.filterByDateLate("06 Jan 2014");
		assertEquals("Should be 7 analyses after filtering by date earliest", 7, page.getNumberOfAnalyses());

		// Clear
		page.clearFilter();
		page.filterByType("Phylogenomics Pipeline");
		assertEquals("Should be 6 analyses aftering filtering by type", 6, page.getNumberOfAnalyses());
	}
}
