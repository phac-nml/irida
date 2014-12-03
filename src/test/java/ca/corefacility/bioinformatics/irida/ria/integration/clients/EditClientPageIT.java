package ca.corefacility.bioinformatics.irida.ria.integration.clients;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.EditClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * IT for the client details page
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/IridaClientDetailsServiceImplIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class EditClientPageIT {
	private static final String ORIGINAL_SECRET = "xxxxxxxx";

	private WebDriver driver;

	@Before
	public void setup() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
		LoginPage.loginAsAdmin(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testCreateGoodClient() {
		EditClientPage page = EditClientPage.goToEditPage(driver, 1L);
		page.editClient(null, true, true, false);
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testEditClientWithNewSecret() {
		EditClientPage page = EditClientPage.goToEditPage(driver, 1L);
		page.editClient(null, true, true, true);
		assertTrue(page.checkSuccess());
		ClientDetailsPage detailsPage = new ClientDetailsPage(driver);
		String newSecret = detailsPage.getClientSecret();
		assertNotEquals(ORIGINAL_SECRET, newSecret);
	}

	@Test
	public void testClientsWithoutScope() {
		EditClientPage page = EditClientPage.goToEditPage(driver, 1L);
		page.editClient(null, false, false, false);
		assertFalse(page.checkSuccess());
	}

}
