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
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.CRUDMemoryRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.Assert.*;
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

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = new CRUDMemoryRepository<>(IdentifiableTestEntity.class);
        crudService = new CRUDServiceImpl<>(crudRepository, validator, IdentifiableTestEntity.class);
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
    public void testUpdateInvalidProject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null");

        crudRepository.create(i);

        // make the project invalid
        i.setNonNull(null);
        try {
            crudService.update(i);
            fail();
        } catch (ConstraintViolationException constraintViolations) {
            assertEquals(1, constraintViolations.getConstraintViolations().size());
        }
    }

    @Test
    public void testUpdateValidProject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");

        i = crudRepository.create(i);

        // change the non-null, but keep it non-null
        i.setNonNull("Also definitely not null.");

        try {
            crudService.update(i);
        } catch (ConstraintViolationException constraintViolations) {
            fail();
        }

        i = crudRepository.read(i.getIdentifier());
        assertEquals("Also definitely not null.", i.getNonNull());
    }

    @Test
    public void testRead() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null");
        i = crudRepository.create(i);

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
        for (int i = 0; i < itemCount; i++) {
            crudRepository.create(new IdentifiableTestEntity());
        }

        List<IdentifiableTestEntity> items = crudService.list();

        assertEquals(itemCount, items.size());
    }

    @Test
    public void testExists() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i = crudRepository.create(i);

        assertTrue(crudService.exists(i.getIdentifier()));
    }

    @Test
    public void testValidDelete() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i = crudRepository.create(i);

        try {
            crudService.delete(i.getIdentifier());
        } catch (EntityNotFoundException e) {
            fail();
        }

        assertFalse(crudRepository.exists(i.getIdentifier()));
    }

    @Test
    public void testInvalidDelete() {
        try {
            crudService.delete(new Identifier());
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
    public void testPagedResults() {
        final int LIST_SIZE = 30;
        List<IdentifiableTestEntity> created = new ArrayList<>(LIST_SIZE);
        for (int i = 1; i < LIST_SIZE + 1; i++) {
            Audit audit = new Audit();
            IdentifiableTestEntity entity = new IdentifiableTestEntity();
            StringBuilder date = new StringBuilder("2013-04-");
            if (i < 10) {
                date.append("0");
            }
            date.append(i);
            audit.setCreated(Date.valueOf(date.toString()));
            entity.setAuditInformation(audit);
            created.add(crudRepository.create(entity));
        }

        // page 2 with 15 items should return a list of size 15
        List<IdentifiableTestEntity> list = crudService.list(2, 15, "auditInformation", Order.ASCENDING);

        assertEquals(15, list.size());

        // the first 15 items in the list should not be there
        for (int i = 0; i < 15; i++) {
            assertFalse(list.contains(created.get(i)));
        }

        // the second 15 items in the list should be there
        for (int i = 15; i < LIST_SIZE; i++) {
            assertTrue(list.contains(created.get(i)));
        }
    }
    
    @Test
    public void testGetMissingEntity() {
        try {
            Identifier id = new Identifier();
            crudService.read(id);
            fail();
        } catch (EntityNotFoundException e) {
            
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
}
