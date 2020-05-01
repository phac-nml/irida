package ca.corefacility.bioinformatics.irida.ria.web.clients.dto;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to represent an {@link IridaClientDetails} in an ant.design table on the Clients page.
 */
public class ClientModel extends TableModel {
	private Set<String> grants;
	private int tokens;

	public ClientModel(IridaClientDetails client, int tokens) {
		super(client.getId(), client.getClientId(), client.getCreatedDate(), client.getModifiedDate());
		this.grants = client.getAuthorizedGrantTypes();
		this.tokens = tokens;
	}

	public Set<String> getGrants() {
		return grants;
	}

	public int getTokens() {
		return tokens;
	}
}