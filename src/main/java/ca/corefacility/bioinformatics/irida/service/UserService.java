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
package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

/**
 * Customized service for accessing user objects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserService extends CRUDService<Long, User>, UserDetailsService {

    /**
     * Get a user from the database with the supplied username.
     *
     * @param username the user's username.
     * @return the user corresponding to the username.
     */
    public User getUserByUsername(String username) throws EntityNotFoundException;

    /**
     * Get all users associated with a particular project.
     *
     * @param project the project to get users for.
     * @return the users associated with the project.
     */
    public Collection<Relationship> getUsersForProject(Identifier project);
}
