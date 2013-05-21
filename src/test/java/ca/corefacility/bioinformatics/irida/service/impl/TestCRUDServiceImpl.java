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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import com.google.common.collect.ImmutableMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the behavior of {@link CRUDServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class TestCRUDServiceImpl {

    private CRUDService<Identifier, IdentifiableTestEntity> crudService;
    private CRUDRepository<Identifier, IdentifiableTestEntity> crudRepository;
    private Validator validator;
    private SimpleDateFormat dateFormatter;

    @Before
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
            assertEquals(1, constraintViolations.getConstraintViolations().size());
        }
    }

    @Test
    public void testAddValidObject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");

        try {
            crudService.create(i);
        } catch (ConstraintViolationException constraintViolations) {
            fail();
        }
    }

    @Test
    public void testUpdateMissingEntity() {
        Identifier id = new Identifier();
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
        Map<String, Object> updatedProperties = new HashMap<>();
        updatedProperties.put("noSuchField", new Object());
        try {
            crudService.update(entity.getIdentifier(), updatedProperties);
            fail();
        } catch (InvalidPropertyException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateWithBadPropertyType() {
        IdentifiableTestEntity entity = new IdentifiableTestEntity();
        Map<String, Object> updatedProperties = new HashMap<>();
        updatedProperties.put("integerValue", new Object());
        try {
            crudService.update(entity.getIdentifier(), updatedProperties);
            fail();
        } catch (InvalidPropertyException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdatedValidEntity() {
        String oldField = "Absolutely not null";
        String newField = "super not null.";
        Integer oldIntegerValue = 30;
        Integer newIntegerValue = 50;
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull(oldField);
        i.setIntegerValue(oldIntegerValue);
        Identifier id = new Identifier();
        i.setIdentifier(id);
        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.read(id)).thenReturn(i);
        //when(crudRepository.update(i)).thenReturn(i);

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("nonNull", newField);
        updatedFields.put("integerValue", newIntegerValue);
        when(crudRepository.update(id, updatedFields)).thenReturn(i);
        try {
            i = crudService.update(id, updatedFields);
        } catch (ConstraintViolationException e) {
            fail();
        }
        assertEquals(newField, i.getNonNull());
        assertEquals(newIntegerValue, i.getIntegerValue());
        assertNotNull(i.getAuditInformation().getUpdated());
    }

    @Test
    public void testUpdateInvalidEntry() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");
        i.setIntegerValue(Integer.MIN_VALUE);
        Identifier id = new Identifier();
        i.setIdentifier(id);
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
        i.setNonNull("Definitely not null");

        when(crudRepository.read(i.getIdentifier())).thenReturn(i);

        try {
            i = crudService.read(i.getIdentifier());
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
        when(crudRepository.exists(i.getIdentifier())).thenReturn(Boolean.TRUE);
        assertTrue(crudService.exists(i.getIdentifier()));
    }

    @Test
    public void testValidDelete() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();

        when(crudService.exists(i.getIdentifier())).thenReturn(Boolean.TRUE);

        try {
            crudService.delete(i.getIdentifier());
        } catch (EntityNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testInvalidDelete() {
        Identifier id = new Identifier();
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
            Audit audit = new Audit();
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            audit.setCreated(dateFormatter.parse(date.toString()));
            entity.setAuditInformation(audit);
            created.add(entity);
        }

        when(crudRepository.list(2, 15, "auditInformation", Order.ASCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, "auditInformation", Order.ASCENDING);

        assertEquals(15, list.size());

        // the second 15 items in the list should be there
        for (int i = 0; i < LIST_SIZE; i++) {
            assertEquals(created.get(i), list.get(i));
        }
    }

    @Test
    public void testGetMissingEntity() {
        Identifier id = new Identifier();
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
            Audit audit = new Audit();
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            audit.setCreated(dateFormatter.parse(date.toString()));
            entity.setAuditInformation(audit);
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
            Audit audit = new Audit();
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            audit.setCreated(dateFormatter.parse(date.toString()));
            entity.setAuditInformation(audit);
            created.add(entity);
        }

        when(crudRepository.list(2, 15, "auditInformation", Order.DESCENDING)).thenReturn(created);

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, "auditInformation", Order.DESCENDING);

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
            Audit audit = new Audit();
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            audit.setCreated(dateFormatter.parse(date.toString()));
            entity.setAuditInformation(audit);
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

    /**
     * Audit information must be created by the service class just before being
     * inserted into the database. We cannot rely on the class to manage that
     * information itself.
     */
    @Test
    public void testSetAuditInformation() {
        IdentifiableTestEntity e = new IdentifiableTestEntity();
        e.setNonNull("Not null");
        e.setAuditInformation(null);
        when(crudRepository.create(e)).thenReturn(e);

        e = crudService.create(e);

        assertNotNull(e.getAuditInformation());
        assertTrue(e.getAuditInformation().getCreated().compareTo(new Date()) <= 0);

        verify(crudRepository).create(e);
    }

    @Test
    public void testUpdateSetAuditInformation() {
        Identifier id = new Identifier();
        IdentifiableTestEntity e = new IdentifiableTestEntity();
        e.setIdentifier(id);
        e.setNonNull("Not null");
        e.setAuditInformation(new Audit());
        
        ImmutableMap<String, Object> changed = ImmutableMap.of("nonNull", (Object) "another not null");
        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.read(id)).thenReturn(e);
        when(crudRepository.update(id,changed)).thenReturn(e);

        e = crudService.update(id, changed);
        
        assertNotNull(e.getAuditInformation().getUpdated());
        assertTrue(e.getAuditInformation().getUpdated().compareTo(new Date()) <= 0);
        
        verify(crudRepository).exists(id);
        verify(crudRepository).read(id);
        //verify(crudRepository).update(e);
        verify(crudRepository).update(id,changed);
    }
}
