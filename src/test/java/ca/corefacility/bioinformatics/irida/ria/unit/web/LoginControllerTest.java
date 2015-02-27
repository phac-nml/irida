package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.ria.web.LoginController;

/**
 * Unit Tests for {@link LoginController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class LoginControllerTest {
	private LoginController controller = new LoginController();

	@Test
	public void testShowLoginPage() {
		Model model = new ExtendedModelMap();
		HttpSession ses = mock(HttpSession.class);
		assertEquals("login", controller.showLogin(model, false, null, ses));
	}
}
