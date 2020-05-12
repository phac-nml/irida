package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

public class ProjectUserGroupTableModel extends TableModel {
	private final String role;
	private final boolean canManage;

	public ProjectUserGroupTableModel(UserGroup group, Date dateAdded, String role, boolean canManage) {
		super(group.getId(), group.getLabel(), dateAdded, null);
		this.role = role;
		this.canManage = canManage;
	}

	public String getRole() {
		return role;
	}

	public boolean isCanManage() {
		return canManage;
	}
}
