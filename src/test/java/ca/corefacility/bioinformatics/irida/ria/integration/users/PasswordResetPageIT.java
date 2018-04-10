package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import org.junit.*;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/PasswordResetPageIT.xml")
public class PasswordResetPageIT extends AbstractIridaUIITChromeDriver {
	private static final String RESET_USER = "differentUser";

	private PasswordResetPage passwordResetPage;

	@Before
	public void setUpTest() {
		// Don't do login here! should be able to go through this without
		// logging in

		passwordResetPage = new PasswordResetPage(driver());
	}

	@After
	@Override
	public void tearDown() {
		// don't log out, we didn't log in!
	}

	@Test
	public void testSetPassword() {
		String password = "Password1!";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertFalse("Password inputs should have no errors", passwordResetPage.hasErrors());
		assertTrue("Submit button should be enabled", passwordResetPage.isSubmitEnabled());
		passwordResetPage.clickSubmit();
		assertTrue("Should have successfully reset password.", passwordResetPage.checkSuccess());
	}

	@Test
	public void testSetUnequalPassword() {
		String password = "Password1!";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, "different1");
		assertTrue("There should be elements with '.t-form-error' due to mismatching passwords",
				passwordResetPage.hasErrors());
		assertFalse("Submit button should be disabled",
				passwordResetPage.isSubmitEnabled());
	}

	@Test
	public void testSetBadPassword() {
		String password = "notcomplex";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertTrue("There should be elements with '.t-form-error' due to password not meeting requirements",
				passwordResetPage.hasErrors());
		assertFalse("Submit button should be disabled", passwordResetPage.isSubmitEnabled());
	}

	@Test
	public void testChangedCredentials() {
		String password = "Password1!";
		// reset password
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertFalse("There should be no '.t-form-error' elements; password should meet all requirements",
				passwordResetPage.hasErrors());
		assertTrue("Submit button should be enabled", passwordResetPage.isSubmitEnabled());
		passwordResetPage.clickSubmit();
		assertTrue("Should have successfully reset password.", passwordResetPage.checkSuccess());

		AbstractPage.logout(driver());
		// try new password
		LoginPage.login(driver(), RESET_USER, password);
		assertTrue("The user is logged in and redirected.", driver().getTitle().contains("Dashboard"));
		LoginPage.logout(driver());
	}
}
