package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import ca.corefacility.bioinformatics.irida.ria.integration.drivers.IridaPhantomJSDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIsPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RemoteAPIsPageIT {
	private WebDriver driver;
	private RemoteAPIsPage apisPage;

	@Before
	public void setup() {
		driver = new IridaPhantomJSDriver();
		LoginPage.loginAsAdmin(driver);

		apisPage = new RemoteAPIsPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testCheckClientsPageNumber() {
		int clientsTableSize = apisPage.remoteApisTableSize();
		assertEquals(2, clientsTableSize);
		assertTrue(apisPage.checkRemoteApiExistsInTable("a client"));
		assertTrue(apisPage.checkRemoteApiExistsInTable("another client"));
	}
}
