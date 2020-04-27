package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@Controller
public class ProjectBaseController {
	private ProjectService projectService;
	private ProjectOwnerPermission projectOwnerPermission;
	private ManageLocalProjectSettingsPermission manageLocalProjectSettingsPermission;

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Autowired
	public void setProjectOwnerPermission(ProjectOwnerPermission projectOwnerPermission) {
		this.projectOwnerPermission = projectOwnerPermission;
	}

	@Autowired
	public void setManageLocalProjectSettingsPermission(
			ManageLocalProjectSettingsPermission manageLocalProjectSettingsPermission) {
		this.manageLocalProjectSettingsPermission = manageLocalProjectSettingsPermission;
	}

	@ModelAttribute("project")
	public Project getProject(@PathVariable Long projectId) {
		return projectService.read(projectId);
	}

	@ModelAttribute("isAdmin")
	public boolean getIsAdmin() {
		User user = (User) SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
		return user.getSystemRole()
				.equals(Role.ROLE_ADMIN);
	}

	@ModelAttribute("isOwner")
	public boolean isOwner(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return projectOwnerPermission.isAllowed(authentication, project);
	}

	@ModelAttribute("isOwnerAllowRemote")
	public boolean getIsOwnerAllowRemote(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return manageLocalProjectSettingsPermission.isAllowed(authentication, project);
	}

	@ModelAttribute("manageMembers")
	public boolean getManageMembers(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return manageLocalProjectSettingsPermission.isAllowed(authentication, project);
	}
}
