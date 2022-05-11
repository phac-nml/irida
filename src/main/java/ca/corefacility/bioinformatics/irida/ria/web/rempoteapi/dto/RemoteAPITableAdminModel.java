package ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Represents a {@link RemoteAPI} in the Remote API table.
 * This is only for admin users to have access to client details
 */
public class RemoteAPITableAdminModel extends RemoteAPITableModel {
	private final String serviceURI;
	private final String clientId;
	private final String clientSecret;

	public RemoteAPITableAdminModel(RemoteAPI api) {
		super(api);
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
