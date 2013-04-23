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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import com.google.common.net.HttpHeaders;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Identifier, Project, ProjectResource> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);
    private final ProjectService projectService;
    private static final String PROJECT_USERS_REL = "project/users";

    @Autowired
    public ProjectsController(ProjectService projectService) {
        super(projectService, Project.class, ProjectResource.class, Identifier.class);
        this.projectService = projectService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody ProjectResource pr) {
        Project p = new Project();
        p.setName(pr.getName());
        p = projectService.create(p);
        logger.debug("Created project with ID [" + p.getIdentifier() + "]");
        String location = linkTo(ProjectsController.class).slash(p.getIdentifier().getUUID().toString()).withSelfRel().getHref();
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        ResponseEntity<String> response = new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
        return response;
    }

    @Override
    public Collection<Link> constructCustomResourceLinks(Project p) {
        Collection<Link> links = new HashSet<>();
        links.add(linkTo(ProjectsController.class).
                slash(p.getIdentifier().getIdentifier()).slash("users").
                withRel(PROJECT_USERS_REL));
        return links;
    }
}
