package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.ria.web.LoginController;

/**
 * Unit Tests for {@link LoginController}
 *
 */
public class LoginControllerTest {
	private LoginController controller = new LoginController();

	@Test
	public void testShowLoginPage() {
		Model model = new ExtendedModelMap();
		assertEquals("login", controller.showLogin(model, false));
	}
}
