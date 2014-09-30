package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
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
	private final RemoteRelatedProjectService remoteRelatedProjectService;

	@Autowired
	public ProjectControllerUtils(ProjectService projectService, SampleService sampleService, UserService userService,
			RemoteRelatedProjectService remoteRelatedProjectService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.remoteRelatedProjectService = remoteRelatedProjectService;
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

		int relatedCount = projectService.getRelatedProjects(project).size();
		int remoteRelatedCount = remoteRelatedProjectService.getRemoteProjectsForProject(project).size();
		model.addAttribute("related_project_count", relatedCount + remoteRelatedCount);

		// TODO: (Josh - 14-06-23) Get list of recent activities on project.
	}
}
