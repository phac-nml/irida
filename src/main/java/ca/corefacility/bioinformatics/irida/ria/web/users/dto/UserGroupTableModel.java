package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * UI representation of {@link UserGroup}
 */
public class UserGroupTableModel extends TableModel {
	private String description;
	private int members;
	private boolean canManage;

	public UserGroupTableModel(UserGroup group, int members, boolean canManage) {
		super(group.getId(), group.getName(), group.getCreatedDate(), group.getModifiedDate());
		this.description = group.getDescription();
		this.members = members;
		this.canManage = canManage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMembers() {
		return members;
	}

	public void setMembers(int members) {
		this.members = members;
	}

	public boolean isCanManage() {
		return canManage;
	}

	public void setCanManage(boolean canManage) {
		this.canManage = canManage;
	}
}
