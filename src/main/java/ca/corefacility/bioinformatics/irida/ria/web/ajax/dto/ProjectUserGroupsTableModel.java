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
	private final String metadataRole;

	public ProjectUserGroupsTableModel(UserGroup group, String role, String metadataRole, Date dateAdded) {
		super(group.getId(), group.getLabel(), dateAdded, null);
		this.description = group.getDescription();
		this.role = role;
		this.metadataRole = metadataRole;
	}

	public String getRole() {
		return role;
	}

	public String getDescription() {
		return description;
	}

	public String getMetadataRole() {
		return metadataRole;
	}
}
