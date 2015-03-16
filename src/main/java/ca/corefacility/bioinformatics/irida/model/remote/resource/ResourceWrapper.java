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
	 * @return
	 */
	public Type getResource() {
		return resource;
	}

	/**
	 * Set the resource
	 * @param resource
	 */
	public void setResource(Type resource) {
		this.resource = resource;
	}    
}
