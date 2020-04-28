package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a {@link UserGroup} in the UI.
 */
public class ProjectGroupTableModel extends TableModel {
	private final String role;

	public ProjectGroupTableModel(UserGroup group, String role, Date joinedDate) {
		super(group.getId(), group.getName(), joinedDate, null);
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
