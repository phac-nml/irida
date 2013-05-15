/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
@XmlRootElement
public class ResourceCollection<Type extends ResourceSupport> extends ResourceSupport implements Iterable<Type> {

    /**
     * A collection of resources to be serialized.
     */
    @XmlElement
    private List<Type> resources;
    /**
     * The total number of resources in the ENTIRE collection (not just those that are being serialized).
     */
    private int totalResources;

    /**
     * Default constructor, empty set of resources.
     */
    public ResourceCollection() {
        this.resources = new ArrayList<>();
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
    public int getTotalResources() {
        return this.totalResources;
    }

    /**
     * Set the total number of resources in the collection.
     *
     * @param totalResources total number of resources in the collection.
     */
    public void setTotalResources(int totalResources) {
        this.totalResources = totalResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Type> iterator() {
        return resources.iterator();
    }
}
