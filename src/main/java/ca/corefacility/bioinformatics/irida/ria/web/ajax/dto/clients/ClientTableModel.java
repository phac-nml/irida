package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Used to represent an {@link IridaClientDetails} in an ant.design table on the Clients page.
 */
public class ClientTableModel {
	private final int tokens;
	private final IridaClientDetails details;

	public ClientTableModel(IridaClientDetails client, int tokens) {
		this.tokens = tokens;
		this.details = client;
	}

	public IridaClientDetails getDetails() {
		return details;
	}

	public int getTokens() {
		return tokens;
	}
}