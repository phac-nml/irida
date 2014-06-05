package ca.corefacility.bioinformatics.irida.ria.web;

import static org.junit.Assert.assertEquals;

import java.security.Principal;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Unit Test for {@link DashboardController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class DashboardControllerTest {

	private DashboardController controller = new DashboardController();

	@Test
	public void indexPageNormal() {
		Model model = new ExtendedModelMap();
		Principal principal = () -> "tester";
		assertEquals("index", controller.showIndex(model, principal));
	}
}
