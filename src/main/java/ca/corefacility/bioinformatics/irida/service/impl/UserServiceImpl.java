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
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;
import java.util.Collection;
import javax.validation.Validator;

/**
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImpl extends CRUDServiceImpl<UserIdentifier, User> implements UserService {

    public UserServiceImpl(CRUDRepository<UserIdentifier, User> userRepository, Validator validator) {
        super(userRepository, validator, User.class);
    }

    @Override
    public User getUserByUsername(String username) {
        User u = userRepository().getUserByUsername(username);
        if (u == null) {
            throw new EntityNotFoundException("No user with username [" + username
                    + "] exists.");
        }
        return u;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> getUsersForProject(Project project) {
        return userRepository().getUsersForProject(project);
    }

    private UserRepository userRepository() {
        return (UserRepository) repository;
    }
}
