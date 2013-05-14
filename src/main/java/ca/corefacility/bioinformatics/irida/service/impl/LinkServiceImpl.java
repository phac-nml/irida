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

import ca.corefacility.bioinformatics.irida.model.Link;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.LinksRepository;
import ca.corefacility.bioinformatics.irida.service.LinkService;
import java.util.Collection;

public class LinkServiceImpl implements LinkService {

    private LinksRepository linksRepository;

    public LinkServiceImpl() {
    }

    public LinkServiceImpl(LinksRepository linksRepository) {
        this.linksRepository = linksRepository;
    }
    
    @Override
    public <S extends IridaThing,O extends IridaThing> Link createLink(S subject, O object){
        return linksRepository.create(subject, object);
    }

    @Override
    public Collection<Link> listLinks(Identifier id, Class subjectType, Class objectType) {
        return linksRepository.getLinks(id, subjectType, objectType);
        
    }

}
