package ca.corefacility.bioinformatics.irida.ria.web;

import static org.junit.Assert.assertEquals;

import java.security.Principal;

import org.junit.Test;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Unit Test for {@link PageController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PageControllerTest {

	private PageController controller = new PageController();

	@Test
	public void indexPageNormal() {
		SitePreference preference = SitePreference.NORMAL;
		Model model = new ExtendedModelMap();
		Principal principal = () -> "tester";
		assertEquals("index", controller.showIndex(preference, model, principal));
	}

	@Test
	public void indexPageMobile() {
		SitePreference preference = SitePreference.MOBILE;
		Model model = new ExtendedModelMap();
		Principal principal = () -> "tester";
		assertEquals("index", controller.showIndex(preference, model, principal));
	}
}
