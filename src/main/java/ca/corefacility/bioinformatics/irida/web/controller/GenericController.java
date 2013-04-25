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
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import static ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder.pageLinksFor;
import ca.corefacility.bioinformatics.irida.web.controller.support.SortProperty;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller that can serve any model from the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class GenericController<IdentifierType extends Identifier, Type extends Identifiable<IdentifierType> & Comparable<Type>, ResourceType extends Resource> {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericController.class);
    protected CRUDService<IdentifierType, Type> crudService;
    private Class<ResourceType> resourceType;
    private Class<IdentifierType> identifierType;
    private Class<Type> type;
    private String prefix;
    private String resourceCollectionIndex;
    private String resourceIndividualIndex;
    
    protected GenericController(CRUDService<IdentifierType, Type> crudService,
            Class<IdentifierType> identifierType, Class<Type> type, Class<ResourceType> resourceType) {
        this.crudService = crudService;
        this.resourceType = resourceType;
        this.identifierType = identifierType;
        this.type = type;
    }
    
    @PostConstruct
    public void initializePages() {
        // initialize the names of the pages
        String typeName = type.getSimpleName().toLowerCase();
        this.prefix = typeName + "s/";
        this.resourceCollectionIndex = prefix + typeName + "s";
        this.resourceIndividualIndex = prefix + typeName;
    }

    /**
     * Construct a collection of {@link Link}s for a specific resource. Each
     * resource may have custom links that refer to other controllers, but not
     * all will. This method is called by
     * {@link GenericController.getResource()}.
     *
     * @param resource the resource to generate the links for.
     * @return a collection of links.
     */
    public abstract Collection<Link> constructCustomResourceLinks(Type resource);

    /**
     * Map a representation of a resource to a concrete version of the resource
     * so that we can store it in the database.
     *
     * @param resourceType the representation to map.
     * @return the concrete version of the representation.
     */
    public abstract Type mapResourceToType(ResourceType representation);

    /**
     * Get the default sort property, {@link SortProperty.NONE} by default.
     *
     * @return the default sort property for this class.
     */
    protected SortProperty getDefaultSortProperty() {
        return SortProperty.NONE;
    }
    
    protected Order getDefaultSortOrder() {
        return Order.ASCENDING;
    }

    /**
     * Retrieve and construct a response with a collection of resources.
     *
     * @param page the current page of the list of resources that the client
     * wants.
     * @param size the size of the page that the client wants to see.
     * @param sortProperty the property that the resources should be sorted by.
     * @param sortOrder the order of the sort.
     * @return a model and view containing the collection of resources.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listResources(
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_PAGE, defaultValue = "1") int page,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SIZE, defaultValue = "20") int size,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_PROPERTY, required = false) String sortProperty,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_ORDER, required = false) Order sortOrder) throws InstantiationException, IllegalAccessException {
        ModelAndView mav = new ModelAndView(resourceCollectionIndex);
        List<Type> entities;

        // if the client did not specify a sort property, try to get a default sort property from the subclass.
        if (Strings.isNullOrEmpty(sortProperty) && !SortProperty.NONE.equals(getDefaultSortProperty())) {
            sortProperty = getDefaultSortProperty().getSortProperty();
        }

        // if the client did not specify a sort order, try to get the default sort order from the subclass.
        if (sortOrder == null && !Order.NONE.equals(getDefaultSortOrder())) {
            sortOrder = getDefaultSortOrder();
        }
        
        if (Strings.isNullOrEmpty(sortProperty)) {
            entities = crudService.list(page, size, sortOrder);
        } else {
            entities = crudService.list(page, size, sortProperty, sortOrder);
        }
        ControllerLinkBuilder linkBuilder = linkTo(getClass());
        int totalEntities = crudService.count();
        ResourceCollection<ResourceType> resources = new ResourceCollection<>();
        
        for (Type entity : entities) {
            ResourceType resource = resourceType.newInstance();
            resource.setResource(entity);
            resource.add(linkBuilder.slash(entity.getIdentifier().getIdentifier()).withSelfRel());
            resources.add(resource);
        }
        
        resources.add(pageLinksFor(getClass(), page, size, totalEntities, sortProperty, sortOrder));
        resources.setTotalResources(totalEntities);
        
        mav.addObject("resources", resources);
        return mav;
    }
    
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    public ModelAndView getResource(@PathVariable String resourceId) throws InstantiationException, IllegalAccessException {
        ModelAndView mav = new ModelAndView(resourceIndividualIndex);
        logger.debug("Getting resource with id [" + resourceId + "]");
        IdentifierType id = identifierType.newInstance();
        id.setIdentifier(resourceId);
        Type t = crudService.read(id);
        ResourceType resource = resourceType.newInstance();
        resource.setResource(t);
        resource.add(constructCustomResourceLinks(t));
        resource.add(linkTo(getClass()).withSelfRel());
        mav.addObject("resource", resource);
        return mav;
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody ResourceType representation) {
        Type resource = mapResourceToType(representation);
        resource = crudService.create(resource);
        String id = resource.getIdentifier().getIdentifier();
        logger.debug("Created resource with ID [" + resource.getIdentifier().getIdentifier() + "]");
        String location = linkTo(getClass()).slash(id).withSelfRel().getHref();
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
        return new ResponseEntity<>("No such resource found.", HttpStatus.NOT_FOUND);
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
