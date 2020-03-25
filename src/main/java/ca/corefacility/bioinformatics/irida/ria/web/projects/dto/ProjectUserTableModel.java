package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

public class ProjectUserTableModel extends TableModel {
	private final String role;

	public ProjectUserTableModel(User user, String role, Date joinedDate) {
		super(user.getId(), user.getLabel(), joinedDate, null);
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
