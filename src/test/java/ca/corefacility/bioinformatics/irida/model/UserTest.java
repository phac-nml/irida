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

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

/**
 * Testing the validation for user objects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserTest {

    private static final String MESSAGES_BASENAME = "ca.corefacility.bioinformatics.irida.validation.ValidationMessages";
    private Validator validator;
    private ResourceBundle b;

    @Before
    public void setUp() {
        b = ResourceBundle.getBundle(MESSAGES_BASENAME);
        Configuration<?> configuration = Validation.byDefaultProvider().configure();
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MESSAGES_BASENAME);
        configuration.messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource)));
        ValidatorFactory factory = configuration.buildValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    public void testNullUsername() {
        User u = new User();
        u.setUsername(null);

        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.username.notnull"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyUsername() {
        User u = new User();
        u.setUsername("");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.username.size"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testValidUsername() {
        User u = new User();
        u.setUsername("fbristow");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testShortPassword() {
        User u = new User();
        u.setPassword("Sma11");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.password.size"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testWeakLowercasePassword() {
        User u = new User();
        u.setPassword("a11-1owercase");

        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.password.uppercase"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testWeakNoNumbersPassword() {
        User u = new User();
        u.setPassword("NoNumbers");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.password.number"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testWeakPassword() {
        User u = new User();
        u.setPassword("weak");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");
        Set<String> messages = new HashSet<>();
        messages.add(b.getString("user.password.size"));
        messages.add(b.getString("user.password.uppercase"));
        messages.add(b.getString("user.password.number"));

        assertEquals(3, constraintViolations.size());
        for (ConstraintViolation violation : constraintViolations) {
            assertTrue(messages.contains(violation.getMessage()));
            messages.remove(violation.getMessage());
        }
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testNullEmail() {
        User u = new User();
        u.setEmail(null);
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.email.notnull"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testShortEmail() {
        User u = new User();
        u.setEmail("s@s"); // technically valid, too short
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.email.size"), constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        User u = new User();
        u.setEmail("a stunningly incorrect e-mail address.");
        Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

        assertEquals(1, constraintViolations.size());
        assertEquals(b.getString("user.email.invalid"), constraintViolations.iterator().next().getMessage());
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

        u2.setAuditInformation(a1);
        u1.setAuditInformation(a2);
        u3.setAuditInformation(a3);

        // users are in the wrong order
        users.add(u3);
        users.add(u1);
        users.add(u2);

        Collections.sort(users);

        User curr = users.get(0);
        for (int i = 1; i < users.size(); i++) {
            assertTrue(curr.getAuditInformation().getCreated().compareTo(users.get(i).getAuditInformation().getCreated()) < 0);
        }
    }

    @Test
    public void testRemoveProject() {
        User u = new User();
        Map<Project, Role> projects = new HashMap<>();
        Identifier id = new Identifier();
        Project p = new Project();
        p.setIdentifier(id);
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
        p.setIdentifier(id);
        u.addProject(p, new Role());

        assertTrue(u.getProjects().containsKey(p));
    }

    @Test
    public void testEquals() {
        User u1 = new User(new Identifier(), "username", "email", "password", "firstName", "lastName", "phoneNumber");
        User u2 = new User(new Identifier(), "username", "email", "password", "firstName", "lastName", "phoneNumber");
        // the two users DO NOT share the same identifier, and should therefore be different
        assertTrue(!u1.equals(u2));

        u2.setIdentifier(u1.getIdentifier());
        // now the two users share the same identifier, and should therefore be the same
        assertTrue(u1.equals(u2));
    }
}
