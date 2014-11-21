package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.List;
import java.util.Map;

/**
 * Class to hold a list of resources when being read from a remote Irida API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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
	 * @return
	 */
	public List<Type> getResources() {
		return resources;
	}

	/**
	 * Set the list of resources
	 * @param resources
	 */
	public void setResources(List<Type> resources) {
		this.resources = resources;
	}

	/**
	 * Get the links referenced by this list
	 * @return
	 */
	public List<Map<String, String>> getLinks() {
		return links;
	}

	/**
	 * Set the links referenced by this list
	 * @param links
	 */
	public void setLinks(List<Map<String, String>> links) {
		this.links = links;
	}

	/**
	 * Get the total number of resources in this list
	 * @return
	 */
	public Long getTotalResources() {
		return totalResources;
	}

	/**
	 * Set the total number of resources
	 * @param totalResources
	 */
	public void setTotalResources(Long totalResources) {
		this.totalResources = totalResources;
	}

}
