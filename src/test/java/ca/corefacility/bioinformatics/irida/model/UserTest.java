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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the validation for user objects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserTest {

    private Logger logger = LoggerFactory.getLogger(UserTest.class);
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

    @Test
    public void testCompareTo() {
        // should be able to sort users in ascending order of their creation date
        List<User> users = new ArrayList<>();

        User u1 = new User();
        User u2 = new User();
        User u3 = new User();

        Audit a1 = new Audit();
        Audit a2 = new Audit();
        Audit a3 = new Audit();

        a1.setCreated(Date.valueOf("2011-1-1"));
        a2.setCreated(Date.valueOf("2012-1-1"));
        a3.setCreated(Date.valueOf("2013-1-1"));

        u1.setAudit(a1);
        u2.setAudit(a2);
        u3.setAudit(a3);

        // users are in the wrong order
        users.add(u3);
        users.add(u1);
        users.add(u2);

        Collections.sort(users);

        User curr = users.get(0);
        for (int i = 1; i < users.size(); i++) {
            assertTrue(curr.getAudit().getCreated().compareTo(users.get(i).getAudit().getCreated()) < 0);
        }
    }

    @Test
    public void testRemoveProject() {
        User u = new User();
        Map<Project, Role> projects = new HashMap<>();
        Identifier id = new Identifier();
        Project p = new Project();
        p.setId(id);
        projects.put(p, new Role());
        u.setProjects(projects);

        // now remove the project
        u.removeProject(p);

        assertTrue(projects.isEmpty());
    }

    @Test
    public void testAddProject() {
        User u = new User();
        Identifier id = new Identifier();
        Project p = new Project();
        p.setId(id);
        u.addProject(p, new Role());

        assertTrue(u.getProjects().containsKey(p));
    }

    @Test
    public void testEquals() {
        User u1 = new User(new Identifier(), "username", "email", "password", "firstName", "lastName", "phoneNumber");
        User u2 = new User(new Identifier(), "username", "email", "password", "firstName", "lastName", "phoneNumber");
        // the two users DO NOT share the same identifier, and should therefore be different
        assertTrue(!u1.equals(u2));

        u2.setId(u1.getId());
        // now the two users share the same identifier, and should therefore be the same
        assertTrue(u1.equals(u2));
    }
}
