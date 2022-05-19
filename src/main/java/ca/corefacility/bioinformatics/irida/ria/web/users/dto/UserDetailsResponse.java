package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Map;
import java.util.Objects;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an AjaxResponse with user details.
 */
public class UserDetailsResponse extends AjaxResponse {

	private UserDetailsModel userDetails;
	private String currentRole;
	private boolean mailFailure;
	private boolean isAdmin;
	private boolean canEditUserInfo;
	private boolean canEditUserStatus;
	private boolean canChangePassword;
	private boolean canCreatePasswordReset;
	private Map<String, String> errors;

	public UserDetailsResponse(UserDetailsModel userDetails, String currentRole, boolean mailFailure, boolean isAdmin,
			boolean canEditUserInfo, boolean canEditUserStatus, boolean canChangePassword,
			boolean canCreatePasswordReset) {
		this.userDetails = userDetails;
		this.currentRole = currentRole;
		this.mailFailure = mailFailure;
		this.isAdmin = isAdmin;
		this.canEditUserInfo = canEditUserInfo;
		this.canEditUserStatus = canEditUserStatus;
		this.canChangePassword = canChangePassword;
		this.canCreatePasswordReset = canCreatePasswordReset;
	}

	public UserDetailsResponse(boolean mailFailure, Map<String, String> errors) {
		this.mailFailure = mailFailure;
		this.errors = errors;
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

	public boolean isCanEditUserInfo() {
		return canEditUserInfo;
	}

	public void setCanEditUserInfo(boolean canEditUserInfo) {
		this.canEditUserInfo = canEditUserInfo;
	}

	public boolean isCanEditUserStatus() {
		return canEditUserStatus;
	}

	public void setCanEditUserStatus(boolean canEditUserStatus) {
		this.canEditUserStatus = canEditUserStatus;
	}

	public boolean isCanChangePassword() {
		return canChangePassword;
	}

	public void setCanChangePassword(boolean canChangePassword) {
		this.canChangePassword = canChangePassword;
	}

	public boolean isCanCreatePasswordReset() {
		return canCreatePasswordReset;
	}

	public void setCanCreatePasswordReset(boolean canCreatePasswordReset) {
		this.canCreatePasswordReset = canCreatePasswordReset;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsResponse that = (UserDetailsResponse) o;
		return mailFailure == that.mailFailure && isAdmin == that.isAdmin && canEditUserInfo == that.canEditUserInfo
				&& canEditUserStatus == that.canEditUserStatus && canChangePassword == that.canChangePassword
				&& canCreatePasswordReset == that.canCreatePasswordReset && Objects.equals(userDetails,
				that.userDetails) && Objects.equals(currentRole, that.currentRole) && Objects.equals(errors,
				that.errors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userDetails, currentRole, mailFailure, isAdmin, canEditUserInfo, canEditUserStatus,
				canChangePassword, canCreatePasswordReset, errors);
	}
}
