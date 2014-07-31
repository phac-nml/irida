package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@Controller
@RequestMapping("/projects")
public class ProjectMembersController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersController.class);

	private static final String ACTIVE_NAV_MEMBERS = "members";
	private static final String PROJECTS_DIR = "projects/";
	private static final String ACTIVE_NAV = "activeNav";

	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";

	private final ProjectControllerUtils projectUtils;
	private final ProjectService projectService;

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	@Autowired
	public ProjectMembersController(ProjectControllerUtils projectUtils, ProjectService projectService) {
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
	@RequestMapping(value = "/{projectId}/members", method = RequestMethod.GET)
	public String getProjectUsersPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_MEMBERS);
		model.addAttribute("projectRoles", projectRoles);
		return PROJECT_MEMBERS_PAGE;
	}
}
