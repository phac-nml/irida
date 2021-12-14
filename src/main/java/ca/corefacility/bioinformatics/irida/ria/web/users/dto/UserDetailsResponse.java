package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Map;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an AjaxResponse with user details.
 */
public class UserDetailsResponse extends AjaxResponse {

	private UserDetailsModel userDetails;
	private String currentRole;
	private boolean mailConfigured;
	private boolean mailFailure;
	private boolean isAdmin;
	private boolean canEditUser;
	private boolean canCreatePasswordReset;
	private Map<String, String> locales;
	private Map<String, String> allowedRoles;
	private Map<String, String> errors;

	public UserDetailsResponse(UserDetailsModel userDetails, String currentRole, boolean mailConfigured,
			boolean mailFailure, boolean isAdmin, boolean canEditUser, boolean canCreatePasswordReset,
			Map<String, String> locales, Map<String, String> allowedRoles) {
		this.userDetails = userDetails;
		this.currentRole = currentRole;
		this.mailConfigured = mailConfigured;
		this.mailFailure = mailFailure;
		this.isAdmin = isAdmin;
		this.canEditUser = canEditUser;
		this.canCreatePasswordReset = canCreatePasswordReset;
		this.locales = locales;
		this.allowedRoles = allowedRoles;
	}

	public UserDetailsResponse(Map<String, String> errors) {
		this.errors = errors;
	}

	public UserDetailsModel getUser() {
		return userDetails;
	}

	public void setUser(UserDetailsModel userDetails) {
		this.userDetails = userDetails;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
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

	public Map<String, String> getLocales() {
		return locales;
	}

	public void setLocales(Map<String, String> locales) {
		this.locales = locales;
	}

	public Map<String, String> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(Map<String, String> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	/**
	 * Returns whether there are errors
	 *
	 * @return if there is an error
	 */
	public boolean hasErrors() {
		return errors != null && !errors.isEmpty();
	}
}
