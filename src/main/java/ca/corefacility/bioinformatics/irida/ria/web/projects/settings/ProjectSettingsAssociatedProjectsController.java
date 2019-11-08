package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for handling associated projects in a project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings/associated")
@Scope("session")
public class ProjectSettingsAssociatedProjectsController {

	public static final String ASSOCIATED_PROJECTS_PAGE = ProjectsController.PROJECTS_DIR + "associated_projects";
	public static final String EDIT_ASSOCIATED_PROJECTS_PAGE =
			ProjectsController.PROJECTS_DIR + "associated_projects_edit";

	private final ProjectService projectService;
	private final ProjectControllerUtils projectControllerUtils;
	private final UserService userService;
	private final MessageSource messageSource;

	private final Formatter<Date> dateFormatter;

	@Autowired
	public ProjectSettingsAssociatedProjectsController(ProjectService projectService,
			ProjectControllerUtils projectControllerUtils, UserService userService, MessageSource messageSource) {

		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.userService = userService;
		this.messageSource = messageSource;
		dateFormatter = new DateFormatter();
	}

	/**
	 * Get the associated projects for the given project
	 *
	 * @param projectId
	 * 		The ID of the project to get associated projects
	 * @param model
	 * 		A model for the view
	 * @param principal
	 * 		a reference to the logged in user.
	 *
	 * @return The view name of the associated projects view
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getAssociatedProjectsPage(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		User loggedInUser = userService.getUserByUsername(principal.getName());

		// Determine if the user is an owner or admin.
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);

		// Add any associated projects
		User currentUser = userService.getUserByUsername(principal.getName());
		List<Map<String, String>> associatedProjects = getAssociatedProjectsForProject(project, currentUser, isAdmin);
		model.addAttribute("associatedProjects", associatedProjects);

		model.addAttribute("noAssociated", associatedProjects.isEmpty());

		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "associated");

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/associated";
	}

	/**
	 * Get a list of the local associated projects.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 *
	 * @return a list of the associated projects for the given project
	 */
	@RequestMapping("/ajax/associated")
	public @ResponseBody
	List<Project> ajaxAssociatedProjects(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);
		return relatedProjectJoins.stream().map(RelatedProjectJoin::getObject).collect(Collectors.toList());
	}

	/**
	 * Add an associated project to a project
	 *
	 * @param projectId           The subject project id
	 * @param associatedProjectId The associated project id
	 * @param locale              Locale of the logged in user
	 * @return "success" if the request was successful
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addAssociatedProject(@PathVariable Long projectId,
			@RequestParam Long associatedProjectId, Locale locale) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedProjectId);

		projectService.addRelatedProject(project, associatedProject);

		return ImmutableMap.of("result", "success", "message",
				messageSource.getMessage("project.associated.added", new Object[] {}, locale));
	}

	/**
	 * Delete an associated project to a project
	 *
	 * @param projectId           The subject project id
	 * @param associatedProjectId The associated project id
	 * @param locale              Locale of the logged in user
	 * @return "success" if the request was successful
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> removeAssociatedProject(@PathVariable Long projectId,
			@RequestParam Long associatedProjectId, Locale locale) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedProjectId);

		projectService.removeRelatedProject(project, associatedProject);

		return ImmutableMap.of("result", "success", "message",
				messageSource.getMessage("project.associated.removed", new Object[] {}, locale));
	}

	/**
	 * Get the edit associated projects page
	 *
	 * @param projectId
	 * 		The ID of the current project
	 * @param model
	 * 		Model object to be passed to the view
	 * @param principal
	 * 		The logged in user
	 *
	 * @return The name of the edit associated projects view
	 */
	@RequestMapping("/edit")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	public String editAssociatedProjectsForProject(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);

		return "projects/settings/pages/associated_edit";
	}

	/**
	 * Get {@link Project}s that could be associated with this project
	 *
	 * @param projectId
	 * 		The current project ID
	 * @param principal
	 * 		The logged in user
	 * @param page
	 * 		The page to request
	 * @param count
	 * 		The number of elements in the page
	 * @param sortedBy
	 * 		The property to sort by
	 * @param sortDir
	 * 		The direction to sort in
	 * @param projectName
	 * 		The project name to search for
	 *
	 * @return A {@code Map<String,Object>} of elements for a datatable
	 */
	@RequestMapping("/ajax/available")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	@ResponseBody
	public Map<String, Object> getPotentialAssociatedProjects(@PathVariable Long projectId, final Principal principal,
			@RequestParam Integer page, @RequestParam Integer count, @RequestParam String sortedBy,
			@RequestParam String sortDir,
			@RequestParam(value = "name", required = false, defaultValue = "") String projectName) {
		Project project = projectService.read(projectId);

		Sort.Direction sortDirection = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);

		List<Project> projects;
		long totalElements;
		int totalPages;

		final Page<Project> search = projectService
				.getUnassociatedProjects(project, projectName, page, count, sortDirection, sortedBy);

		totalElements = search.getTotalElements();
		totalPages = search.getTotalPages();
		projects = search.getContent();

		Map<String, Object> map = getProjectsDataMap(projects, relatedProjectJoins);
		map.put("totalAssociated", totalElements);
		map.put("totalPages", totalPages);

		return map;
	}

	/**
	 * Find all projects that have been associated with a project.
	 *
	 * @param currentProject
	 * 		The project to find the associated projects of.
	 * @param currentUser
	 * 		The currently logged in user.
	 *
	 * @return List of Maps containing information about the associated projects.
	 */
	private List<Map<String, String>> getAssociatedProjectsForProject(Project currentProject, User currentUser,
			boolean isAdmin) {
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(currentProject);

		List<Map<String, String>> projects = new ArrayList<>();

		for (RelatedProjectJoin rpj : relatedProjectJoins) {
			Project project = rpj.getObject();

			Map<String, String> map = new HashMap<>();
			map.put("name", project.getLabel());
			map.put("id", project.getId().toString());
			map.put("auth", "authorized");

			projects.add(map);
		}
		return projects;
	}

	/**
	 * Generates a map of project information.
	 *
	 * @param projectList
	 * 		a List of {@link ProjectUserJoin} for the current user.
	 *
	 * @return Map containing the information to put the projects table
	 */
	private Map<String, Object> getProjectsDataMap(Iterable<Project> projectList,
			List<RelatedProjectJoin> relatedProjectJoins) {
		Map<String, Object> map = new HashMap<>();

		Map<Project, Boolean> related = new HashMap<>();

		relatedProjectJoins.forEach((p) -> related.put(p.getObject(), true));

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (Project project : projectList) {
			Map<String, String> projectMap = new HashMap<>();
			projectMap.put("id", project.getId().toString());
			projectMap.put("name", project.getName());
			projectMap.put("organism", project.getOrganism());
			projectMap
					.put("createdDate", dateFormatter.print(project.getCreatedDate(), LocaleContextHolder.getLocale()));

			if (related.containsKey(project)) {
				projectMap.put("associated", "associated");
			}

			projectsData.add(projectMap);
		}
		map.put("associated", projectsData);
		return map;
	}

	/**
	 * Handle entity exists exceptions for creating {@link RelatedProjectJoin}s
	 *
	 * @param ex
	 * 		the exception to handle.
	 *
	 * @return a {@link ResponseEntity} to render the exception to the client.
	 */
	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<String> handleEntityExistsException(EntityExistsException ex) {
		return new ResponseEntity<>("This relationship already exists.", HttpStatus.CONFLICT);
	}

}
