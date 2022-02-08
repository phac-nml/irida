package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/PasswordResetPageIT.xml")
public class PasswordResetPageIT extends AbstractIridaUIITChromeDriver {
	private static final String RESET_USER = "differentUser";

	private PasswordResetPage passwordResetPage;

	@BeforeEach
	public void setUpTest() {
		// Don't do login here! should be able to go through this without
		// logging in

		passwordResetPage = new PasswordResetPage(driver());
	}

	@AfterEach
	@Override
	public void tearDown() {
		// don't log out, we didn't log in!
	}

	@Test
	public void testSetPassword() {
		String password = "Password1!";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertFalse(passwordResetPage.hasErrors(), "Password inputs should have no errors");
		assertTrue(passwordResetPage.isSubmitEnabled(), "Submit button should be enabled");
		passwordResetPage.clickSubmit();
		assertTrue(passwordResetPage.checkSuccess(), "Should have successfully reset password.");
	}

	@Test
	public void testSetUnequalPassword() {
		String password = "Password1!";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, "different1");
		assertTrue(passwordResetPage.hasErrors(),
				"There should be elements with '.t-form-error' due to mismatching passwords");
		assertFalse(passwordResetPage.isSubmitEnabled(),
				"Submit button should be disabled");
	}

	@Test
	public void testSetBadPassword() {
		String password = "notcomplex";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertTrue(passwordResetPage.hasErrors(),
				"There should be elements with '.t-form-error' due to password not meeting requirements");
		assertFalse(passwordResetPage.isSubmitEnabled(), "Submit button should be disabled");
	}

	@Test
	public void testChangedCredentials() {
		String password = "Password1!";
		// reset password
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertFalse(passwordResetPage.hasErrors(),
				"There should be no '.t-form-error' elements; password should meet all requirements");
		assertTrue(passwordResetPage.isSubmitEnabled(), "Submit button should be enabled");
		passwordResetPage.clickSubmit();
		assertTrue(passwordResetPage.checkSuccess(), "Should have successfully reset password.");

		AbstractPage.logout(driver());
		// try new password
		LoginPage.login(driver(), RESET_USER, password);
		assertTrue(driver().getTitle().contains("Dashboard"), "The user is logged in and redirected.");
		LoginPage.logout(driver());
	}
}
