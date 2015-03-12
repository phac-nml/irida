package ca.corefacility.bioinformatics.irida.model.remote.resource;

/**
 * Object wrapping a resource read from an Irida API
 * @param <Type> The type this resource will hold (extends {@link RemoteResource})
 */
public class ResourceWrapper <Type extends RemoteResource> {
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
