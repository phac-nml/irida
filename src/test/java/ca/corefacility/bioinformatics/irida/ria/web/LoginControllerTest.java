package ca.corefacility.bioinformatics.irida.ria.web;
import org.junit.Test;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.Assert.assertEquals;

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
		assertEquals("login", controller.showLogin(model, false));
	}

	@Test
	public void testShowSplashPage() {
		SitePreference preference = SitePreference.NORMAL;
		assertEquals("splash", controller.showSplash(preference));
	}
}
