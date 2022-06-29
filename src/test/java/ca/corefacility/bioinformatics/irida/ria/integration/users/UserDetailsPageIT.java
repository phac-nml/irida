package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserDetailsPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private UserDetailsPage userPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		userPage = new UserDetailsPage(driver());
	}

	@Test
	public void testUpdateFirstName() {
		userPage.goTo();
		String newName = "newFirstName";
		userPage.enterFirstName(newName);
		assertFalse(userPage.hasErrors());
		userPage.clickSubmit();
		assertTrue(userPage.updateSuccess());
	}

	@Test
	public void testUpdateInvalidEmail() {
		userPage.goTo();
		String newEmail = "new@email.com?";
		userPage.enterEmail(newEmail);
		assertTrue(userPage.hasErrors());
	}

	@Test
	public void testUpdateEmailAlreadyExists() {
		userPage.goTo();
		String newEmail = "differentUser@nowhere.com";
		userPage.enterEmail(newEmail);
		userPage.clickSubmit();
		assertTrue(userPage.hasErrors());
	}

}
