package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;

/**
 * This overrides the default {@link TableRequest} for the administrators
 * users table to allow for custom handling of setting the sort column
 * since several columns do not directly match up.
 */
public class AdminUsersTableRequest extends TableRequest {
	@Override
	public void setSortColumn(String sortColumn) {
		switch (sortColumn) {
		case "name":
			super.setSortColumn("username");
			return;
		case "role":
			super.setSortColumn("systemRole");
			return;
		case "type":
			super.setSortColumn("userType");
			return;
		default:
			super.setSortColumn(sortColumn);
		}
	}
}
