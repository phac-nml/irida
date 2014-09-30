package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
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

	@Autowired
	public AssociatedProjectsController(RemoteRelatedProjectService remoteRelatedProjectService,
			ProjectService projectService, ProjectControllerUtils projectControllerUtils, UserService userService) {

		this.remoteRelatedProjectService = remoteRelatedProjectService;
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.userService = userService;
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
}
