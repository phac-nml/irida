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

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserRelationalRepository extends GenericRelationalRepository<User> implements UserRepository{
    public UserRelationalRepository(){}
    
    public UserRelationalRepository(DataSource source){
        super(source,User.class);
    }    

    @Transactional
    @Override
    public User create(User object) throws IllegalArgumentException {
        Session session = this.sessionFactory.getCurrentSession();
        
        Role role = object.getRole();
        Criteria roleQuery = session.createCriteria(Role.class);
        roleQuery.add(Restrictions.like("name", role.getName()));
        Role retRole = (Role) roleQuery.uniqueResult();
        if(retRole == null){
            throw new IllegalArgumentException("Role " + role.getName() + " doesn't exist in the database.  User cannot be created");
        }
        
        object.setRole(retRole);
        
        return super.create(object); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(User.class);
        crit.add(Restrictions.like("username", username));
        User u = (User) crit.uniqueResult();
        
        if(u == null){
            throw new EntityNotFoundException("User "+username+" doesn't exist in the database");
        }
        
        return u;
    }

    @Override
    public Collection<User> getUsersForProject(Project project) {
        Session session = sessionFactory.getCurrentSession();
        String qs = "SELECT u.* FROM user u, project_user pu WHERE pu.user=u.id AND pu.project = :pid";
        Query query = session.createSQLQuery(qs).addEntity(User.class);
        query.setLong("pid", project.getId());
        
        List<User> list = query.list();
        
        return list;    
    }
}
