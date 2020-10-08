package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to send information about a project in a user group to the UI.
 */
public class UserGroupProjectTableModel extends TableModel {
	private final String role;

	public UserGroupProjectTableModel(UserGroupProjectJoin join, String role) {
		super(join.getSubject()
				.getId(), join.getSubject()
				.getLabel(), join.getCreatedDate(), null);
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
