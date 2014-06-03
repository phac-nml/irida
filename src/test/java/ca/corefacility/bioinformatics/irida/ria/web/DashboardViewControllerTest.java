package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests for {@link DashboardViewController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class DashboardViewControllerTest {
	private DashboardViewController controller = new DashboardViewController();

	@Test
	public void testGetDashboardViewNormal() {
		assertEquals("views/dashboard", controller.getDashboardView());
	}

	@Test
	public void testGetDashboardViewMobile() {
		assertEquals("views/dashboard", controller.getDashboardView());
	}
}
