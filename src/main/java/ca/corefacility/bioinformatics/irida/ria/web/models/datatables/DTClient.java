package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import org.springframework.util.StringUtils;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * DataTables response object for a @{link IridaClientDetails}
 */
public class DTClient implements DataTablesResponseModel {
	private Long id;
	private String clientId;
	private String grants;
	private Date createdDate;
	private int tokens;

	public DTClient(IridaClientDetails client, int tokens) {
		this.id = client.getId();
		this.clientId = client.getClientId();
		this.grants = StringUtils.collectionToDelimitedString(client.getAuthorizedGrantTypes(), ", ");
		this.createdDate = client.getCreatedDate();
		this.tokens = tokens;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getClientId() {
		return clientId;
	}

	public String getGrants() {
		return grants;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public int getTokens() {
		return tokens;
	}
}
