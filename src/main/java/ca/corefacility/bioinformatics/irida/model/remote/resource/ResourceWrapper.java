package ca.corefacility.bioinformatics.irida.model.remote.resource;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;

/**
 * Object wrapping a resource read from an Irida API
 * 
 * @param <Type>
 *            The type this resource will hold (extends
 *            {@link IridaRepresentationModel})
 */
public class ResourceWrapper<Type extends IridaRepresentationModel> {
	private Type resource;

	/**
	 * Get the resource
	 * 
	 * @return the {@link IridaRepresentationModel} wrapped by this object.
	 */
	public Type getResource() {
		return resource;
	}

	/**
	 * Set the resource
	 * 
	 * @param resource
	 *            the {@link IridaRepresentationModel} wrapped by this object.
	 */
	public void setResource(Type resource) {
		this.resource = resource;
	}
}
