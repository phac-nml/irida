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
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Testing the behavior of {@link UserServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository, validator);
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
}
