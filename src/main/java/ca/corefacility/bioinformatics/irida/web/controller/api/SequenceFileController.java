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
package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Controller for {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@ExposesResourceFor(SequenceFile.class)
@RequestMapping(value = "/sequenceFiles")
public class SequenceFileController extends GenericController<Identifier, SequenceFile, SequenceFileResource> {

    @Autowired
    public SequenceFileController(CRUDService<Identifier, SequenceFile> sequenceFileService) {
        super(sequenceFileService, SequenceFile.class, Identifier.class,
                SequenceFileResource.class);
    }

    /**
     * Map {@link SequenceFileResource} to {@link SequenceFile}.
     *
     * @param representation the instance of {@link SequenceFileResource} to map.
     * @return an instance of {@link SequenceFile}.
     */
    @Override
    public SequenceFile mapResourceToType(SequenceFileResource representation) {
        return new SequenceFile(representation.getFile());
    }

    /**
     * Creating a new instance of a {@link SequenceFile} in the database. This method overrides the generic create
     * method because we need to accept multi-part requests for {@link SequenceFile}s.
     *
     * @param file the file to submit to the service.
     * @return the location of the created entity.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestParam("file") MultipartFile file) throws IOException {
        // create a temporary location to store the file that we've received prior to sending to the database
        Path temp = Files.createTempDirectory(null);
        Path target = temp.resolve(file.getOriginalFilename());

        // write the bytes from the multi-part file into the temporary location
        target = Files.write(target, file.getBytes());
        File f = target.toFile();

        // construct a new sequence file using that temporary file location
        SequenceFile sf = new SequenceFile(f);

        // ask the service to create the file in the database
        sf = crudService.create(sf);

        // clean up after yourself, remove the temporary file and directory that you created before
        f.delete();
        temp.toFile().delete();

        // get a handle on the identifier of the new sequence file so that you can respond with its location
        String identifier = sf.getIdentifier().getIdentifier();

        // construct the location of the sequence file
        String location = linkTo(SequenceFileController.class).slash(identifier).withSelfRel().getHref();

        // add the location to the headers
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);

        // respond to the client
        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }
}
