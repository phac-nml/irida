package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserSecurityPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserSecurityPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserSecurityPageIT extends AbstractIridaUIITChromeDriver {
	private UserSecurityPage userPage;

	@BeforeEach
	public void setUpTest() {
		userPage = new UserSecurityPage(driver());
	}

	@Test
	public void testChangeUserPasswordGood() {
		String oldPassword = "Password1!";
		String newPassword = "Password2!";
		LoginPage.loginAsManager(driver());
		userPage.goTo(UserSecurityPage.SECURITY_PAGE);
		userPage.changePassword(oldPassword, newPassword);
		assertTrue(userPage.checkSuccessNotification());
	}

	@Test
	public void testChangeUserPasswordBad() {
		String oldPassword = "Password1!";
		String newPassword = "notagoodpassword";
		LoginPage.loginAsManager(driver());
		userPage.goTo(UserSecurityPage.SECURITY_PAGE);
		userPage.changePassword(oldPassword, newPassword);
		assertTrue(userPage.hasErrors());
	}

	@Test
	public void testResetUserPassword() {
		LoginPage.loginAsAdmin(driver());
		userPage.getOtherUser(2L);
		userPage.sendPasswordReset();
		assertTrue(userPage.checkSuccessNotification());
	}

	@Test
	public void testChangeUserPasswordLdap() {
		String oldPassword = "Password1!";
		String newPassword = "Password2!";
		LoginPage.loginAsAdmin(driver());
		userPage.goTo(UserSecurityPage.SECURITY_PAGE_LDAP);
		assertThrows(NoSuchElementException.class, () -> {
			userPage.changePassword(oldPassword, newPassword);
		});
	}

}
