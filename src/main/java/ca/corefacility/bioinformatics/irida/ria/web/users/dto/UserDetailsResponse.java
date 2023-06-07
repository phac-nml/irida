package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

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
	private boolean isOwnAccount;
	private boolean canCreatePasswordReset;
	private boolean isDomainAccount;

	public UserDetailsResponse(UserDetailsModel userDetails, boolean isAdmin, boolean canEditUserInfo,
			boolean canEditUserStatus, boolean isOwnAccount, boolean canCreatePasswordReset, boolean isDomainAccount) {
		this.userDetails = userDetails;
		this.isAdmin = isAdmin;
		this.canEditUserInfo = canEditUserInfo;
		this.canEditUserStatus = canEditUserStatus;
		this.isOwnAccount = isOwnAccount;
		this.canCreatePasswordReset = canCreatePasswordReset;
		this.isDomainAccount = isDomainAccount;
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

	public boolean isDomainAccount(){
		return this.isDomainAccount;
	}

	public void setDomainAccount(boolean isDomainAccount) {
		this.isDomainAccount = isDomainAccount;
	}

	public boolean isOwnAccount() {
		return isOwnAccount;
	}

	public void setOwnAccount(boolean isOwnAccount) {
		this.isOwnAccount = isOwnAccount;
	}

	public boolean isCanCreatePasswordReset() {
		return canCreatePasswordReset;
	}

	public void setCanCreatePasswordReset(boolean canCreatePasswordReset) {
		this.canCreatePasswordReset = canCreatePasswordReset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsResponse that = (UserDetailsResponse) o;
		return isAdmin == that.isAdmin && canEditUserInfo == that.canEditUserInfo
				&& canEditUserStatus == that.canEditUserStatus && isOwnAccount == that.isOwnAccount
				&& canCreatePasswordReset == that.canCreatePasswordReset && Objects.equals(userDetails,
				that.userDetails) && isDomainAccount == that.isDomainAccount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userDetails, isAdmin, canEditUserInfo, canEditUserStatus, isOwnAccount,
				canCreatePasswordReset, isDomainAccount);
	}
}
