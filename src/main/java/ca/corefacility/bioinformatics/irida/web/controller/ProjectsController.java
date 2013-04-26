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
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.support.SortProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Identifier, Project, ProjectResource> {

    private static final String PROJECT_USERS_REL = "project/users";
    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Autowired
    public ProjectsController(ProjectService projectService) {
        super(projectService, Identifier.class, Project.class, ProjectResource.class);
    }
    
    @Override
    protected Order getDefaultSortOrder() {
        return Order.DESCENDING;
    }
            
    @Override
    public Project mapResourceToType(ProjectResource pr) {
        Project p = new Project();
        p.setName(pr.getName());
        return p;
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
