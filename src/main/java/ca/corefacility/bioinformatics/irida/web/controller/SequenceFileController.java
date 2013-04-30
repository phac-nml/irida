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
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Controller for {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/sequenceFiles")
public class SequenceFileController extends GenericController<Identifier, SequenceFile, SequenceFileResource> {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileController.class);

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

    /**
     * Accept multi-part requests for {@link SequenceFile}s.
     *
     * @param file the file to submit to the service.
     * @return the location of the created entity.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestParam("file") MultipartFile file) throws IOException {
        Path temp = Files.createTempFile(null, null);
        Files.write(temp, file.getBytes());
        SequenceFile sf = new SequenceFile(temp.toFile());
        sf = crudService.create(sf);
        String identifier = sf.getIdentifier().getIdentifier();
        String location = linkTo(SequenceFileController.class).slash(identifier).withSelfRel().getHref();
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        ResponseEntity<String> response = new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
        return response;
    }
}
