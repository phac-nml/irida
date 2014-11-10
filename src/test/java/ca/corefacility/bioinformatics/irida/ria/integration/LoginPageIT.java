package ca.corefacility.bioinformatics.irida.ria.integration;

import static org.junit.Assert.assertEquals;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p> Integration test to ensure that the Login Page works and redirects the user to the dashboard. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("dev")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class LoginPageIT {

	private static final String EXPIRED_USERNAME = "expiredGuy";
	private static final String EXPIRED_PASSWORD = "Password1";

	private WebDriver driver;

	@Before
	public void setup() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testBadUsername() throws Exception {
		LoginPage page = LoginPage.to(driver);
		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertEquals("Should update the url with '?error=true'", driver.getCurrentUrl(),
				"http://localhost:8080/login?error=true");
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Email or Password");
	}

	@Test
	public void testBadPassword() throws Exception {
		LoginPage page = LoginPage.to(driver);
		page.login(LoginPage.USER_USERNAME, LoginPage.BAD_PASSWORD);
		assertEquals("Should update the url with '?error=true'", driver.getCurrentUrl(),
				"http://localhost:8080/login?error=true");
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Email or Password");
	}

	@Test
	public void testGoodLogin() throws Exception {
		LoginPage.login(driver, LoginPage.ADMIN_USERNAME, LoginPage.GOOD_PASSWORD);
		assertEquals("The 'test' user is logged in and redirected.", "http://localhost:8080/dashboard",
				driver.getCurrentUrl());
	}

	@Test
	public void testExpiredCredentialsLogin() throws Exception {
		LoginPage page = LoginPage.to(driver);
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		String expectedPage = "http://localhost:8080/password_reset/.+";
		assertTrue("The 'expiredGuy' user should be sent to a password reset page.",
				driver.getCurrentUrl().matches(expectedPage));
	}

	@Test
	public void testLoginWithChangedCredentials() {
		String newPassword = "aGoodP@ssW0rD";
		LoginPage page = LoginPage.to(driver);
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		PasswordResetPage passwordResetPage = new PasswordResetPage(driver);
		passwordResetPage.enterPassword(newPassword, newPassword);
		assertTrue(passwordResetPage.checkSuccess());

		page.logout(driver);
		page = LoginPage.to(driver);
		page.login(EXPIRED_USERNAME, newPassword);
		assertEquals("The user is logged in and redirected.", "http://localhost:8080/dashboard",
				driver.getCurrentUrl());
	}
}
