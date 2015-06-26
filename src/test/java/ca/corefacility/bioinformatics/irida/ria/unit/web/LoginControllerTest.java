package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.ria.utilities.EmailController;
import ca.corefacility.bioinformatics.irida.ria.web.LoginController;

/**
 * Unit Tests for {@link LoginController}
 *
 */
public class LoginControllerTest {
	private LoginController controller;
	private EmailController emailController;
	
	@Before
	public void setUp() {
		this.emailController = mock(EmailController.class);
		controller = new LoginController(emailController);
	}

	@Test
	public void testShowLoginPage() {
		Model model = new ExtendedModelMap();
		HttpSession ses = mock(HttpSession.class);
		assertEquals("login", controller.showLogin(model, false, null,null, ses));
	}
}
