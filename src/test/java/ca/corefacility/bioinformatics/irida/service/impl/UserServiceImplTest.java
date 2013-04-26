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
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.ProjectMemoryRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.UserMemoryRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;
import java.util.Collection;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing the behavior of {@link UserServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImplTest {

//    private UserService userService;
//    private ProjectRepository projectRepository;
//    private UserRepository userRepository;
//    private Validator validator;
//
//    @Before
//    public void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//        userRepository = new UserMemoryRepository();
//        projectRepository = new ProjectMemoryRepository();
//        userService = new UserServiceImpl(userRepository, validator);
//    }
//
//    @Test
//    public void testGetUserByUsername() {
//        String username = "jadam";
//        User u = userService.getUserByUsername(username);
//        assertNotNull(u);
//        assertEquals(username, u.getUsername());
//    }
//
//    @Test
//    public void testBadUsername() {
//        String username = "superwrongusername";
//        try {
//            userService.getUserByUsername(username);
//            fail();
//        } catch (EntityNotFoundException e) {
//        } catch (Throwable e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void testGetUsersForProject() {
//        Project p = new Project();
//        User u = new User();
//        Role r = new Role();
//
//        p.addUserToProject(u, r);
//        u.addProject(p, r);
//
//        u = userRepository.create(u);
//        p = projectRepository.create(p);
//
//        Collection<User> users = userService.getUsersForProject(p);
//
//        assertEquals(1, users.size());
//        assertTrue(users.contains(u));
//    }
}
