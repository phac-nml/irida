package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.DashboardController;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit Test for {@link DashboardController}
 * 
 */
public class DashboardControllerTest {

	private DashboardController controller = new DashboardController();

	@Test
	public void indexPageNormal() {
		assertEquals("dashboard", controller.showIndex());
	}
}
