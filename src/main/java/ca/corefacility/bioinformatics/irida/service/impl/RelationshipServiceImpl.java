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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a service for managing {@link Relationship}.
 *
 * @author Tom Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class RelationshipServiceImpl extends CRUDServiceImpl<Identifier, Relationship> implements RelationshipService {

    private RelationshipRepository linksRepository;

    public RelationshipServiceImpl(RelationshipRepository linksRepository, Validator validator) {
        super(linksRepository, validator, Relationship.class);
        this.linksRepository = linksRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends IridaThing, O extends IridaThing> Relationship create(S subject, O object) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject of relationship cannot be null.");
        }
        if (object == null) {
            throw new IllegalArgumentException("Object of relationship cannot be null.");
        }
        return linksRepository.create(subject, object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Relationship> getRelationshipsForEntity(Identifier id, Class subjectType, Class objectType) {
        return linksRepository.getLinks(id, subjectType, objectType);

    }

    /**
     * Updating a relationship does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public Relationship update(Identifier id, Map<String, Object> updatedProperties)
            throws EntityExistsException, EntityNotFoundException, ConstraintViolationException,
            InvalidPropertyException {
        throw new UnsupportedOperationException("Updating links will not be supported.");
    }

    /**
     * Listing all relationships does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public List<Relationship> list() {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    /**
     * Listing relationships does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public List<Relationship> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    /**
     * Listing relationships does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public List<Relationship> list(int page, int size, Order order) {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    /**
     * Checking for the existence of an identifier does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public Boolean exists(Identifier id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Checking for the existence of an identifier does not make sense, semantically. This method always throws {@link
     * UnsupportedOperationException}.
     */
    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Counting links will not be supported.");
    }

}
