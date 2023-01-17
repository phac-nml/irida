package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;

/**
 * Wraps a collection of {@link ResourceCollection} objects to be sent to the client.
 *
 * @param <Type> The type of object in this collection
 */
public class ResourceCollection<Type> extends IridaRepresentationModel implements Iterable<Type> {

	/**
	 * A collection of resources to be serialized.
	 */
	private List<Type> resources;

	/**
	 * Default constructor, empty set of resources.
	 */
	public ResourceCollection() {
		this.resources = new ArrayList<>();
	}

	/**
	 * Constructor with a pre-defined size for the set of resources.
	 *
	 * @param size the size of the collection.
	 */
	public ResourceCollection(long size) {
		this.resources = new ArrayList<>((int) size);
	}

	/**
	 * Add a new {@link ResourceCollection} to this collection.
	 *
	 * @param u The {@link ResourceCollection} to add.
	 */
	public void add(Type u) {
		resources.add(u);
	}

	/**
	 * The collection of {@link ResourceCollection} objects in this collection.
	 *
	 * @return a collection of {@link ResourceCollection} objects.
	 */
	public List<Type> getResources() {
		return this.resources;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Type> iterator() {
		return resources.iterator();
	}

	/**
	 * Get the number of elements in the collection.
	 *
	 * @return the number of elements in the collection.
	 */
	public int size() {
		return resources.size();
	}
}
