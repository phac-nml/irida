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
package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import java.util.List;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface RelationshipRepository extends CRUDRepository<Identifier, Relationship>{ 
    /**
     * Create a new link in the system for the two given objects.
     * A default link type must be defined for the types.
     * @param <SubjectType> The class of the subject object
     * @param <ObjectType> The class of the object object
     * @param subject The subject parameter of the link
     * @param object The object parameter of the link
     * @return A new <type>Link</type> object of the created relationship
     */
    public <SubjectType extends IridaThing,ObjectType extends IridaThing> Relationship create(SubjectType subject, ObjectType object);
   
    /**
     * Delete default links between the two given objects.
     * @param <SubjectType> The class of the subject object
     * @param <ObjectType> The class of the object object
     * @param subject The subject parameter of the link to delete
     * @param object The object parameter of the link to delete
     */
    public <SubjectType extends IridaThing, ObjectType extends IridaThing> void delete(SubjectType subject, ObjectType object);
    /**
     * List the objects with the given subject and predicate
     * @param subjectId The identifier of the subject for the requested triples
     * @param predicate The predicate object of the requested triples
     * @return A list of identifiers for the objects
     */
    public List<Identifier> listObjects(Identifier subjectId, RdfPredicate predicate);
    
    /**
     * List the objects with the given object and predicate
     * @param objectId The identifier of the object for the requested triples
     * @param predicate The predicate object of the requested triples
     * @return A list of identifiers for the subjects
     */
    public List<Identifier> listSubjects(Identifier objectId, RdfPredicate predicate);
    
    /**
     * Get a list of identifiers for links with the given subject identifier, subject class, and object class
     * @param id The subject identifier
     * @param subjectType The class of the subject type
     * @param objectType The class of the object type
     * @return A list of identifiers that match the given types
     */
    public List<Identifier> listLinks(Identifier id, Class subjectType,Class objectType);
    
    /**
     * list the Link objects that have the given identifier type, subject class, and object class
     * @param subjectId The identifier of the subject 
     * @param subjectType The class of the subject
     * @param objectType The class of the object
     * @return A list of constructed links for the given types
     */
    public List<Relationship> getLinks(Identifier subjectId, Class subjectType, Class objectType);
    
    
    /**
     * Get links for the given Subject, Predicate, and Object.
     * Any of subjectId, predicate, and objectId can be null, but not all 3
     * @param subjectId The identifier of the subject for the requested triples
     * @param predicate An {@link RdfPredicate} of the requested triples
     * @param objectId The identifier of the Object of the requested triples
     * @return
     */
    public List<Relationship> getLinks(Identifier subjectId, RdfPredicate predicate, Identifier objectId);
}
