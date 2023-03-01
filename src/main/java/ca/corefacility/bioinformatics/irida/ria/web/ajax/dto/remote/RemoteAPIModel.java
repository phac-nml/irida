package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * UI Model to represent the details of a {@link RemoteAPI}
 */
public class RemoteAPIModel {
	private final Long id;
	private final String name;
	private final String url;
	private final String clientId;
	private final String clientSecret;
	private final Date created;

	public RemoteAPIModel(RemoteAPI remoteAPI) {
		this.id = remoteAPI.getId();
		this.name = remoteAPI.getName();
		this.url = remoteAPI.getServiceURI();
		this.clientId = remoteAPI.getClientId();
		this.clientSecret = remoteAPI.getClientSecret();
		this.created = remoteAPI.getCreatedDate();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public Date getCreated() {
		return created;
	}

}
