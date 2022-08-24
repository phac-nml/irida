package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIAddAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIRemoveAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Service for handling associated projects
 */
@Component
public class UIAssociatedProjectsService {
	private final ProjectService projectService;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final MessageSource messageSource;
	private final UserService userService;

	@Autowired
	public UIAssociatedProjectsService(ProjectService projectService, ProjectOwnerPermission projectOwnerPermission,
			MessageSource messageSource, UserService userService) {
		this.projectService = projectService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.messageSource = messageSource;
		this.userService = userService;
	}

	/**
	 * Get a list of all projects associated with the current project.
	 *
	 * @param projectId project identifier for the currently active project
	 * @return list of projects
	 */
	public List<AssociatedProject> getAssociatedProjectsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);
		return relatedProjectJoins.stream()
				.map(j -> new AssociatedProject(j.getObject(), true))
				.collect(Collectors.toList());
	}

	/**
	 * Get a list of all projects associated with the current project. If the user is a manager or administrator, the
	 * list will also contain all projects they have access to.
	 *
	 * @param projectId project identifier for the currently active project
	 * @return list of projects
	 */
	public List<AssociatedProject> getAssociatedProjects(Long projectId) {
		Project project = projectService.read(projectId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getUserByUsername(authentication.getName());
		boolean hasPermission = user.getSystemRole().equals(Role.ROLE_ADMIN)
				|| projectOwnerPermission.isAllowed(authentication, project);
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
			page.getContent().forEach(p -> {
				if (!associatedIds.contains(p.getId())) {
					unassociatedProjects.add(new AssociatedProject(p, false));
				}
			});
		}
		return Stream.concat(associatedProjects.stream(), unassociatedProjects.stream()).collect(Collectors.toList());
	}

	/**
	 * Create a new associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 * @param locale              currently logged in users locale
	 * @throws UIAddAssociatedProjectException if the project or associated project cannot be found
	 */
	public void addAssociatedProject(Long projectId, Long associatedProjectId, Locale locale)
			throws UIAddAssociatedProjectException {
		try {
			Project project = projectService.read(projectId);
			Project associatedProject = projectService.read(associatedProjectId);
			projectService.addRelatedProject(project, associatedProject);
		} catch (EntityNotFoundException e) {
			throw new UIAddAssociatedProjectException(
					messageSource.getMessage("server.ViewAssociatedProjects.add-error", new Object[] {}, locale));
		}
	}

	/**
	 * Remove an associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 * @param locale              current users locale
	 * @throws UIRemoveAssociatedProjectException if there is an issue removing the associated project
	 */
	public void removeAssociatedProject(Long projectId, Long associatedProjectId, Locale locale)
			throws UIRemoveAssociatedProjectException {
		try {
			Project project = projectService.read(projectId);
			Project associatedProject = projectService.read(associatedProjectId);
			projectService.removeRelatedProject(project, associatedProject);
		} catch (EntityNotFoundException e) {
			throw new UIRemoveAssociatedProjectException(
					messageSource.getMessage("server.ViewAssociatedProjects.remove-error", new Object[] {}, locale));
		}
	}
}
