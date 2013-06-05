/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
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
package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.MultipleRelationshipsException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

import java.util.Collection;

/**
 * Service for managing relationships between entities.
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface RelationshipService extends CRUDService<Identifier, Relationship> {

    /**
     * Create a relationship with the default type between two entities in the database. If more than one relationship
     * type exists, then this method will throw an exception.
     *
     * @param subject   the subject of the relationship.
     * @param object    the object of the relationship.
     * @param <Subject> The subject type of the relationship.
     * @param <Object>  the object type of the relationship.
     * @return an instance of {@link Relationship} with an {@link Identifier}.
     */
    public <Subject extends IridaThing, Object extends IridaThing> Relationship create(Subject subject, Object object);

    /**
     * Get the list of links for a specific identifier with the specified object and subject types.
     *
     * @param id          the {@link Identifier} to get the relationships for.
     * @param subjectType the type of class for the identifier.
     * @param objectType  the type of class for the target of the relationship.
     * @return the collection of relationships for this class.
     */
    public Collection<Relationship> getRelationshipsForEntity(Identifier id, Class subjectType, Class objectType);

    /**
     * Check to see if any kind of relationship between two {@link Identifier} entities exists in the database.
     *
     * @param subject the subject {@link Identifier}.
     * @param object  the object {@link Identifier}.
     * @return the collection of relationships between two entities
     */
    public Relationship getRelationship(Identifier subject, Identifier object) throws MultipleRelationshipsException;
}
