package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represent a {@link UserGroup} in the UI
 */
public class UserGroupTableModel extends TableModel {
	private boolean canManage;
	private String description;

	public UserGroupTableModel(UserGroup userGroup, boolean canManage) {
		super(userGroup.getId(), userGroup.getName(), userGroup.getCreatedDate(), userGroup.getModifiedDate());
		this.canManage = canManage;
		this.description = userGroup.getDescription();
	}

	public boolean isCanManage() {
		return canManage;
	}

	public void setCanManage(boolean canManage) {
		this.canManage = canManage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
