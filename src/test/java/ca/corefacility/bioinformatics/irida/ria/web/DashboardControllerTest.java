package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for {@link DashboardController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class DashboardControllerTest {

	private DashboardController controller = new DashboardController();

	@Test
	public void indexPageNormal() {
		assertEquals("index", controller.showIndex());
	}
}
