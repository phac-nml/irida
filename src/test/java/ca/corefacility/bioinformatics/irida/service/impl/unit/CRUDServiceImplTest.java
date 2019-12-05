package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntitySpecification;

import com.google.common.collect.Lists;

/**
 * Testing the behavior of {@link CRUDServiceImpl}
 * 
 */
public class CRUDServiceImplTest {

	private CRUDService<Long, IdentifiableTestEntity> crudService;
	private IridaJpaRepository<IdentifiableTestEntity, Long> crudRepository;
	private Validator validator;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		crudRepository = mock(IridaJpaRepository.class);
		crudService = new CRUDServiceImpl<>(crudRepository, validator, IdentifiableTestEntity.class);
	}

	@Test
	public void testAddValidObject() {
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setNonNull("Definitely not null.");
		i.setLabel("labelled");

		try {
			crudService.create(i);
		} catch (ConstraintViolationException constraintViolations) {
			fail();
		}
	}
	
	@Test(expected=EntityExistsException.class)
	public void testCreateEntityExists(){
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setId(1L);
		
		when(crudRepository.existsById(1L)).thenReturn(true);
		crudService.create(i);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void testUpdateNotExists(){
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setId(1L);
		
		when(crudRepository.existsById(1L)).thenReturn(false);
		crudService.update(i);
	}

	@Test
	public void testUpdateFields() throws InterruptedException {
		IdentifiableTestEntity before = new IdentifiableTestEntity();
		before.setNonNull("Definitely not null.");
		before.setIntegerValue(Integer.MIN_VALUE);
		Long id = 1L;
		before.setId(id);
		String newNonNull = "new value";

		when(crudRepository.existsById(id)).thenReturn(Boolean.TRUE);
		when(crudRepository.findById(id)).thenReturn(Optional.of(before));

		ArgumentCaptor<IdentifiableTestEntity> pageArgument = ArgumentCaptor.forClass(IdentifiableTestEntity.class);

		Map<String, Object> updatedFields = new HashMap<>();
		updatedFields.put("nonNull", newNonNull);
		// need to sleep for a bit so that the dates are different
		Thread.sleep(500L);
		crudService.updateFields(id, updatedFields);

		verify(crudRepository).save(pageArgument.capture());

		IdentifiableTestEntity captured = pageArgument.getValue();
		assertEquals(newNonNull, captured.getNonNull());
	}
	
	@Test
	public void testUpdateMultiple() {
		IdentifiableTestEntity ent1 = new IdentifiableTestEntity();
		ent1.setNonNull("Definitely not null.");
		ent1.setIntegerValue(Integer.MIN_VALUE);
		ent1.setLabel("label");
		Long id = 1L;
		ent1.setId(id);

		IdentifiableTestEntity ent2 = new IdentifiableTestEntity();
		ent2.setNonNull("Another entity");
		ent2.setIntegerValue(Integer.MAX_VALUE);
		ent2.setLabel("label");
		Long id2 = 2L;
		ent2.setId(id);

		when(crudRepository.existsById(id)).thenReturn(Boolean.TRUE);
		when(crudRepository.existsById(id2)).thenReturn(Boolean.TRUE);

		crudService.updateMultiple(Lists.newArrayList(ent1, ent2));

		verify(crudRepository).save(ent1);
		verify(crudRepository).save(ent2);
	}

	@Ignore
	@Test(expected = EntityNotFoundException.class)
	public void testUpdateMissingEntity() {
		Long id = new Long(1);
		Map<String, Object> updatedProperties = new HashMap<>();
		when(crudRepository.existsById(id)).thenReturn(Boolean.FALSE);

		crudService.updateFields(id, updatedProperties);
	}

	@Test
	public void testUpdateWithBadPropertyName() {
		IdentifiableTestEntity entity = new IdentifiableTestEntity();
		entity.setId(1L);
		Map<String, Object> updatedProperties = new HashMap<>();
		updatedProperties.put("noSuchField", new Object());
		when(crudRepository.findById(1L)).thenReturn(Optional.of(entity));

		try {
			crudService.updateFields(entity.getId(), updatedProperties);
			fail();
		} catch (InvalidPropertyException ex) {
			assertNotNull(ex.getAffectedClass());
		}
	}

	@Test
	public void testUpdateWithBadPropertyType() {
		IdentifiableTestEntity entity = new IdentifiableTestEntity();
		entity.setId(new Long(1));
		Map<String, Object> updatedProperties = new HashMap<>();
		updatedProperties.put("integerValue", new Object());
		when(crudRepository.findById(1L)).thenReturn(Optional.of(entity));

		try {
			crudService.updateFields(entity.getId(), updatedProperties);
			fail();
		} catch (InvalidPropertyException ex) {
			assertNotNull(ex.getAffectedClass());
		}
	}

	@Test
	public void testUpdateInvalidEntry() {
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setNonNull("Definitely not null.");
		i.setIntegerValue(Integer.MIN_VALUE);
		Long id = new Long(1);
		i.setId(id);
		when(crudRepository.existsById(id)).thenReturn(Boolean.TRUE);
		when(crudRepository.findById(id)).thenReturn(Optional.of(i));

		Map<String, Object> updatedFields = new HashMap<>();
		updatedFields.put("nonNull", null);
		try {
			crudService.updateFields(id, updatedFields);
			fail();
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			assertEquals(1, violations.size());
			ConstraintViolation<?> v = violations.iterator().next();
			assertEquals("nonNull", v.getPropertyPath().toString());
		}
	}

	@Test
	public void testRead() {
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setId(new Long(1));
		i.setNonNull("Definitely not null");

		when(crudRepository.findById(i.getId())).thenReturn(Optional.of(i));

		try {
			i = crudService.read(i.getId());
			assertNotNull(i);
		} catch (IllegalArgumentException e) {
			fail();
		}
	}

	@Test
	public void testList() {
		int itemCount = 10;
		List<IdentifiableTestEntity> entities = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) {
			entities.add(new IdentifiableTestEntity());
		}
		when(crudRepository.findAll()).thenReturn(entities);

		Iterable<IdentifiableTestEntity> items = crudService.findAll();

		assertEquals(entities, items);
	}

	@Test
	public void testExists() {
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setId(new Long(1));
		when(crudRepository.existsById(i.getId())).thenReturn(Boolean.TRUE);
		assertTrue(crudService.exists(i.getId()));
	}

	@Test
	public void testValidDelete() {
		IdentifiableTestEntity i = new IdentifiableTestEntity();
		i.setId(new Long(1));

		when(crudService.exists(i.getId())).thenReturn(Boolean.TRUE);

		try {
			crudService.delete(i.getId());
		} catch (EntityNotFoundException e) {
			fail();
		}
	}

	@Ignore
	@Test(expected = EntityNotFoundException.class)
	public void testInvalidDelete() {
		Long id = new Long(1);
		when(crudRepository.existsById(id)).thenReturn(Boolean.FALSE);

		crudService.delete(id);
	}

	@Ignore
	@Test(expected = EntityNotFoundException.class)
	public void testGetMissingEntity() {
		Long id = new Long(1);
		when(crudRepository.findById(id)).thenReturn(Optional.of(null));

		crudService.read(id);
	}

	@Test
	public void testCount() {
		long count = 30;
		when(crudRepository.count()).thenReturn(count);
		assertEquals(count, crudService.count());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSearch() {
		int page = 1;
		int size = 1;
		Direction order = Direction.ASC;
		Page<IdentifiableTestEntity> idPage = new PageImpl<>(Lists.newArrayList(new IdentifiableTestEntity(),
				new IdentifiableTestEntity()));
		when(crudRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(idPage);
		Page<IdentifiableTestEntity> search = crudService.search(IdentifiableTestEntitySpecification.search(), page,
				size, order);

		assertEquals(2, search.getTotalElements());

		ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

		verify(crudRepository).findAll(any(Specification.class), pageArgument.capture());

		// ensure a created date sort property is set
		Pageable pagable = pageArgument.getValue();
		Order sort = pagable.getSort().iterator().next();
		assertEquals("createdDate", sort.getProperty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSearchSortEmptyArray() {
		int page = 1;
		int size = 1;
		Direction order = Direction.ASC;
		Page<IdentifiableTestEntity> idPage = new PageImpl<>(Lists.newArrayList(new IdentifiableTestEntity(),
				new IdentifiableTestEntity()));
		when(crudRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(idPage);
		Page<IdentifiableTestEntity> search = crudService.search(IdentifiableTestEntitySpecification.search(), page,
				size, order, new String[0]);

		assertEquals(2, search.getTotalElements());

		ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

		verify(crudRepository).findAll(any(Specification.class), pageArgument.capture());

		// ensure a created date sort property is set
		Pageable pagable = pageArgument.getValue();
		Order sort = pagable.getSort().iterator().next();
		assertEquals("createdDate", sort.getProperty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSearchSortEmptyString() {
		int page = 1;
		int size = 1;
		Direction order = Direction.ASC;
		Page<IdentifiableTestEntity> idPage = new PageImpl<>(Lists.newArrayList(new IdentifiableTestEntity(),
				new IdentifiableTestEntity()));
		when(crudRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(idPage);
		Page<IdentifiableTestEntity> search = crudService.search(IdentifiableTestEntitySpecification.search(), page,
				size, order, "");

		assertEquals(2, search.getTotalElements());

		ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

		verify(crudRepository).findAll(any(Specification.class), pageArgument.capture());

		// ensure a created date sort property is set
		Pageable pagable = pageArgument.getValue();
		Order sort = pagable.getSort().iterator().next();
		assertEquals("createdDate", sort.getProperty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSearchSortSetProperty() {
		int page = 1;
		int size = 1;
		String property = "nonNull";
		Direction order = Direction.ASC;
		Page<IdentifiableTestEntity> idPage = new PageImpl<>(Lists.newArrayList(new IdentifiableTestEntity(),
				new IdentifiableTestEntity()));
		when(crudRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(idPage);
		Page<IdentifiableTestEntity> search = crudService.search(IdentifiableTestEntitySpecification.search(), page,
				size, order, property);

		assertEquals(2, search.getTotalElements());

		ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

		verify(crudRepository).findAll(any(Specification.class), pageArgument.capture());

		// ensure a created date sort property is set
		Pageable pagable = pageArgument.getValue();
		Order sort = pagable.getSort().iterator().next();
		assertEquals(property, sort.getProperty());
	}
}
