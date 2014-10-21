package ca.corefacility.bioinformatics.irida.ria.utilities;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

public class CacheObject<Type extends RemoteResource> {
	private Type resource;
	private RemoteAPI api;

	public CacheObject(Type object, RemoteAPI api) {
		this.resource = object;
		this.api = api;
	}

	public Type getResource() {
		return resource;
	}

	public void setResource(Type resource) {
		this.resource = resource;
	}

	public RemoteAPI getAPI() {
		return api;
	}

	public void setAPI(RemoteAPI api) {
		this.api = api;
	}
}
