package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a Project member in the UI.
 */
public class ProjectMemberTableModel extends TableModel {
	private final String role;

	public ProjectMemberTableModel(User user, String role, Date joinedDate) {
		super(user.getId(), user.getLabel(), joinedDate, null);
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
