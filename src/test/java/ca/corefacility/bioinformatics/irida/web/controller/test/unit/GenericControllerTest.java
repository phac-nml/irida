package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link RESTGenericController}.
 * 
 */
@SuppressWarnings("unused")
public class GenericControllerTest {

	private static final String RELATED_IDENTIFIABLE_TEST_ENTITY_KEY = "related";
	private RESTGenericController<IdentifiableTestEntity> controller;
	private CRUDService<Long, IdentifiableTestEntity> crudService;
	private IdentifiableTestEntity entity;
	private Map<String, Object> updatedFields;
	private Long identifier;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		crudService = mock(CRUDService.class);
		entity = new IdentifiableTestEntity();
		identifier = 1L;
		entity.setId(identifier);
		controller = new RESTGenericController<IdentifiableTestEntity>(crudService,
				IdentifiableTestEntity.class) {
		};
		updatedFields = new HashMap<>();
	}

	@Test
	public void testCreateBadEntity() {
		when(crudService.create(entity)).thenThrow(
				new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));

		try {
			controller.create(entity,new MockHttpServletResponse());
			fail();
		} catch (ConstraintViolationException ex) {
		} catch (Exception ex) {
			fail();
		}
	}

	@Test
	public void testCreateGoodEntity() {
		when(crudService.create(entity)).thenReturn(entity);
		when(crudService.read(identifier)).thenReturn(entity);
		ModelMap model = controller.create(entity,new MockHttpServletResponse());
		assertTrue("Model should contain resource",model.containsKey("resource"));
		IdentifiableTestEntity testResource = (IdentifiableTestEntity) model.get("resource");
		assertNotNull("Resource should not be null",testResource);
		assertTrue("Resource from model should be equivalent to resource added to model",testResource.equals(entity));
		assertTrue("Model should contain a self-reference",testResource.getLink(Link.REL_SELF).getHref().endsWith(identifier.toString()));
	}

	@Test
	public void testDeleteEntity() throws InstantiationException, IllegalAccessException {
		ModelMap modelMap = controller.delete(2L);
		ResourceSupport rootResource = (ResourceSupport) modelMap.get(RESTGenericController.RESOURCE_NAME);
		Link l = rootResource.getLink(RESTGenericController.REL_COLLECTION);
		assertNotNull(l);
		assertEquals("http://localhost/api/generic", l.getHref());
	}

	@Test
	public void testDeleteInvalidEntity() {
		doThrow(new EntityNotFoundException("not found")).when(crudService).delete(2L);

		try {
			controller.delete(2L);
		} catch (EntityNotFoundException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetResource() {
		when(crudService.read(identifier)).thenReturn(entity);
		ModelMap model = null;

		try {
			model = controller.getResource(identifier);
		} catch (GenericsException e) {
			fail();
		}

		assertTrue(model.containsKey("resource"));
		IdentifiableTestEntity resource = (IdentifiableTestEntity) model.get("resource");
		assertTrue(resource.getLink(Link.REL_SELF).getHref().endsWith(identifier.toString()));
	}

	@Test
	public void testGetBadResource() {
		when(crudService.read(identifier)).thenThrow(new EntityNotFoundException("not found"));
		try {
			controller.getResource(identifier);
		} catch (EntityNotFoundException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testUpdate() throws InstantiationException, IllegalAccessException {
		when(crudService.updateFields(identifier, updatedFields)).thenReturn(entity);
		ModelMap response = controller.update(identifier, updatedFields);
		ResourceSupport r = (ResourceSupport) response.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull(r.getLink(Link.REL_SELF));
		assertNotNull(r.getLink(RESTGenericController.REL_COLLECTION));
	}

	@Test
	public void testUpdateBadResource() throws InstantiationException, IllegalAccessException {
		when(crudService.updateFields(identifier, updatedFields)).thenThrow(new EntityNotFoundException("not found"));
		try {
			controller.update(identifier, updatedFields);
			fail();
		} catch (EntityNotFoundException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testUpdateFailsConstraints() throws InstantiationException, IllegalAccessException {
		when(crudService.updateFields(identifier, updatedFields)).thenThrow(
				new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));
		try {
			controller.update(identifier, updatedFields);
			fail();
		} catch (ConstraintViolationException e) {
		} catch (Exception e) {
			fail();
		}
	}
}
