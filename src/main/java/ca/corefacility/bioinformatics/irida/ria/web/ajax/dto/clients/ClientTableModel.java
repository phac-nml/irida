package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

import java.util.Set;

/**
 * Used to represent an {@link IridaClientDetails} in an ant.design table on the Clients page.
 */
public class ClientTableModel extends TableModel {
	private final Set<String> grants;
	private final int tokens;

	public ClientTableModel(IridaClientDetails client, int tokens) {
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