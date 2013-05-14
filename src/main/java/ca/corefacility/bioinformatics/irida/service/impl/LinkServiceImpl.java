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
import ca.corefacility.bioinformatics.irida.repositories.sesame.LinksRepository;
import ca.corefacility.bioinformatics.irida.service.LinkService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;

public class LinkServiceImpl implements LinkService {

    private LinksRepository linksRepository;

    public LinkServiceImpl() {
    }

    public LinkServiceImpl(LinksRepository linksRepository) {
        this.linksRepository = linksRepository;
    }
    
    @Override
    public <S extends IridaThing,O extends IridaThing> Relationship createLink(S subject, O object){
        return linksRepository.create(subject, object);
    }

    @Override
    public Collection<Relationship> listLinks(Identifier id, Class subjectType, Class objectType) {
        return linksRepository.getLinks(id, subjectType, objectType);
        
    }

    @Override
    public Relationship create(Relationship object) throws EntityExistsException, ConstraintViolationException {
        return linksRepository.create(object);
    }

    @Override
    public Relationship read(Identifier id) throws EntityNotFoundException {
        return linksRepository.read(id);
    }

    @Override
    public Relationship update(Identifier id, Map<String, Object> updatedProperties) throws EntityExistsException, EntityNotFoundException, ConstraintViolationException, InvalidPropertyException {
        throw new UnsupportedOperationException("Updating links will not be supported.");
    }

    @Override
    public void delete(Identifier id) throws EntityNotFoundException {
        linksRepository.delete(id);
    }

    @Override
    public List<Relationship> list() {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    @Override
    public List<Relationship> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    @Override
    public List<Relationship> list(int page, int size, Order order) {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    @Override
    public Boolean exists(Identifier id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Counting links will not be supported.");
    }

}
