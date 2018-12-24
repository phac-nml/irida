package ca.corefacility.bioinformatics.irida.ria.integration;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue("Should update the url with '?error=true'", driver().getCurrentUrl().contains("login?error=true"));
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Username or Password");
	}

	@Test
	public void testBadPassword() throws Exception {
		LoginPage page = LoginPage.to(driver());
		page.login(LoginPage.USER_USERNAME, LoginPage.BAD_PASSWORD);
		assertTrue("Should update the url with '?error=true'", driver().getCurrentUrl().contains("login?error=true"));
		assertEquals("Should display error on bad login", page.getErrors(), "Incorrect Username or Password");
	}

	@Test
	public void testGoodLogin() throws Exception {
		LoginPage.login(driver(), LoginPage.MANAGER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue("The 'test' user is logged in and redirected.", driver().getTitle().contains("Dashboard"));
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
		passwordResetPage.clickSubmit();
		assertTrue("Should have succeeded in changing password.", passwordResetPage.checkSuccess());

		AbstractPage.logout(driver());
		page = LoginPage.to(driver());
		page.login(EXPIRED_USERNAME, newPassword);
		assertTrue("The user is logged in and redirected.", driver().getTitle().contains("Dashboard"));
	}

	@Test
	public void testSequencerLogin() throws Exception {
		LoginPage.login(driver(), LoginPage.SEQUENCER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertFalse("The sequencer user should not be able to see the dashboard", driver().getTitle().contains("Dashboard"));
		assertTrue("The sequencer user should get access denied", driver().getTitle().contains("Access Denied"));
	}
}
