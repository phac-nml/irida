package ca.corefacility.bioinformatics.irida.ria.utilities;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * Object stored by the {@link RemoteObjectCache}. Includes a reference to
 * {@link RemoteObjectCache}
 * 
 *
 * @param <Type>
 *            the type stored by this object
 */
public class CacheObject<Type extends RemoteResource> {
	private Type resource;
	private RemoteAPI api;

	/**
	 * Create a new {@link CacheObject}
	 * 
	 * @param object
	 *            The object to store
	 * @param api
	 *            The API this object came from
	 */
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
