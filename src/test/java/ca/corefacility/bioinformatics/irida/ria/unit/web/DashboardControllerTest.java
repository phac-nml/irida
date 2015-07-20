package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.ria.web.DashboardController;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for {@link DashboardController}
 * 
 */
public class DashboardControllerTest {

	private DashboardController controller = new DashboardController();

	@Test
	public void indexPageNormal() {
		Model model = new ExtendedModelMap();
		assertEquals("index", controller.showIndex(model));
	}
}
