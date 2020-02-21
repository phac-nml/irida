package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;

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
		default:
			super.setSortColumn(sortColumn);
		}
	}
}
