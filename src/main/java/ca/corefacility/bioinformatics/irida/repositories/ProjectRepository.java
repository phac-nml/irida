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
package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

import java.util.Collection;

/**
 * Specialized repository for {@link Project}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface ProjectRepository extends CRUDRepository<Long, Project> {

    /**
     * Get all {@link Project}s associated with a particular {@link User}.
     *
     * @param user the user to get projects for.
     * @return the projects associated with the user.
     */
    public Collection<ProjectUserJoin> getProjectsForUser(User user);

    /**
     * Add a user to a project
     *
     * @param project The project to add the user to
     * @param user    The user to add
     * @return A {@link Relationship} object describing the project/user link
     */
    public ProjectUserJoin addUserToProject(Project project, User user);

    /**
     * Remove a {@link User} from a {@link Project}.
     *
     * @param project the {@link Project} to remove the {@link User} from.
     * @param user    the {@link User} to remove from the {@link Project}.
     */
    public void removeUserFromProject(Project project, User user);
}
