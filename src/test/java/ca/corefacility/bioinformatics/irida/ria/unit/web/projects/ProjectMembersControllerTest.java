package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectMembersController;

import static org.junit.Assert.assertEquals;

public class ProjectMembersControllerTest {
	ProjectMembersController controller;

	@Before
	public void setUp() {
		controller = new ProjectMembersController();
	}

	@Test
	public void testGetProjectUsersPage() {
		ExtendedModelMap model = new ExtendedModelMap();
		assertEquals("Gets the correct project members page", "projects/settings/pages/members",
				controller.getProjectUsersPage(model));

		assertEquals("Should be the members subpage", model.get("page"), "members");
	}
}
