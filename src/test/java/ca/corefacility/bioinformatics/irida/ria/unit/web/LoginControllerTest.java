package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.ria.web.LoginController;
import ca.corefacility.bioinformatics.irida.service.EmailController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unit Tests for {@link LoginController}
 *
 */
public class LoginControllerTest {
	private LoginController controller;
	private EmailController emailController;

	@BeforeEach
	public void setUp() {
		this.emailController = mock(EmailController.class);
		controller = new LoginController(emailController);
	}

	@Test
	public void testShowLoginPage() {
		Model model = new ExtendedModelMap();
		assertEquals("login", controller.showLogin(model, false, null));
	}
}
