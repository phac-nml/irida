package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;

/**
 * UI request for client details for the clients table.
 * Required to overwrite the default setSortColumn since
 * IridaClientDetails does not have a "name" attribute,
 * instead it required a "clientId".
 */
public class ClientTableRequest extends TableRequest {

	@Override
	public void setSortColumn(String sortColumn) {
		String column = sortColumn.equals("name") ? "clientId" : sortColumn;
		super.setSortColumn(column);
	}
}
