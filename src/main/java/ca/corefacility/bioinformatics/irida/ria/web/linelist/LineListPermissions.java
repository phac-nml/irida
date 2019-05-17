package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Component
public class LineListPermissions {
	private UserService userService;
	private UpdateSamplePermission updateSamplePermission;
	private ProjectOwnerPermission projectOwnerPermission;

	//
	private boolean isAdmin;
	private Authentication authentication;

	@Autowired
	public LineListPermissions(UserService userService, UpdateSamplePermission updateSamplePermission,
			ProjectOwnerPermission projectOwnerPermission) {
		this.userService = userService;
		this.updateSamplePermission = updateSamplePermission;
		this.projectOwnerPermission = projectOwnerPermission;
	}

	public void setPrincipalPermissions(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		this.isAdmin = user.getSystemRole()
				.equals(Role.ROLE_ADMIN);
		this.authentication = SecurityContextHolder.getContext()
				.getAuthentication();
	}

	public boolean canModifySample(Sample sample) {
		return this.isAdmin || updateSamplePermission.isAllowed(this.authentication, sample);
	}

	public boolean canModifyProject(Project project) {
		return this.isAdmin || projectOwnerPermission.isAllowed(authentication, project);
	}
}
