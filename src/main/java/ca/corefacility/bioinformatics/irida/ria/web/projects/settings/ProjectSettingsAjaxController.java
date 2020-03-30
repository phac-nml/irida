package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectUserTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller to handle all asynchronous call from the project settings UI.
 */
@Controller
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private final ProjectService projectService;
	private final UserService userService;
	private final ProjectOwnerPermission projectOwnerPermission;

	@Autowired
	public ProjectSettingsAjaxController(ProjectService projectService, ProjectOwnerPermission projectOwnerPermission,
			UserService userService) {
		this.projectService = projectService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.userService = userService;
	}

	/**
	 * Get a list of all projects associated with the current project.  If the user is a manager or administrator, the
	 * list will also contain all projects they have access to.
	 *
	 * @param projectId project identifier for the currently active project
	 * @param principal currently logged in user
	 * @return list of projects
	 */
	@RequestMapping("/associated")
	public List<AssociatedProject> getProjectAssociatedProjects(@PathVariable long projectId, Principal principal) {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean hasPermission = user.getSystemRole()
				.equals(Role.ROLE_ADMIN) || projectOwnerPermission.isAllowed(authentication, project);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);

		List<AssociatedProject> associatedProjects = relatedProjectJoins.stream()
				.map(j -> new AssociatedProject(j.getObject(), true))
				.collect(Collectors.toList());
		List<Long> associatedIds = associatedProjects.stream()
				.map(AssociatedProject::getId)
				.collect(Collectors.toList());

		// If they have permission, show them all their projects so they can add them if they want.
		List<AssociatedProject> unassociatedProjects = new ArrayList<>();
		if (hasPermission) {
			Page<Project> page = projectService.getUnassociatedProjects(project, "", 0, Integer.MAX_VALUE,
					Sort.Direction.ASC, "name");
			page.getContent()
					.forEach(p -> {
						if (!associatedIds.contains(p.getId())) {
							unassociatedProjects.add(new AssociatedProject(p, false));
						}
					});
		}
		return Stream.concat(associatedProjects.stream(), unassociatedProjects.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Remove an associated project from the currently active project
	 *
	 * @param projectId    project identifier for the currently active project
	 * @param associatedId project identifier for the associated project to remove
	 */
	@RequestMapping(value = "/associated/remove", method = RequestMethod.POST)
	public void removeAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.removeRelatedProject(project, associatedProject);
	}

	/**
	 * Create a new associated project within the currently active project
	 *
	 * @param projectId    project identifier for the currently active project
	 * @param associatedId project identifier for the  project to add association
	 */
	@RequestMapping(value = "/associated/add", method = RequestMethod.POST)
	public void addAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.addRelatedProject(project, associatedProject);
	}
}
