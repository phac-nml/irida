package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.ActivateAccountPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ActivateAccountPageIT.xml")
public class ActivateAccountPageIT extends AbstractIridaUIITChromeDriver {

	private ActivateAccountPage activateAccountPage;
	private PasswordResetPage passwordResetPage;

	private String validActivationID = "XYZ";
	private String invalidActivationID = "XYZA";

	@BeforeEach
	public void setUpTest() {
		activateAccountPage = new ActivateAccountPage(driver());
	}

	@Test
	public void testActivateAccountValidActivationId() {
		activateAccountPage.goTo();
		activateAccountPage.clickActivateAccountLink();
		activateAccountPage.enterActivationID(validActivationID);
		assertTrue(activateAccountPage.isSubmitEnabled(), "Submit button should be enabled");
		activateAccountPage.clickSubmit();
		assertFalse(activateAccountPage.isErrorAlertDisplayed());
		assertTrue(activateAccountPage.passwordResetPageDisplayed(validActivationID), "After entering a valid activation id and clicking submit, the user should be taken to the reset password page");

		passwordResetPage = new PasswordResetPage(driver());

		String password = "Password1!";
		String RESET_USER = "differentUser";

		passwordResetPage.enterPassword(password);
		assertTrue(passwordResetPage.isSubmitEnabled(), "Submit button should be enabled");
		passwordResetPage.clickSubmit();
		assertFalse(passwordResetPage.isErrorAlertDisplayed(),
				"There should be no error alert");
		assertTrue(passwordResetPage.checkSuccess(), "Should have successfully reset password.");

		AbstractPage.logout(driver());
		// try new password
		LoginPage.login(driver(), RESET_USER, password);
		assertTrue(driver().getTitle().contains("Dashboard"), "The user is logged in and redirected.");
		LoginPage.logout(driver());

	}

	@Test
	public void testActivateAccountInvalidActivationId() {
		activateAccountPage.goTo();
		activateAccountPage.clickActivateAccountLink();
		activateAccountPage.enterActivationID("XYZA");
		assertTrue(activateAccountPage.isSubmitEnabled(), "Submit button should be enabled");
		activateAccountPage.clickSubmit();
		assertTrue(activateAccountPage.isErrorAlertDisplayed());
		assertFalse(activateAccountPage.passwordResetPageDisplayed(invalidActivationID), "After entering a valid activation id and clicking submit, the user should be taken to the reset password page");
	}
}
