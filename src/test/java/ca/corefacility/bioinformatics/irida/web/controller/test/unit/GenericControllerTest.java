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
package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestResource;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GenericController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class GenericControllerTest {

    private GenericController<Identifier, IdentifiableTestEntity, IdentifiableTestResource> controller;
    private CRUDService<Identifier, IdentifiableTestEntity> crudService;
    private IdentifiableTestEntity entity;
    private Identifier id;
    private Map<String, Object> updatedFields;

    @Before
    public void setUp() {
        crudService = mock(CRUDService.class);
        id = new Identifier();
        entity = new IdentifiableTestEntity();
        entity.setIdentifier(id);
        controller = new GenericController<Identifier, IdentifiableTestEntity, IdentifiableTestResource>(crudService,
                Identifier.class, IdentifiableTestResource.class) {
            @Override
            public Collection<Link> constructCustomResourceLinks(IdentifiableTestEntity resource) {
                return Collections.emptySet();
            }

            @Override
            public IdentifiableTestEntity mapResourceToType(IdentifiableTestResource representation) {
                return entity;
            }
        };
        updatedFields = new HashMap<>();

        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }

    @Test
    public void testCreateBadEntity() {
        IdentifiableTestResource r = new IdentifiableTestResource(entity);

        when(crudService.create(entity)).thenThrow(
                new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));

        try {
            controller.create(r);
            fail();
        } catch (ConstraintViolationException ex) {
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testCreateGoodEntity() {
        IdentifiableTestResource resource = new IdentifiableTestResource(entity);
        when(crudService.create(entity)).thenReturn(entity);

        ResponseEntity<String> mav = controller.create(resource);
        assertEquals(HttpStatus.CREATED, mav.getStatusCode());
        assertTrue(mav.getHeaders().getFirst(HttpHeaders.LOCATION).
                endsWith(id.getIdentifier()));
    }

    @Test
    public void testDeleteEntity() throws InstantiationException, IllegalAccessException {
        ResponseEntity<String> response = null;
        response = controller.delete(UUID.randomUUID().toString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteInvalidEntity() {
        String uuid = UUID.randomUUID().toString();
        Identifier identifier = new Identifier();
        identifier.setIdentifier(uuid);
        doThrow(new EntityNotFoundException("not found")).when(crudService).delete(identifier);

        try {
            controller.delete(uuid);
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetResource() {
        when(crudService.read(id)).thenReturn(entity);
        ModelMap model = null;

        try {
            model = controller.getResource(id.getIdentifier());
        } catch (InstantiationException | IllegalAccessException e) {
            fail();
        }

        assertTrue(model.containsKey("resource"));
        IdentifiableTestResource resource = (IdentifiableTestResource) model.get("resource");
        assertTrue(resource.getLink(PageLink.REL_SELF).getHref().endsWith(id.getIdentifier()));
    }

    @Test
    public void testGetBadResource() {
        when(crudService.read(id)).thenThrow(new EntityNotFoundException("not found"));
        try {
            controller.getResource(id.getIdentifier());
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdate() throws InstantiationException, IllegalAccessException {
        when(crudService.update(id, updatedFields)).thenReturn(entity);
        ResponseEntity<String> response = controller.update(id.getIdentifier(), updatedFields);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateBadResource() throws InstantiationException, IllegalAccessException {
        when(crudService.update(id, updatedFields)).thenThrow(new EntityNotFoundException("not found"));
        try {
            controller.update(id.getIdentifier(), updatedFields);
            fail();
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateFailsConstraints() throws InstantiationException, IllegalAccessException {
        when(crudService.update(id, updatedFields)).thenThrow(
                new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));
        try {
            controller.update(id.getIdentifier(), updatedFields);
            fail();
        } catch (ConstraintViolationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testListResources() throws InstantiationException, IllegalAccessException {
        int totalResources = 400;
        List<IdentifiableTestEntity> entities = new ArrayList<>();
        entities.add(entity);
        when(crudService.list(2, 20, "nonNull", Order.DESCENDING)).thenReturn(entities);
        when(crudService.count()).thenReturn(totalResources);
        ModelMap mav = controller.listResources(2, 20, "nonNull", Order.DESCENDING);
        assertNotNull(mav.get(GenericController.RESOURCE_NAME));
        Object o = mav.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        ResourceCollection<IdentifiableTestResource> collection = (ResourceCollection<IdentifiableTestResource>) o;
        assertEquals(5, collection.getLinks().size());
        assertEquals(totalResources, collection.getTotalResources());

        for (IdentifiableTestResource r : collection) {
            assertEquals(1, r.getLinks().size());
            Link link = r.getLink(PageLink.REL_SELF);
            assertTrue(link.getHref().endsWith(entity.getIdentifier().getIdentifier()));
        }
    }

    @Test
    public void testHandleConstraintViolations() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        Set<ConstraintViolation<IdentifiableTestEntity>> violations = validator.validate(new IdentifiableTestEntity());
        for (ConstraintViolation<IdentifiableTestEntity> v : violations) {
            constraintViolations.add(v);
        }
        ResponseEntity<String> response = controller.handleConstraintViolations(
                new ConstraintViolationException(constraintViolations));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"nonNull\":[\"may not be null\"]}", response.getBody());
    }

    @Test
    public void testHandleNotFoundException() {
        ResponseEntity<String> response = controller.handleNotFoundException(new EntityNotFoundException("not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testHandleExistsException() {
        ResponseEntity<String> response = controller.handleExistsException(new EntityExistsException("exists"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testHandleInvalidPropertyException() {
        ResponseEntity<String> response = controller.handleInvalidPropertyException(
                new InvalidPropertyException("invalid property"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
