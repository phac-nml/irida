package ca.corefacility.bioinformatics.irida.ria.integration;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Login Page works and redirects the user to the dashboard. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageIT.xml")
public class LoginPageIT extends AbstractIridaUIIT {

	private static final String EXPIRED_USERNAME = "expiredGuy";
	private static final String EXPIRED_PASSWORD = "Password1";

	@Test
	public void testBadUsername() throws Exception {
		LoginPage page = LoginPage.to(driver());
		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue("Should update the url with '?error=true'", driver().getCurrentUrl().contains("login?error=true"));
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Email or Password");
	}

	@Test
	public void testBadPassword() throws Exception {
		LoginPage page = LoginPage.to(driver());
		page.login(LoginPage.USER_USERNAME, LoginPage.BAD_PASSWORD);
		assertTrue("Should update the url with '?error=true'", driver().getCurrentUrl().contains("login?error=true"));
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Email or Password");
	}

	@Test
	public void testGoodLogin() throws Exception {
		LoginPage.login(driver(), LoginPage.MANAGER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue("The 'test' user is logged in and redirected.", driver().getCurrentUrl().contains("dashboard"));
	}

	@Test
	public void testExpiredCredentialsLogin() throws Exception {
		LoginPage page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		assertTrue("The 'expiredGuy' user should be sent to a password reset page.",
				driver().getCurrentUrl().contains("password_reset/"));
	}

	@Test
	public void testLoginWithChangedCredentials() {
		String newPassword = "aGoodP@ssW0rD";
		LoginPage page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		PasswordResetPage passwordResetPage = new PasswordResetPage(driver());
		passwordResetPage.enterPassword(newPassword, newPassword);
		assertTrue(passwordResetPage.checkSuccess());

		page.logout(driver());
		page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, newPassword);
		assertTrue("The user is logged in and redirected.", driver().getCurrentUrl().contains("dashboard"));
	}
}
