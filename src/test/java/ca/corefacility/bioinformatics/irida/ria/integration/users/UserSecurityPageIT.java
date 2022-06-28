package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserSecurityPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserSecurityPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserSecurityPageIT extends AbstractIridaUIITChromeDriver {
	private UserSecurityPage userPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		userPage = new UserSecurityPage(driver());
	}

	@Test
	public void testChangeUserPasswordGood() {
		String oldPassword = "Password1!";
		String newPassword = "Password2!";
		userPage.goTo();
		userPage.changePassword(oldPassword, newPassword);
		assertTrue(userPage.checkSuccessNotification());
	}

	@Test
	public void testChangeUserPasswordBad() {
		String oldPassword = "Password1!";
		String newPassword = "notagoodpassword";
		userPage.goTo();
		userPage.changePassword(oldPassword, newPassword);
		assertTrue(userPage.hasErrors());
	}

	@Test
	public void testResetUserPassword() {
		userPage.getOtherUser(2L);
		userPage.sendPasswordReset();
		assertTrue(userPage.checkSuccessNotification());
	}

}
