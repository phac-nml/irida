package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestResource;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

/**
 * Unit tests for the {@link GenericController}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@SuppressWarnings("unused")
public class GenericControllerTest {

	private static final String RELATED_IDENTIFIABLE_TEST_ENTITY_KEY = "related";
	private GenericController<IdentifiableTestEntity, IdentifiableTestResource> controller;
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
		controller = new GenericController<IdentifiableTestEntity, IdentifiableTestResource>(crudService,
				IdentifiableTestEntity.class, IdentifiableTestResource.class) {
		};
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
		assertTrue(mav.getHeaders().getFirst(HttpHeaders.LOCATION).endsWith(identifier.toString()));
	}

	@Test
	public void testDeleteEntity() throws InstantiationException, IllegalAccessException {
		ModelMap modelMap = controller.delete(2L);
		RootResource rootResource = (RootResource) modelMap.get(GenericController.RESOURCE_NAME);
		Link l = rootResource.getLink(GenericController.REL_COLLECTION);
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
		IdentifiableTestResource resource = (IdentifiableTestResource) model.get("resource");
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
		when(crudService.update(identifier, updatedFields)).thenReturn(entity);
		ModelMap response = controller.update(identifier, updatedFields);
		RootResource r = (RootResource) response.get(GenericController.RESOURCE_NAME);
		assertNotNull(r.getLink(Link.REL_SELF));
		assertNotNull(r.getLink(GenericController.REL_COLLECTION));
	}

	@Test
	public void testUpdateBadResource() throws InstantiationException, IllegalAccessException {
		when(crudService.update(identifier, updatedFields)).thenThrow(new EntityNotFoundException("not found"));
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
		when(crudService.update(identifier, updatedFields)).thenThrow(
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
