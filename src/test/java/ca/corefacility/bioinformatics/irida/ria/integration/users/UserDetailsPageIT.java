package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.openqa.selenium.ElementNotInteractableException;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserDetailsPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private UserDetailsPage userPage;

	@Test
	public void testUpdateFirstName() {
		LoginPage.loginAsManager(driver());
		userPage = new UserDetailsPage(driver());
		userPage.goTo(UserDetailsPage.DETAILS_PAGE);
		String newName = "newFirstName";
		userPage.enterFirstName(newName);
		assertFalse(userPage.hasErrors());
		userPage.clickSubmit();
		assertTrue(userPage.updateSuccess());
	}

	@Test
	public void testUpdateInvalidEmail() {
		LoginPage.loginAsManager(driver());
		userPage = new UserDetailsPage(driver());
		userPage.goTo(UserDetailsPage.DETAILS_PAGE);
		String newEmail = "new@email.com?";
		userPage.enterEmail(newEmail);
		assertTrue(userPage.hasErrors());
	}

	@Test
	public void testUpdateEmailAlreadyExists() {
		LoginPage.loginAsManager(driver());
		userPage = new UserDetailsPage(driver());
		userPage.goTo(UserDetailsPage.DETAILS_PAGE);
		String newEmail = "differentUser@nowhere.com";
		userPage.enterEmail(newEmail);
		userPage.clickSubmit();
		assertTrue(userPage.hasErrors());
	}

	@Test
	public void testUpdateLdap() {
		LoginPage.loginAsAdmin(driver());
		userPage = new UserDetailsPage(driver());
		userPage.goTo(UserDetailsPage.DETAILS_PAGE_LDAP);
		assertThrows(ElementNotInteractableException.class, () -> {
			String newName = "newFirstName";
			userPage.enterFirstName(newName);
		});
		assertThrows(ElementNotInteractableException.class, () -> {
			String newName = "newLastName";
			userPage.enterLastName(newName);
		});
		assertThrows(ElementNotInteractableException.class, () -> {
			String newEmail = "newEmail@email.email";
			userPage.enterEmail(newEmail);
		});
		assertThrows(ElementNotInteractableException.class, () -> {
			String newPhone = "1234";
			userPage.enterPhone(newPhone);
		});
		assertThrows(ElementNotInteractableException.class, () -> {
			userPage.clickEnabledCheckbox();
		});

		userPage.clickSubmit();
		assertTrue(userPage.updateSuccess());
	}

}
