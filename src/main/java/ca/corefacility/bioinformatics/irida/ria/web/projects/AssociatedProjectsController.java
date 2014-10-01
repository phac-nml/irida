package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsAdminDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Controller
@RequestMapping("/projects")
public class AssociatedProjectsController {
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_ASSOCIATED_PROJECTS = "associated";
	public static final String ASSOCIATED_PROJECTS_PAGE = ProjectsController.PROJECTS_DIR + "associated_projects";
	public static final String EDIT_ASSOCIATED_PROJECTS_PAGE = ProjectsController.PROJECTS_DIR
			+ "associated_projects_edit";

	private final RemoteRelatedProjectService remoteRelatedProjectService;
	private final ProjectService projectService;
	private final ProjectControllerUtils projectControllerUtils;
	private final UserService userService;

	private final Formatter<Date> dateFormatter;

	private Map<Integer, String> SORT_COLUMNS = ImmutableMap
			.of(1, "id", 2, "name", 3, "organism", 4, "createdDate");

	@Autowired
	public AssociatedProjectsController(RemoteRelatedProjectService remoteRelatedProjectService,
			ProjectService projectService, ProjectControllerUtils projectControllerUtils, UserService userService) {

		this.remoteRelatedProjectService = remoteRelatedProjectService;
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.userService = userService;
		dateFormatter = new DateFormatter();
	}

	/**
	 * Get the associated projects for the given project
	 * 
	 * @param projectId
	 *            The ID of the project to get associated projects
	 * @param model
	 *            A model for the view
	 * @return The view name of the assocated projects view
	 */
	@RequestMapping("/{projectId}/associated")
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

		Map<RemoteAPI, List<RemoteRelatedProject>> remoteRelatedProjectsByApi = getRemoteRelatedProjectsByApi(project);
		model.addAttribute("remoteProjectsByApi", remoteRelatedProjectsByApi);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ASSOCIATED_PROJECTS);

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return ASSOCIATED_PROJECTS_PAGE;
	}

	@RequestMapping("/{projectId}/associated/edit")
	public String editAssociatedProjectsForProject(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return EDIT_ASSOCIATED_PROJECTS_PAGE;
	}

	@RequestMapping("/{projectId}/associated/ajax/available")
	@ResponseBody
	public Map<String, Object> getPotentialAssociatedProjects(
			@PathVariable Long projectId,
			final Principal principal,
			@RequestParam Integer page,
			@RequestParam Integer count,
			@RequestParam String sortedBy,
			@RequestParam String sortDir,
			@RequestParam(value="search", required=false, defaultValue="") String projectName) {
		//Project project = projectService.read(projectId);
		
		User loggedInUser = userService.getUserByUsername(principal.getName());
		Sort.Direction sortDirection = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		

		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		List<Project> projects;
		long totalElements;
		int totalPages;
		if (isAdmin) {
			String sortString = sortedBy;
			Page<Project> search = projectService.search(ProjectSpecification.searchProjectName(projectName), page, count, sortDirection, sortString);
			totalElements = search.getTotalElements();
			totalPages = search.getTotalPages();
			projects = search.getContent();
		} else {
			String sortString = "project." + sortedBy;
			Page<ProjectUserJoin> searchProjectUsers = projectService.searchProjectUsers(ProjectUserJoinSpecification.searchProjectNameWithUser(projectName, loggedInUser), page, count, sortDirection, sortString);
			totalElements = searchProjectUsers.getTotalElements();
			totalPages = searchProjectUsers.getTotalPages();
			projects = new ArrayList<>();
			searchProjectUsers.forEach((puj) -> projects.add(puj.getSubject()));
		}
		
		Map<String, Object> map = getProjectsDataMap(projects);
		map.put("totalAssociated", totalElements);
		map.put("totalPages", totalPages);

		return map;
	}

	/**
	 * Find all projects that have been associated with a project.
	 *
	 * @param currentProject
	 *            The project to find the associated projects of.
	 * @param currentUser
	 *            The currently logged in user.
	 * @return List of Maps containing information about the associated
	 *         projects.
	 */
	private List<Map<String, String>> getAssociatedProjectsForProject(Project currentProject, User currentUser,
			boolean isAdmin) {
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(currentProject);

		// Need to know if the user has rights to view the project
		List<Join<Project, User>> userProjectJoin = projectService.getProjectsForUser(currentUser);

		List<Map<String, String>> projects = new ArrayList<>();
		// Create a quick lookup list
		Map<Long, Boolean> usersProjects = new HashMap<>(userProjectJoin.size());
		for (Join<Project, User> join : userProjectJoin) {
			usersProjects.put(join.getSubject().getId(), true);
		}

		for (RelatedProjectJoin rpj : relatedProjectJoins) {
			Project project = rpj.getObject();

			Map<String, String> map = new HashMap<>();
			map.put("name", project.getLabel());
			map.put("id", project.getId().toString());
			map.put("auth", isAdmin || usersProjects.containsKey(project.getId()) ? "authorized" : "");

			// TODO: (Josh - 2014-07-07) Will need to add remote location
			// information here.
			projects.add(map);
		}
		return projects;
	}

	/**
	 * Get the {@link RemoteRelatedProject} sorted by {@link RemoteAPI}
	 * 
	 * @param currentProject
	 *            The current project to get related projects for
	 * @return A Map<RemoteAPI,List<RemoteRelatedProject>> of the relationships
	 */
	private Map<RemoteAPI, List<RemoteRelatedProject>> getRemoteRelatedProjectsByApi(Project currentProject) {
		List<RemoteRelatedProject> remoteProjectsForProject = remoteRelatedProjectService
				.getRemoteProjectsForProject(currentProject);
		Map<RemoteAPI, List<RemoteRelatedProject>> projectsByApi = new HashMap<>();
		for (RemoteRelatedProject p : remoteProjectsForProject) {
			RemoteAPI api = p.getRemoteAPI();
			if (!projectsByApi.containsKey(api)) {
				List<RemoteRelatedProject> list = new ArrayList<>();
				projectsByApi.put(api, list);
			}

			projectsByApi.get(api).add(p);
		}

		return projectsByApi;
	}

	/**
	 * Generates a map of project information for the {@link ProjectsDataTable}
	 *
	 * @param projectList
	 *            a List of {@link ProjectUserJoin} for the current user.
	 * @param draw
	 *            property sent from {@link ProjectsDataTable} as the table to
	 *            render information to.
	 * @param totalElements
	 *            Total number of elements that could go into the table.
	 * @param sortColumn
	 *            Column to sort by.
	 * @param sortDirection
	 *            Direction to sort the column
	 * @return Map containing the information to put into the
	 *         {@link ProjectsDataTable}
	 */
	private Map<String, Object> getProjectsDataMap(Iterable<Project> projectList) {
		Map<String, Object> map = new HashMap<>();

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (Project p : projectList) {
			Map<String, String> convertProjectToMap = convertProjectToMap(p);
			projectsData.add(convertProjectToMap);
		}
		map.put("associated", projectsData);
		return map;
	}

	private Map<String, String> convertProjectToMap(Project project) {
		Map<String, String> projectMap = new HashMap<>();

		projectMap.put("id", project.getId().toString());
		projectMap.put("name", project.getName());
		projectMap.put("organism", project.getOrganism());
		projectMap.put("createdDate", dateFormatter.print(project.getCreatedDate(), LocaleContextHolder.getLocale()));

		return projectMap;
	}
}
