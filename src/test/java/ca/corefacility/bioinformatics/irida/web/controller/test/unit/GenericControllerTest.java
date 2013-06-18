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

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.api.links.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.links.PageLink;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestResource;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GenericController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@SuppressWarnings("unused")
public class GenericControllerTest {

    private static final String RELATED_IDENTIFIABLE_TEST_ENTITY_KEY = "related";
    private GenericController<Identifier, IdentifiableTestEntity, IdentifiableTestResource> controller;
    private CRUDService<Identifier, IdentifiableTestEntity> crudService;
    private RelationshipService relationshipService;
    private EntityLinks entityLinks;
    private IdentifiableTestEntity entity;
    private Identifier id;
    private Map<String, Object> updatedFields;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        crudService = mock(CRUDService.class);
        relationshipService = mock(RelationshipService.class);
        entityLinks = mock(EntityLinks.class);
        id = new Identifier();
        entity = new IdentifiableTestEntity();
        entity.setIdentifier(id);
        controller = new GenericController<Identifier, IdentifiableTestEntity, IdentifiableTestResource>(crudService,
                IdentifiableTestEntity.class, Identifier.class, IdentifiableTestResource.class) {
            @Override
            public IdentifiableTestEntity mapResourceToType(IdentifiableTestResource representation) {
                return entity;
            }

            @Override
            protected Map<String, Class<?>> getUniquelyRelatedClasses() {
                return (Map<String, Class<?>>) ImmutableMap.of(RELATED_IDENTIFIABLE_TEST_ENTITY_KEY,
                        (Class<?>) IdentifiableTestEntity.class);
            }
        };
        controller.setRelationshipService(relationshipService);
        controller.setEntityLinks(entityLinks);
        updatedFields = new HashMap<>();
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
        ModelMap modelMap = controller.delete(UUID.randomUUID().toString());
        RootResource rootResource = (RootResource) modelMap.get(GenericController.RESOURCE_NAME);
        Link l = rootResource.getLink(GenericController.REL_COLLECTION);
        assertNotNull(l);
        assertEquals("http://localhost/generic", l.getHref());
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
        } catch (GenericsException e) {
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
        ModelMap response = controller.update(id.getIdentifier(), updatedFields);
        RootResource r = (RootResource) response.get(GenericController.RESOURCE_NAME);
        assertNotNull(r.getLink(PageLink.REL_SELF));
        assertNotNull(r.getLink(GenericController.REL_COLLECTION));
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
        @SuppressWarnings("unchecked")
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
    public void testListResourcesNoSortProperty() throws InstantiationException, IllegalAccessException {
        int totalResources = 400;
        List<IdentifiableTestEntity> entities = new ArrayList<>();
        entities.add(entity);
        when(crudService.list(2, 20, Order.DESCENDING)).thenReturn(entities);
        when(crudService.count()).thenReturn(totalResources);
        ModelMap mav = controller.listResources(2, 20, null, Order.DESCENDING);
        assertNotNull(mav.get(GenericController.RESOURCE_NAME));
        Object o = mav.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<IdentifiableTestResource> collection = (ResourceCollection<IdentifiableTestResource>) o;
        assertEquals(5, collection.getLinks().size());
        assertEquals(totalResources, collection.getTotalResources());

        for (IdentifiableTestResource r : collection) {
            assertEquals(1, r.getLinks().size());
            Link link = r.getLink(PageLink.REL_SELF);
            assertTrue(link.getHref().endsWith(entity.getIdentifier().getIdentifier()));
        }

        verify(crudService).list(2, 20, Order.DESCENDING);
        verify(crudService, times(0)).list(2, 20, null, Order.DESCENDING);
    }
}
