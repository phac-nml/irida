package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * UI Model for user groups that are associated with a project
 */
public class ProjectUserGroupsTableModel extends TableModel {
	private final String role;
	private final String description;

	public ProjectUserGroupsTableModel(UserGroup group, String role, Date dateAdded) {
		super(group.getId(), group.getLabel(), dateAdded, null);
		this.description = group.getDescription();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public String getDescription() {
		return description;
	}
}
