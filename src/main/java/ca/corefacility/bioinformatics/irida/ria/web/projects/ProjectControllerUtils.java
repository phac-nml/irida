package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Common functions for project related controllers
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Component
public class ProjectControllerUtils {
	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;

	@Autowired
	public ProjectControllerUtils(ProjectService projectService, SampleService sampleService, UserService userService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
	}

	/**
	 * Adds to the current view model default template information:
	 * <ul>
	 * <li>Sidebar Information</li>
	 * <li>If the current user is an admin</li>
	 * </ul>
	 * 
	 * @param model
	 *            {@link Model} for the current view.
	 * @param principal
	 *            {@link Principal} currently logged in user.
	 * @param project
	 *            {@link} current project viewed.
	 */
	public void getProjectTemplateDetails(Model model, Principal principal, Project project) {
		User loggedInUser = userService.getUserByUsername(principal.getName());

		// Determine if the user is an owner or admin.
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);

		// Find out who the owner of the project is.
		Collection<Join<Project, User>> ownerJoinList = userService.getUsersForProjectByRole(project,
				ProjectRole.PROJECT_OWNER);
		boolean isOwner = false;
		for (Join<Project, User> owner : ownerJoinList) {
			if (loggedInUser.equals(owner.getObject())) {
				isOwner = true;
			}
		}

		model.addAttribute("isOwner", isOwner);

		int sampleSize = sampleService.getSamplesForProject(project).size();
		model.addAttribute("samples", sampleSize);

		int userSize = userService.getUsersForProject(project).size();
		model.addAttribute("users", userSize);

		// TODO: (Josh - 14-06-23) Get list of recent activities on project.

		// Add any associated projects
		User currentUser = userService.getUserByUsername(principal.getName());
		List<Map<String, String>> associatedProjects = getAssociatedProjects(project, currentUser, isAdmin);
		model.addAttribute("associatedProjects", associatedProjects);

		List<RemoteRelatedProject> remoteProjectsForProject = projectService.getRemoteProjectsForProject(project);
		model.addAttribute("remoteAssociatedProjects", remoteProjectsForProject);

		Map<RemoteAPI, List<RemoteRelatedProject>> remoteRelatedProjectsByApi = getRemoteRelatedProjectsByApi(project);
		model.addAttribute("remoteProjectsByApi", remoteRelatedProjectsByApi);
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
	private List<Map<String, String>> getAssociatedProjects(Project currentProject, User currentUser, boolean isAdmin) {
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

	private Map<RemoteAPI, List<RemoteRelatedProject>> getRemoteRelatedProjectsByApi(Project currentProject) {
		List<RemoteRelatedProject> remoteProjectsForProject = projectService
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
