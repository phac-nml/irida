package ca.corefacility.bioinformatics.irida.ria.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;

/**
 * <p> Integration test to ensure that the Login Page works and redirects the user to the dashboard. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageIT.xml")
public class LoginPageIT extends AbstractIridaUIITChromeDriver {

	private static final String EXPIRED_USERNAME = "expiredGuy";
	private static final String EXPIRED_PASSWORD = "Password1!";

	@Test
	public void testBadUsername() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	@Test
	public void testBadPassword() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(LoginPage.USER_USERNAME, LoginPage.BAD_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	@Test
	public void testGoodLogin() throws Exception {
		LoginPage.login(driver(), LoginPage.MANAGER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'test' user is logged in and redirected.");
	}

	@Test
	public void testExpiredCredentialsLogin() throws Exception {
		LoginPage page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("password_reset/"),
				"The 'expiredGuy' user should be sent to a password reset page.");
	}

	@Test
	public void testLoginWithChangedCredentials() {
		String newPassword = "aGoodP@ssW0rD";
		LoginPage page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
		PasswordResetPage passwordResetPage = new PasswordResetPage(driver());
		passwordResetPage.enterPassword(newPassword);
		passwordResetPage.clickSubmit();
		assertTrue(passwordResetPage.checkSuccess(), "Should have succeeded in changing password.");

		AbstractPage.logout(driver());
		page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, newPassword);
		assertTrue(driver().getTitle().contains("Dashboard"), "The user is logged in and redirected.");
	}

	@Test
	public void testSequencerLogin() throws Exception {
		LoginPage.login(driver(), LoginPage.SEQUENCER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertFalse(driver().getTitle().contains("Dashboard"), "The sequencer user should not be able to see the dashboard");
		assertTrue(driver().getTitle().contains("Access Denied"), "The sequencer user should get access denied");
	}
}
