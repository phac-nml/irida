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

import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import static ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder.pageLinksFor;
import com.google.common.base.Strings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller that can serve any model from the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class GenericController<IDType extends Identifier, Type extends Identifiable<IDType> & Comparable<Type>, ResourceType extends Resource> {

    private static final Logger logger = LoggerFactory.getLogger(GenericController.class);
    protected CRUDService<IDType, Type> crudService;
    private Class<ResourceType> resourceType;
    private String prefix;

    protected GenericController(CRUDService<IDType, Type> crudService, Class<Type> type, Class<ResourceType> resourceType) {
        this.crudService = crudService;
        this.resourceType = resourceType;
        this.prefix = type.getName().toLowerCase() + "s";
    }

    /**
     * Retrieve and construct a response with a collection of user resources.
     *
     * @param page the current page of the list of resources that the client
     * wants.
     * @param size the size of the page that the client wants to see.
     * @param sortProperty the property that the resources should be sorted by.
     * @param sortOrder the order of the sort.
     * @return a model and view containing the collection of user resources.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listResources(
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_PAGE, defaultValue = "1") int page,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SIZE, defaultValue = "20") int size,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_PROPERTY, required = false) String sortProperty,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_ORDER, defaultValue = "ASCENDING") Order sortOrder) throws InstantiationException, IllegalAccessException {
        ModelAndView mav = new ModelAndView(prefix + "/index");
        List<Type> entities;
        
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
}
