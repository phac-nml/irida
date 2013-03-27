/*
 * Copyright 2013 Franklin Bristow.
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
package ca.corefacility.bioinformatics.irida.model;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing the validation for user objects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullUsername() {
        User u = new User();
        u.setUsername(null);

        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testEmptyUsername() {
        User u = new User();
        u.setUsername("");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testValidUsername() {
        User u = new User();
        u.setUsername("fbristow");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testShortPassword() {
        User u = new User();
        u.setPassword("Sma11");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testWeakLowercasePassword() {
        User u = new User();
        u.setPassword("a11-1owercase");

        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testWeakNoNumbersPassword() {
        User u = new User();
        u.setPassword("NoNumbers");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testWeakPassword() {
        User u = new User();
        u.setPassword("weak");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(3, constraintViolations.size());
    }
    
    @Test
    public void testValidUser() {
        User u = new User();
        u.setUsername("fbristow");
        u.setEmail("franklin.bristow+plusSymbolsAREValid@phac-aspc.gc.ca");
        u.setPassword("SuperVa1idP4ssw0rd");
        u.setFirstName("Franklin");
        u.setLastName("Bristow");
        u.setPhoneNumber("7029");
        
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(u);
        
        assertTrue(constraintViolations.isEmpty());
    }
}
