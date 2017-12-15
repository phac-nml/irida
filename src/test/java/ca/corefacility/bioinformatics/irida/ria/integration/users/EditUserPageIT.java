package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.EditUserPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/EditUserPageIT.xml")
public class EditUserPageIT extends AbstractIridaUIITChromeDriver {
	private EditUserPage editPage;

	@Before
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
		String updatedName = editPage.getUpdatedUserFirstLastName();
		assertTrue(updatedName.contains(newName));
	}

	@Test
	public void testUpdatePassword() {
		editPage.goTo();
		String newPassword = "paSsW0Rd1!";
		editPage.enterPassword(newPassword, newPassword);
		assertFalse(editPage.hasErrors());
		assertTrue(editPage.isSubmitEnabled());
		editPage.clickSubmit();
		assertTrue(editPage.updateSuccess());
	}

	@Test
	public void testUpdatePasswordFail() {
		editPage.goTo();
		String newPassword = "paSsW0Rd1!";
		editPage.enterPassword(newPassword, "notthesame");
		assertTrue(editPage.hasErrors());
		assertFalse(editPage.isSubmitEnabled());
	}

}
