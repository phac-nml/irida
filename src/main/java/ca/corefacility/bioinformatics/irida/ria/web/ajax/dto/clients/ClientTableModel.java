package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to represent an {@link IridaClientDetails} in an ant.design table on the Clients page.
 */
public class ClientTableModel extends TableModel {
	private final int tokens;
	private final IridaClientDetails details;

	public ClientTableModel(IridaClientDetails client, int tokens) {
		super(client.getId(), client.getClientId(), client.getCreatedDate(), client.getModifiedDate());
		this.tokens = tokens;
		this.details = client;
	}

	public IridaClientDetails getDetails() {
		return details;
	}

	public String getClientId() {
		return details.getClientId();
	}

	public int getTokens() {
		return tokens;
	}
}