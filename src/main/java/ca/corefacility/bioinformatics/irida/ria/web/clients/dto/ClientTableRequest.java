package ca.corefacility.bioinformatics.irida.ria.web.clients.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;

public class ClientTableRequest extends TableRequest {

	@Override
	public void setSortColumn(String sortColumn) {
		String column = sortColumn.equals("name") ? "clientId" : sortColumn;
		super.setSortColumn(column);
	}
}
