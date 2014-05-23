package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for {@link PageController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PageControllerTest {

	private PageController controller = new PageController();

	@Test
	public void loginPageNormal() {
		SitePreference preference = SitePreference.NORMAL;
		assertEquals("login", controller.showLogin(preference));
	}

	@Test
	public void loginPageMobile() {
		SitePreference preference = SitePreference.MOBILE;
		assertEquals("login", controller.showLogin(preference));
	}

	@Test
	public void indexPageNormal(){
		SitePreference preference = SitePreference.NORMAL;
		Model model = new ExtendedModelMap();
		Principal principal = () -> "tester";
		assertEquals("index", controller.showIndex(preference, model, principal));
	}

	@Test
	public void indexPageMobile(){
		SitePreference preference = SitePreference.MOBILE;
		Model model = new ExtendedModelMap();
		Principal principal = () -> "tester";
		assertEquals("index", controller.showIndex(preference, model, principal));
	}
}
