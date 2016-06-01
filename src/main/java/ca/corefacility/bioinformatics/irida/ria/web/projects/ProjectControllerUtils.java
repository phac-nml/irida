package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.permissions.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Common functions for project related controllers
 * 
 *
 */
@Component
public class ProjectControllerUtils {
	// Services
	private final UserService userService;
	
	private final ProjectOwnerPermission projectOwnerPermission;

	@Autowired
	public ProjectControllerUtils(final UserService userService, final ProjectOwnerPermission projectOwnerPermission) {
		this.userService = userService;
		this.projectOwnerPermission = projectOwnerPermission;
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

		boolean isOwner = projectOwnerPermission.isAllowed(SecurityContextHolder.getContext().getAuthentication(),
				project);

		model.addAttribute("isOwner", isOwner);
	}
}
