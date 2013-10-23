package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;

/**
 * Testing the behavior of {@link CRUDServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class TestCRUDServiceImpl {

    private CRUDService<Long, IdentifiableTestEntity> crudService;
    private CRUDRepository<Long, IdentifiableTestEntity> crudRepository;
    private Validator validator;
    private SimpleDateFormat dateFormatter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = mock(CRUDRepository.class);
        crudService = new CRUDServiceImpl<>(crudRepository, validator, IdentifiableTestEntity.class);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Test
    public void testAddInvalidObject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity(); // nothing is set, this should be invalid

        try {
            crudService.create(i);
            fail();
        } catch (ConstraintViolationException constraintViolations) {
            assertEquals(2, constraintViolations.getConstraintViolations().size());
        }
    }
    
    @Test
    public void addObjectWithoutLabel() {
        IdentifiableTestEntity i = new IdentifiableTestEntity(); // nothing is set, this should be invalid
        i.setNonNull("Definitely not null.");
        
        try {
            crudService.create(i);
            fail();
        } catch (ConstraintViolationException constraintViolations) {

            assertEquals(1, constraintViolations.getConstraintViolations().size());
            ConstraintViolation<?> next = constraintViolations.getConstraintViolations().iterator().next();
            Path propertyPath = next.getPropertyPath();
            assertEquals("label", propertyPath.toString());
        }
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

    @Test
    public void testUpdateMissingEntity() {
        Long id = new Long(1);
        Map<String, Object> updatedProperties = new HashMap<>();
        when(crudRepository.exists(id)).thenReturn(Boolean.FALSE);
        try {
            crudService.update(id, updatedProperties);
            fail();
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateWithBadPropertyName() {
        IdentifiableTestEntity entity = new IdentifiableTestEntity();
        entity.setId(new Long(1));
        Map<String, Object> updatedProperties = new HashMap<>();
        updatedProperties.put("noSuchField", new Object());
        try {
            crudService.update(entity.getId(), updatedProperties);
            fail();
        } catch (InvalidPropertyException e) {
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void testUpdateWithBadPropertyType() {
        IdentifiableTestEntity entity = new IdentifiableTestEntity();
        entity.setId(new Long(1));
        Map<String, Object> updatedProperties = new HashMap<>();
        updatedProperties.put("integerValue", new Object());
        try {
            crudService.update(entity.getId(), updatedProperties);
            fail();
        } catch (InvalidPropertyException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateInvalidEntry() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");
        i.setIntegerValue(Integer.MIN_VALUE);
        Long id = new Long(1);
        i.setId(id);
        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.read(id)).thenReturn(i);

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("nonNull", null);
        try {
            crudService.update(id, updatedFields);
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

        when(crudRepository.read(i.getId())).thenReturn(i);

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
        when(crudRepository.list()).thenReturn(entities);

        List<IdentifiableTestEntity> items = crudService.list();

        assertEquals(itemCount, items.size());
    }

    @Test
    public void testExists() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setId(new Long(1));
        when(crudRepository.exists(i.getId())).thenReturn(Boolean.TRUE);
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
    
    @Test
    public void testInvalidDelete() {
        Long id = new Long(1);
        when(crudRepository.exists(id)).thenReturn(Boolean.FALSE);
        try {
            crudService.delete(id);
            fail();
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            fail();
        }
    }
    

    @Test
    public void testPagedResultsBadProperty() {
        try {
            crudService.list(2, 20, "somePropertyThatDefinitelyDoesntExist", Order.ASCENDING);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testPagedResults() throws ParseException {
        final int LIST_SIZE = 15;
        List<IdentifiableTestEntity> created = new ArrayList<>(LIST_SIZE);
        for (int i = 1; i < LIST_SIZE + 1; i++) {
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            entity.setTimestamp(dateFormatter.parse(date.toString()));
            created.add(entity);
        }

        when(crudRepository.list(2, 15, "createdDate", Order.ASCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, "createdDate", Order.ASCENDING);

        assertEquals(15, list.size());

        // the second 15 items in the list should be there
        for (int i = 0; i < LIST_SIZE; i++) {
            assertEquals(created.get(i), list.get(i));
        }
    }
    
    @Test
    public void testGetMissingEntity() {
        Long id = new Long(1);
        when(crudRepository.read(id)).thenThrow(new EntityNotFoundException("not found"));
        try {
            crudService.read(id);
            fail();
        } catch (EntityNotFoundException e) {
        } catch (Throwable e) {
            fail();
        }
    }
    

    @Test
    public void testCount() {
        int count = 30;
        when(crudRepository.count()).thenReturn(count);
        assertEquals(count, crudService.count().intValue());
    }

    @Test
    public void testPagedResultsDefaultOrderBy() throws ParseException {
        final int LIST_SIZE = 15;
        List<IdentifiableTestEntity> created = new ArrayList<>(LIST_SIZE);
        for (int i = 1; i < LIST_SIZE + 1; i++) {
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            entity.setTimestamp(dateFormatter.parse(date.toString()));
            created.add(entity);
        }

        when(crudRepository.list(2, 15, null, Order.ASCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, Order.ASCENDING);

        assertEquals(15, list.size());

        // the second 15 items in the list should be there
        for (int i = 0; i < LIST_SIZE; i++) {
            assertEquals(created.get(i), list.get(i));
        }
    }

    @Test
    public void testPagedResultsDescending() throws ParseException {
        final int LIST_SIZE = 15;
        List<IdentifiableTestEntity> created = new ArrayList<>(LIST_SIZE);
        for (int i = 1; i < LIST_SIZE + 1; i++) {
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            entity.setTimestamp(dateFormatter.parse(date.toString()));
            created.add(entity);
        }

        when(crudRepository.list(2, 15, "createdDate", Order.DESCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, "createdDate", Order.DESCENDING);

        assertEquals(LIST_SIZE, list.size());

        // the first 15 items in the list should be there
        for (int i = 0; i < LIST_SIZE; i++) {
            assertEquals(created.get(i), list.get(i));
        }
    }

    @Test
    public void testPagedResultsDefaultOrderByDescending() throws ParseException {
        final int LIST_SIZE = 15;
        List<IdentifiableTestEntity> created = new ArrayList<>(LIST_SIZE);
        for (int i = 1; i < LIST_SIZE + 1; i++) {
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            //audit.setCreated(dateFormatter.parse(date.toString()));
            entity.setTimestamp(dateFormatter.parse(date.toString()));
            created.add(entity);
        }

        when(crudRepository.list(2, 15, null, Order.DESCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, Order.DESCENDING);

        assertEquals(15, list.size());

        // the first 15 items in the list should be there
        for (int i = 0; i < 15; i++) {
            assertEquals(created.get(i), list.get(i));
        }
    }


}
