package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.EditUserPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/EditUserPageIT.xml")
public class EditUserPageIT extends AbstractIridaUIITPhantomJS {
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
		String updateName = editPage.updateFirstName(newName);
		assertTrue(editPage.updateSuccess());
		assertTrue(updateName.contains(newName));
	}

	@Test
	public void testUpdatePassword() {
		editPage.goTo();
		String newPassword = "paSsW0Rd";
		editPage.updatePassword(newPassword, newPassword);
		assertTrue(editPage.updateSuccess());
	}

	@Test
	public void testUpdatePasswordFail() {
		editPage.goTo();
		String newPassword = "paSsW0Rd";
		editPage.updatePassword(newPassword, "notthesame");
		assertFalse(editPage.updateSuccess());
	}

}
