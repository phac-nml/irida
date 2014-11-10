package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.drivers.IridaChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisAdminPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisAdminPageIT {
	private WebDriver driver;
	private AnalysisAdminPage adminPage;

	@Before
	public void setUp() {
		// TODO (14-11-07 - josh): Find out why PhantomJS fails here.
		driver = new IridaChromeDriver();
		LoginPage.loginAsAdmin(driver);
		adminPage = new AnalysisAdminPage(driver);
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetup() {
		assertEquals(8, adminPage.getTableRowCount());

		adminPage.clickShowFilterButton();
		adminPage.filterByName("My");
		assertEquals(7, adminPage.getTableRowCount());

		adminPage.selectStateFilter("Completed");
		assertEquals(2, adminPage.getTableRowCount());

		adminPage.clickClearFilterButton();
		assertEquals(8, adminPage.getTableRowCount());
	}
}
