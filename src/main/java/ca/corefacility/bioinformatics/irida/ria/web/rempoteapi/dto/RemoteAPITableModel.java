package ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a {@link RemoteAPI} in the Remote API table.
 */
public class RemoteAPITableModel extends TableModel {
	private final String serviceURI;
	private final String clientId;
	private final String clientSecret;

	public RemoteAPITableModel(RemoteAPI api) {
		super(api.getId(), api.getName(), api.getCreatedDate(), api.getModifiedDate());
		this.serviceURI = api.getServiceURI();
		this.clientId = api.getClientId();
		this.clientSecret = api.getClientSecret();
	}

	public String getServiceURI() {
		return serviceURI;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}
}
