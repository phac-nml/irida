package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;

/**
 * Class to hold a list of resources when being read from a remote Irida API
 * 
 * @param <Type>
 *            The type of object being held in this list
 */
public class ResourceList<Type extends IridaRepresentationModel> extends IridaRepresentationModel{

	private List<Type> resources;
	private Long totalResources;

	public ResourceList() {
	}

	/**
	 * Get the list of resources
	 * @return the {@link IridaRepresentationModel} collection.
	 */
	public List<Type> getResources() {
		return resources;
	}

	/**
	 * Set the list of resources
	 * @param resources the {@link IridaRepresentationModel} collection.
	 */
	public void setResources(List<Type> resources) {
		this.resources = resources;
	}

	/**
	 * Get the total number of resources in this list
	 * @return the total number of {@link IridaRepresentationModel} in the collection.
	 */
	public Long getTotalResources() {
		return totalResources;
	}

	/**
	 * Set the total number of resources
	 * @param totalResources the total number of {@link IridaRepresentationModel} in the collection.
	 */
	public void setTotalResources(Long totalResources) {
		this.totalResources = totalResources;
	}

}
