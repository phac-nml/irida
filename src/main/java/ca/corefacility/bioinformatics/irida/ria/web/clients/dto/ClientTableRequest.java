package ca.corefacility.bioinformatics.irida.ria.web.clients.dto;

import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;

/**
 * {@link org.springframework.security.oauth2.provider.ClientDetails} does not have a name
 * column, we need to overwrite the table request `setSortField` to return `clientId` instead.
 */
public class ClientTableRequest extends TableRequest {
	@Override
	public void setSortField(String sortField) {
		super.setSortField(sortField.equals("name") ? "clientId" : sortField);
	}
}
