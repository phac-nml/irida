package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a Project member in the UI.
 */
public class ProjectContactTableModel extends TableModel {
	private final String email;

	public ProjectContactTableModel(User user, String email, Date joinedDate) {
		super(user.getId(), user.getLabel(), null, null);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
