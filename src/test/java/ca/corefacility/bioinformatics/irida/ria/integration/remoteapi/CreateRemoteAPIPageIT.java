package ca.corefacility.bioinformatics.irida.ria.integration.remoteapi;

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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.CreateRemoteAPIPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi.RemoteAPIDetailsPage.ApiStatus;
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
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/oauth/RemoteApisIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class CreateRemoteAPIPageIT {
	private WebDriver driver;
	private CreateRemoteAPIPage page;

	@Before
	public void setup() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsAdmin(driver);

		page = new CreateRemoteAPIPage(driver);
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
		page.createRemoteAPIWithDetails("new name", "http://newuri", "newClient", "newSecret");
		assertTrue(page.checkSuccess());
	}

	@Test
	public void testCreateClientWithDuplicateURI() {
		page.createRemoteAPIWithDetails("new name", "http://nowhere", "newClient", "newSecret");
		assertFalse(page.checkSuccess());
	}

	@Test
	public void testAndConnectToClient() {
		String applicationPort = page.getApplicationPort();
		String url = "http://localhost:" + applicationPort + "/api";

		page.createRemoteAPIWithDetails("new name", url, "testClient", "testClientSecret");
		assertTrue(page.checkSuccess());

		RemoteAPIDetailsPage remoteAPIDetailsPage = new RemoteAPIDetailsPage(driver);

		ApiStatus remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals(ApiStatus.INVALID, remoteApiStatus);
		remoteAPIDetailsPage.clickConnect();
		remoteAPIDetailsPage.clickAuthorize();

		remoteApiStatus = remoteAPIDetailsPage.getRemoteApiStatus();
		assertEquals(ApiStatus.CONNECTED, remoteApiStatus);
	}
}
