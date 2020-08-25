package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to represent an {@link IridaClientDetails} in an ant.design table on the Clients page.
 */
public class ClientTableModel extends TableModel {
	private final IridaClientDetails details;
	private final int tokens;


	public ClientTableModel(IridaClientDetails client, int tokens) {
		super(client.getId(), client.getClientId(), client.getCreatedDate(), client.getModifiedDate());
		this.details = client;
		this.tokens = tokens;
	}

	public IridaClientDetails getDetails() {
		return details;
	}

	public int getTokens() {
		return tokens;
	}
}