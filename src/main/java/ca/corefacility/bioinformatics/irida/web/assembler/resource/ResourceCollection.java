package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a collection of {@link ResourceCollection} objects to be sent to the client.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "resource")
public class ResourceCollection<Type extends ResourceSupport> extends ResourceSupport implements Iterable<Type> {

    /**
     * A collection of resources to be serialized.
     */
    @XmlElement
    private List<Type> resources;
    /**
     * The total number of resources in the ENTIRE collection (not just those that are being serialized).
     */
    private long totalResources;

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
        this.resources = new ArrayList<>((int)size);
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
     * Get the total number of resources in the collection.
     *
     * @return total resources in the collection.
     */
    public long getTotalResources() {
        return this.totalResources;
    }

    /**
     * Set the total number of resources in the collection.
     *
     * @param totalResources total number of resources in the collection.
     */
    public void setTotalResources(long totalResources) {
        this.totalResources = totalResources;
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
