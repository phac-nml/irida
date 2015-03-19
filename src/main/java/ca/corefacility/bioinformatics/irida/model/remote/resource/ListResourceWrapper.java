package ca.corefacility.bioinformatics.irida.model.remote.resource;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;

/**
 * Object wrapping a list of returned resources from a remote IRIDA API. This
 * type will be returned when listing objects from the API. Example: "/projects"
 * 
 * @param <Type>
 *            The type of object being stored in the list extends
 *            {@link IridaResourceSupport}
 */
public class ListResourceWrapper<Type extends IridaResourceSupport> {
	private ResourceList<Type> resource;

	public ResourceList<Type> getResource() {
		return resource;
	}

	public void setResource(ResourceList<Type> resource) {
		this.resource = resource;
	}
}
