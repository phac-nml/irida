package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;
import org.springframework.mobile.device.site.SitePreference;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ProjectsViewController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsViewControllerTest {
	private ProjectsViewController controller = new ProjectsViewController();

	@Test
	public void testProjectViewNormal() {
		SitePreference preference = SitePreference.NORMAL;
		assertEquals("views/projects", controller.getProjectsView(preference));
	}

	@Test
	public void testProjectViewMobile() {
		SitePreference preference = SitePreference.MOBILE;
		assertEquals("views/404", controller.getProjectsView(preference));
	}
}
