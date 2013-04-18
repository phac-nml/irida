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
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectCollectionResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder.pageLinksFor;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);
    private final ProjectService projectService;

    @Autowired
    public ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showProjectsPage(Model model,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_PAGE, defaultValue = "1") int page,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SIZE, defaultValue = "20") int size,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_COLUMN, defaultValue = "name") String sortColumn,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_ORDER, defaultValue = "ASCENDING") Order sortOrder) {
        List<Project> projects = projectService.list(page, size, sortColumn, sortOrder);
        int totalProjects = projectService.count();
        ControllerLinkBuilder linkBuilder = linkTo(ProjectsController.class);

        ProjectCollectionResource resources = new ProjectCollectionResource();

        for (Project p : projects) {
            ProjectResource resource = new ProjectResource(p);
            resource.add(linkBuilder.slash(p.getIdentifier().getUUID()).withSelfRel());
            resources.add(resource);
        }

        resources.add(pageLinksFor(ProjectsController.class, page, size, totalProjects, sortColumn, sortOrder));

        model.addAttribute("userResources", resources);
        model.addAttribute("users", true);
        model.addAttribute("pageTitle", "Users");
        return "projects/index";
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public String getProject(HttpServletResponse response, @PathVariable String projectId, Model model) {
        logger.debug("Getting project with id [" + projectId + "]");
        Project p = new Project();
        p.setIdentifier(new Identifier());
        model.addAttribute("project", p);
        return "projects/project";
    }
}
