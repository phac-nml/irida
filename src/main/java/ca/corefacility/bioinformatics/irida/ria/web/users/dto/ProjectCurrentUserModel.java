package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Details about how the current user can interact with a specific project
 */
public class ProjectCurrentUserModel {
	private  boolean canManage;

	public ProjectCurrentUserModel() {
	}

	public void setCanManage(boolean canManage) {
		this.canManage = canManage;
	}

	public boolean isCanManage() {
		return canManage;
	}
}
