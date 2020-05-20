package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableList;

/**
 * Controller for handling project/members views and functions
 */
@Controller
@RequestMapping("/projects")
public class ProjectMembersController {
	private final ProjectControllerUtils projectUtils;
	private final ProjectService projectService;

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	@Autowired
	public ProjectMembersController(final ProjectControllerUtils projectUtils, final ProjectService projectService) {
		this.projectUtils = projectUtils;
		this.projectService = projectService;
	}

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param principal
	 *            {@link Principal}
	 * @param projectId
	 *            Id for the project to show the users for
	 * @return The name of the project members page.
	 */
	@RequestMapping(value = { "/{projectId}/settings/members", "/{projectId}/settings/members/edit" })
	public String getProjectUsersPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		projectUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("projectRoles", projectRoles);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "members");
		return "projects/settings/pages/members";
	}

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param principal
	 *            {@link Principal}
	 * @param projectId
	 *            Id for the project to show the users for
	 * @return The name of the project members page.
	 */
	@RequestMapping(value = "/{projectId}/settings/user-groups", method = RequestMethod.GET)
	public String getProjectGroupsPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("projectRoles", projectRoles);
		model.addAttribute("page", "groups");
		return "projects/settings/pages/groups";
	}
}
