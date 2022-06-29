package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	public void testSetPassword() {
		String password = "Password1!";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password);
		assertTrue(passwordResetPage.isSubmitEnabled(), "Submit button should be enabled");
		passwordResetPage.clickSubmit();
		assertFalse(passwordResetPage.isErrorAlertDisplayed(), "There should be no error alert");
		assertTrue(passwordResetPage.checkSuccess(), "Should have successfully reset password.");
	}

	@Test
	public void testSetBadPassword() {
		String password = "notcomplex";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password);
		assertTrue(passwordResetPage.passwordErrorDisplayed(),
				"Error should be displayed that the password does not match the password policy requirements");
		assertFalse(passwordResetPage.isSubmitEnabled(), "Submit button should not be enabled");
	}

	@Test
	public void testChangedCredentials() {
		String password = "Password1!";
		// reset password
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password);
		assertTrue(passwordResetPage.isSubmitEnabled(), "Submit button should be enabled");
		passwordResetPage.clickSubmit();
		assertFalse(passwordResetPage.isErrorAlertDisplayed(), "There should be no error alert");
		assertTrue(passwordResetPage.checkSuccess(), "Should have successfully reset password.");

		AbstractPage.logout(driver());
		// try new password
		LoginPage.login(driver(), RESET_USER, password);
		assertTrue(driver().getTitle().contains("Dashboard"), "The user is logged in and redirected.");
		LoginPage.logout(driver());
	}
}
