package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;

/**
 * Unit tests for the {@link RESTGenericController}.
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
		controller = new RESTGenericController<IdentifiableTestEntity>(crudService, IdentifiableTestEntity.class) {
		};
		updatedFields = new HashMap<>();
	}

	@Test
	public void testCreateBadEntity() {
		when(crudService.create(entity)).thenThrow(
				new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));

		try {
			controller.create(entity, new MockHttpServletResponse());
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
		ResponseResource<IdentifiableTestEntity> responseObject = controller.create(entity,
				new MockHttpServletResponse());
		IdentifiableTestEntity testResource = responseObject.getResource();
		assertNotNull("Resource should not be null", testResource);
		assertTrue("Resource from model should be equivalent to resource added to model", testResource.equals(entity));
		assertTrue("Model should contain a self-reference", testResource.getLink(Link.REL_SELF)
				.getHref()
				.endsWith(identifier.toString()));
	}

	@Test
	public void testDeleteEntity() throws InstantiationException, IllegalAccessException {
		ResponseResource<RootResource> responseObject = controller.delete(2L);
		RootResource rootResource = responseObject.getResource();
		Link l = rootResource.getLink(RESTGenericController.REL_COLLECTION);
		assertNotNull(l);
		assertEquals("http://localhost/api/generic", l.getHref());
	}

	@Test
	public void testDeleteInvalidEntity() {
		doThrow(new EntityNotFoundException("not found")).when(crudService)
				.delete(2L);

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
		ResponseResource<IdentifiableTestEntity> responseObject = null;

		try {
			responseObject = controller.getResource(identifier);
		} catch (GenericsException e) {
			fail();
		}

		IdentifiableTestEntity resource = responseObject.getResource();
		assertTrue(resource.getLink(Link.REL_SELF)
				.getHref()
				.endsWith(identifier.toString()));
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
		ResponseResource<RootResource> responseObject = controller.update(identifier, updatedFields);
		RootResource r = responseObject.getResource();
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
