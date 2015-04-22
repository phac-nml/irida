package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.List;
import java.util.Map;

/**
 * Class to hold a list of resources when being read from a remote Irida API
 * 
 * @param <Type>
 *            The type of object being held in this list
 */
public class ResourceList<Type extends RemoteResource> {

	protected List<Map<String, String>> links;
	private List<Type> resources;
	private Long totalResources;

	public ResourceList() {
	}

	/**
	 * Get the list of resources
	 * @return the {@link RemoteResource} collection.
	 */
	public List<Type> getResources() {
		return resources;
	}

	/**
	 * Set the list of resources
	 * @param resources the {@link RemoteResource} collection.
	 */
	public void setResources(List<Type> resources) {
		this.resources = resources;
	}

	/**
	 * Get the links referenced by this list
	 * @return the collection of links for each {@link RemoteResource}.
	 */
	public List<Map<String, String>> getLinks() {
		return links;
	}

	/**
	 * Set the links referenced by this list
	 * @param links the collection of links for each {@link RemoteResource}.
	 */
	public void setLinks(List<Map<String, String>> links) {
		this.links = links;
	}

	/**
	 * Get the total number of resources in this list
	 * @return the total number of {@link RemoteResource} in the collection.
	 */
	public Long getTotalResources() {
		return totalResources;
	}

	/**
	 * Set the total number of resources
	 * @param totalResources the total number of {@link RemoteResource} in the collection.
	 */
	public void setTotalResources(Long totalResources) {
		this.totalResources = totalResources;
	}

}
