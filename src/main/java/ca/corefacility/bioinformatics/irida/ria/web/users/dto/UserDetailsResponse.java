package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Map;
import java.util.Objects;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an AjaxResponse with user details.
 */
public class UserDetailsResponse extends AjaxResponse {
	private UserDetailsModel userDetails;
	private boolean isAdmin;
	private boolean canEditUserInfo;
	private boolean canEditUserStatus;
	private boolean canChangePassword;
	private boolean canCreatePasswordReset;
	private Map<String, String> errors;

	public UserDetailsResponse(UserDetailsModel userDetails, boolean isAdmin, boolean canEditUserInfo,
			boolean canEditUserStatus, boolean canChangePassword, boolean canCreatePasswordReset) {
		this.userDetails = userDetails;
		this.isAdmin = isAdmin;
		this.canEditUserInfo = canEditUserInfo;
		this.canEditUserStatus = canEditUserStatus;
		this.canChangePassword = canChangePassword;
		this.canCreatePasswordReset = canCreatePasswordReset;
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
		return isAdmin == that.isAdmin && canEditUserInfo == that.canEditUserInfo
				&& canEditUserStatus == that.canEditUserStatus && canChangePassword == that.canChangePassword
				&& canCreatePasswordReset == that.canCreatePasswordReset && Objects.equals(userDetails,
				that.userDetails) && Objects.equals(errors, that.errors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userDetails, isAdmin, canEditUserInfo, canEditUserStatus, canChangePassword,
				canCreatePasswordReset, errors);
	}
}
