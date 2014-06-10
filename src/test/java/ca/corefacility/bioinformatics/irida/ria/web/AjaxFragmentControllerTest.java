package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link AjaxFragmentController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class AjaxFragmentControllerTest {

	private AjaxFragmentController controller = new AjaxFragmentController();


	@Test
	public void testGetSiteMenu() {
		assertEquals("fragments/sitemenu", controller.getSiteMenu());
	}
}
