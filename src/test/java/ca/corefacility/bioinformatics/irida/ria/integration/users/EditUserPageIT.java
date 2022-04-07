package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.EditUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/EditUserPageIT.xml")
public class EditUserPageIT extends AbstractIridaUIITChromeDriver {
	private EditUserPage editPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		editPage = new EditUserPage(driver());
	}

	@Test
	public void testUpdateFirstName() {
		editPage.goTo();
		String newName = "newFirstName";
		editPage.enterFirstName(newName);
		assertFalse(editPage.hasErrors());
		editPage.clickSubmit();
		assertTrue(editPage.updateSuccess());
	}

	@Test
	public void testUpdateInvalidEmail() {
		editPage.goTo();
		String newEmail = "new@email.com?";
		editPage.enterEmail(newEmail);
		assertTrue(editPage.hasErrors());
	}

	@Test
	public void testUpdateEmailAlreadyExists() {
		editPage.goTo();
		String newEmail = "differentUser@nowhere.com";
		editPage.enterEmail(newEmail);
		assertTrue(editPage.hasErrors());
	}
}
