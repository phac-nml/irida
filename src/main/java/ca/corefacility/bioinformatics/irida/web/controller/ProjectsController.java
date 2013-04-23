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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectCollectionResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import java.util.List;
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
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

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
    private static final String PROJECT_USERS_REL = "project/users";

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

        model.addAttribute("projectResources", resources);
        return "projects/projects";
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public ModelAndView getProject(@PathVariable String projectId) {
        ModelAndView mav = new ModelAndView("projects/project");
        logger.debug("Getting project with id [" + projectId + "]");
        Identifier id = new Identifier();
        id.setUUID(UUID.fromString(projectId));
        Project p = projectService.read(id);
        ProjectResource pr = new ProjectResource(p);
        pr.add(linkTo(ProjectsController.class).slash(id.getUUID().toString()).slash("users").withRel(PROJECT_USERS_REL));
        pr.add(linkTo(ProjectsController.class).withSelfRel());
        mav.addObject("project", pr);
        return mav;
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

    /**
     * Handle {@link EntityNotFoundException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>("No such project found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Handle {@link ConstraintViolationException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolations(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        return new ResponseEntity<>(validationMessages(constraintViolations), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link EntityExistsException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleExistsException(EntityExistsException e) {
        return new ResponseEntity<>("An entity already exists with that identifier.", HttpStatus.CONFLICT);
    }

    /**
     * Render a collection of constraint violations as a JSON object.
     *
     * @param failures the set of constraint violations.
     * @return the constraint violations as a JSON object.
     */
    private String validationMessages(Set<ConstraintViolation<?>> failures) {
        Map<String, List<String>> mp = new HashMap();
        for (ConstraintViolation<?> failure : failures) {
            logger.debug(failure.getPropertyPath().toString() + ": " + failure.getMessage());
            String property = failure.getPropertyPath().toString();
            if (mp.containsKey(property)) {
                mp.get(failure.getPropertyPath().toString()).add(failure.getMessage());
            } else {
                List<String> list = new ArrayList<>();
                list.add(failure.getMessage());
                mp.put(property, list);
            }
        }
        Gson g = new Gson();
        return g.toJson(mp);
    }
}
