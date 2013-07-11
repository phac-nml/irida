/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
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
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Transactional
@Repository
public class ProjectRelationalRepository extends GenericRelationalRepository<Project> implements ProjectRepository{    
    
    public ProjectRelationalRepository(){}
    
    public ProjectRelationalRepository(DataSource source){
        super(source,Project.class);
    }
    
    @Transactional
    @Override
    public Collection<Project> getProjectsForUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        String qs = "SELECT p.* FROM project p, project_user pu WHERE pu.project=p.id AND pu.user = :uid";
        Query query = session.createSQLQuery(qs).addEntity(Project.class);
        query.setLong("uid", user.getId());
        
        List<Project> list = query.list();
        
        return list;
    }

    @Transactional
    @Override
    public Relationship addUserToProject(Project project, User user) {
        String addUser = "INSERT INTO project_user (project,user) VALUES (?,?)";
        this.jdbcTemplate.update(addUser,project.getId(),user.getId());
        
        return null;
    }

    @Transactional
    @Override
    public void removeUserFromProject(Project project, User user) {
        String query = "DELETE FROM project_user WHERE project=? AND user=?";
        int update = this.jdbcTemplate.update(query,project.getId(),user.getId());
        
        if(update != 1){
            throw new StorageException("Removing user from project affected more than 1 row.");
        }
        
    }

    
    
}
