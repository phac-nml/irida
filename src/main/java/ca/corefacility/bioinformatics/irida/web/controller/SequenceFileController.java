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
package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/sequenceFile")
public class SequenceFileController extends GenericController<Identifier, SequenceFile, SequenceFileResource> {

    //private CRUDService<Identifier, SequenceFile> sequenceFileService;
    @Autowired
    public SequenceFileController(CRUDService<Identifier, SequenceFile> sequenceFileService) {
        super(sequenceFileService, Identifier.class, SequenceFile.class, SequenceFileResource.class);
    }

    @Override
    public Collection<Link> constructCustomResourceLinks(SequenceFile resource) {
        return Collections.emptySet();
    }

    @Override
    public SequenceFile mapResourceToType(SequenceFileResource representation) {
        return new SequenceFile(representation.getFile());
    }
}
