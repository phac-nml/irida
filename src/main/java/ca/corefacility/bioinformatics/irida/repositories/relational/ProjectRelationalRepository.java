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
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.FieldMap;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.IntegerIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Transactional
@Repository
public class ProjectRelationalRepository extends GenericRelationalRepository<Project> implements ProjectRepository{

//public class ProjectRelationalRepository implements ProjectRepository{
    
    
    public ProjectRelationalRepository(){}
    
    public ProjectRelationalRepository(DataSource source){
        super(source,Project.class);
    }


    /*@Transactional
    @Override
    public Project create(Project object) throws IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        Serializable save = session.save(object);        
        
        String toString = save.toString();
        IntegerIdentifier id = new IntegerIdentifier(Integer.parseInt(toString));
        object.setIdentifier(id);
        return object;
    }*/
    
    
    @Override
    public Collection<Project> getProjectsForUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Relationship addUserToProject(Project project, User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeUserFromProject(Project project, User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
    @Override
    public Project read(Long id) throws EntityNotFoundException {
        
        Session session = sessionFactory.getCurrentSession();
        Project load = (Project) session.load(Project.class, id);
                
        return load;
    }*/

    @Override
    public Collection<Project> readMultiple(Collection<Identifier> idents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
    @Transactional
    @Override
    public Project update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        Session session = sessionFactory.getCurrentSession();
        Project base = (Project) session.get(Project.class, id);        
        
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(base);
       
        for(String key : updatedFields.keySet()){
            Object value = updatedFields.get(key);

            fieldAccessor.setPropertyValue(key, value);
        }
        
        session.save(base);
        
        return base;
    }*/

    /*
    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Session session = sessionFactory.getCurrentSession();

        if(!exists(id)){
            throw new StorageException("Entity with id " + id + " cannot be deleted because it doesn't exists");
        }
        
        Project read = read(id);
        session.delete(read);
    }*/

    /*
    @Override
    public List<Project> list() {
        return list(0, 20, null, Order.NONE);
    }

    @Override
    public List<Project> list(int page, int size, String sortProperty, Order order) {
        
        Session session = sessionFactory.getCurrentSession();
        
        String name = Project.class.getName();
        List<Project> query1;
        String query = "FROM "+name;
        if(sortProperty != null){
            query += " ORDER BY ?";
            if(order == Order.ASCENDING){
                query += " ASC";
            }
            else if(order == Order.DESCENDING){
                query += " DESC";
            }
            Query createQuery = session.createQuery(query);
            createQuery.setString(1, sortProperty);
            query1 = createQuery.list();
        }
        else{
            Query createQuery = session.createQuery(query);
            query1 = createQuery.list();
        }    
        
        return query1;
    }*/

    /*
    @Override
    public Boolean exists(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String name = Project.class.getName();
        String queryStr = "SELECT 1 FROM "+name+" WHERE id = :id";
        Query query = session.createQuery(queryStr);
        query.setLong("id", id );
        return (query.uniqueResult() != null);
    }*/

    @Override
    public Integer count() {
        String query = "SELECT count(id) FROM project";
        Integer queryForObject = this.jdbcTemplate.queryForObject(query, Integer.class);
        
        return queryForObject;   
    }

    @Override
    public List<FieldMap> listMappedFields(List<String> fields) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FieldMap> listMappedFields(List<String> fields, int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
