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
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing the behavior of {@link UserServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private Validator validator;
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder, validator);
    }

    @Test
    // should throw the exception to the caller instead of swallowing it.
    public void testBadUsername() {
        String username = "superwrongusername";
        when(userRepository.getUserByUsername(username)).thenThrow(new EntityNotFoundException("not found"));
        try {
            userService.getUserByUsername(username);
            fail();
        } catch (EntityNotFoundException e) {
        } catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testBadPasswordCreate() {
        // a user should not be persisted with a bad password (like password1)
        String username = "fbristow";
        String password = "password1";
        String passwordEncoded = "$2a$10$vMzhJFdyM72NnnWIoMSbUecHRxZDtCE1fdiPfjfjT1WD0fISDXOX2";
        String email = "fbristow@gmail.com";
        String firstName = "Franklin";
        String lastName = "Bristow";
        String phoneNumber = "7029";
        UserIdentifier uid = new UserIdentifier();
        uid.setIdentifier(username);
        User user = new User(uid, username, email, password, firstName, lastName, phoneNumber);
        when(passwordEncoder.encode(password)).thenReturn(passwordEncoded);
        try {
            userService.create(user);
            fail();
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
            assertEquals(1, violationSet.size());
            ConstraintViolation<?> violation = violationSet.iterator().next();
            assertTrue(violation.getPropertyPath().toString().contains("password"));
        } catch (Exception e) {
            fail();
        }
    }

    /*
     * TODO: reimplement this test
     
    @Test
    public void testBadPasswordUpdate() {
        // a user should not be persisted with a bad password (like password1)
        String username = "fbristow";
        String password = "password1";
        String passwordEncoded = "$2a$10$vMzhJFdyM72NnnWIoMSbUecHRxZDtCE1fdiPfjfjT1WD0fISDXOX2";
        UserIdentifier uid = new UserIdentifier();
        uid.setIdentifier(username);
        Map<String, Object> properties = new HashMap<>();
        properties.put("password", password);

        when(passwordEncoder.encode(password)).thenReturn(passwordEncoded);
        try {
            userService.update(uid, properties);
            fail();
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
            assertEquals(1, violationSet.size());
            ConstraintViolation<?> violation = violationSet.iterator().next();
            assertTrue(violation.getPropertyPath().toString().contains("password"));
        } catch (Exception e) {
            fail();
        }
    }*/

    @Test
    public void testLoadUserByUsername() {
        String username = "fbristow";
        String password = "password1";
        String email = "fbristow@gmail.com";
        String firstName = "Franklin";
        String lastName = "Bristow";
        String phoneNumber = "7029";
        UserIdentifier uid = new UserIdentifier();
        uid.setIdentifier(username);
        User user = new User(uid, username, email, password, firstName, lastName, phoneNumber);

        when(userRepository.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }
}
