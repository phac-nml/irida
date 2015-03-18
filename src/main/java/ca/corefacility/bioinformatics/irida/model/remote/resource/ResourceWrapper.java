package ca.corefacility.bioinformatics.irida.model.remote.resource;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;

/**
 * Object wrapping a resource read from an Irida API
 * @param <Type> The type this resource will hold (extends {@link IridaResourceSupport})
 */
public class ResourceWrapper <Type extends IridaResourceSupport> {
	private Type resource;

	/**
	 * Get the resource
	 * @return the {@link RemoteResource} wrapped by this object.
	 */
	public Type getResource() {
		return resource;
	}

	/**
	 * Set the resource
	 * @param resource the {@link RemoteResource} wrapped by this object.
	 */
	public void setResource(Type resource) {
		this.resource = resource;
	}    
}
