package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreatePasswordResetPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/PasswordResetPageIT.xml")
public class CreatePasswordResetPageIT extends AbstractIridaUIITChromeDriver {

	private CreatePasswordResetPage passwordResetPage;

	@BeforeEach
	public void setUpTest() {
		// Don't do login here! should be able to go through this without
		// logging in

		passwordResetPage = new CreatePasswordResetPage(driver());
	}

	@After
	@Override
	public void tearDown() {
		// don't log out, we didn't log in!
	}

	@Test
	public void testCreateReset() {
		passwordResetPage.goTo();
		String email = "differentUser@nowhere.com";
		passwordResetPage.enterEmail(email);
		assertTrue(passwordResetPage.checkSuccess());
	}

	@Test
	public void testCreateResetBadEmail() {
		passwordResetPage.goTo();
		String email = "notauser@nowhere.com";
		passwordResetPage.enterEmail(email);
		assertFalse(passwordResetPage.checkSuccess());
	}
}
