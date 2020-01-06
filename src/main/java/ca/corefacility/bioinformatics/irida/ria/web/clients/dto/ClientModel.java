package ca.corefacility.bioinformatics.irida.ria.web.clients.dto;

import org.springframework.util.StringUtils;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableModel;

public class ClientModel extends TableModel {
	private String grants;
	private int tokens;

	public ClientModel(IridaClientDetails client, int tokens) {
		super(client.getId(), client.getClientId(), client.getCreatedDate(), client.getModifiedDate());
		this.grants = StringUtils.collectionToDelimitedString(client.getAuthorizedGrantTypes(), ", ");
		this.tokens = tokens;
	}

	public String getGrants() {
		return grants;
	}

	public int getTokens() {
		return tokens;
	}
}
