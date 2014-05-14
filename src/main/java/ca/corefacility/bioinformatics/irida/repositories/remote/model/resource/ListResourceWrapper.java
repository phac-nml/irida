package ca.corefacility.bioinformatics.irida.repositories.remote.model.resource;


/**
 *
 * @author tom
 * @param <Type>
 */
public class ListResourceWrapper <Type extends RemoteResource> {
	private ResourceList<Type> resource;

	public ResourceList<Type> getResource() {
		return resource;
	}

	public void setResource(ResourceList<Type> resource) {
		this.resource = resource;
	}    
}
