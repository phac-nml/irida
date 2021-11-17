package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an AjaxResponse with user details.
 */
public class UserDetailsResponse extends AjaxResponse {
	private User user;
	private String systemRole;
	private List<Map<String, Object>> projects;
	private boolean mailConfigured;
	private boolean mailFailure;
	private boolean canEditUser;
	private boolean canCreatePasswordReset;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSystemRole() {
		return systemRole;
	}

	public void setSystemRole(String systemRole) {
		this.systemRole = systemRole;
	}

	public List<Map<String, Object>> getProjects() {
		return projects;
	}

	public void setProjects(List<Map<String, Object>> projects) {
		this.projects = projects;
	}

	public boolean isMailConfigured() {
		return mailConfigured;
	}

	public void setMailConfigured(boolean mailConfigured) {
		this.mailConfigured = mailConfigured;
	}

	public boolean isMailFailure() {
		return mailFailure;
	}

	public void setMailFailure(boolean mailFailure) {
		this.mailFailure = mailFailure;
	}

	public boolean isCanEditUser() {
		return canEditUser;
	}

	public void setCanEditUser(boolean canEditUser) {
		this.canEditUser = canEditUser;
	}

	public boolean isCanCreatePasswordReset() {
		return canCreatePasswordReset;
	}

	public void setCanCreatePasswordReset(boolean canCreatePasswordReset) {
		this.canCreatePasswordReset = canCreatePasswordReset;
	}
}
