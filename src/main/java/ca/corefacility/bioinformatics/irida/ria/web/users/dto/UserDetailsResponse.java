package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an AjaxResponse with user details.
 */
public class UserDetailsResponse extends AjaxResponse {

	private UserDetailsModel userDetails;
	private String systemRole;
	private List<Map<String, Object>> projects;
	private boolean mailConfigured;
	private boolean mailFailure;
	private boolean canEditUser;
	private boolean canCreatePasswordReset;

	public UserDetailsModel getUser() {
		return userDetails;
	}

	public void setUser(UserDetailsModel userDetails) {
		this.userDetails = userDetails;
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
