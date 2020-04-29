package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;

/**
 * Controller for handling project/members views and functions
 */
@Controller
@RequestMapping("/projects")
public class ProjectMembersController extends ProjectBaseController {

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model {@link Model}
	 * @return The name of the project members page.
	 */
	@RequestMapping("/{projectId}/settings/members")
	public String getProjectUsersPage(final Model model) {
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "members");
		return "projects/settings/pages/members";
	}

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model {@link Model}
	 * @return The name of the project members page.
	 */
	@RequestMapping(value = "/{projectId}/settings/groups", method = RequestMethod.GET)
	public String getProjectGroupsPage(final Model model) {
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "groups");
		return "projects/settings/pages/groups";
	}
}
