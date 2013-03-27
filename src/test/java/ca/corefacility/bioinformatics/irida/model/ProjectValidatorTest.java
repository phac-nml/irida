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
package ca.corefacility.bioinformatics.irida.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests that projects must have at least one user assigned to them.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testProjectsHaveNoUsers() {
        Project p = new Project();
        Map<User, Role> users = new HashMap<>();

        p.setUsers(users);

        Set<ConstraintViolation<Project>> constraintViolations = validator.validateProperty(p, "users");

        assertEquals(1, constraintViolations.size());
    }
    
    @Test
    public void testProjectsHaveAtLeastOneUser() {
        Project p = new Project();
        Map<User, Role> users = new HashMap<>();
        users.put(new User(), new Role());
        p.setUsers(users);
        
        Set<ConstraintViolation<Project>> constraintViolations = validator.validateProperty(p, "users");
        assertEquals(0, constraintViolations.size());
    }
}
