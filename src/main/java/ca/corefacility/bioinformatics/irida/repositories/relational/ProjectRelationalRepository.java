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
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.IntegerIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
//public class ProjectRelationalRepository extends GenericRelationalRepository<Identifier, Project> implements ProjectRepository{
@Transactional
@Repository
public class ProjectRelationalRepository implements ProjectRepository{
    
    private RowMapper<Project> rowMapper = new ProjectRowMapper();
    protected JdbcTemplate jdbcTemplate;
    
    private SessionFactory sessionFactory;
    public ProjectRelationalRepository(){}
    
    public ProjectRelationalRepository(DataSource source){
        this.jdbcTemplate = new JdbcTemplate(source);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public Project create(Project object) throws IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        Serializable save = session.save(object);        
        
        String toString = save.toString();
        IntegerIdentifier id = new IntegerIdentifier(Integer.parseInt(toString));
        object.setIdentifier(id);
        return object;
    }
    
    protected Project buildObject(Project p){
        IntegerIdentifier id = new IntegerIdentifier(p.getId().intValue());
        p.setIdentifier(id);
        return p;
    }
    

    public class ProjectRowMapper implements RowMapper<Project>{
        @Override
        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
            Project p = new Project(rs.getString("name"));
            IntegerIdentifier id = new IntegerIdentifier(rs.getInt("id"));
            p.setIdentifier(id);
            return p;
        }
        
    }
    
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
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
    
    protected PreparedStatementCreator getStatementCreator(Project object) {
        return new ProjectStatementCreator(object);
    }
    
    public class ProjectStatementCreator implements PreparedStatementCreator{
        private Project project;
        
        public ProjectStatementCreator(Project project){
            this.project = project;
        }

        public Project getObject() {
            return project;
        }

        public void setObject(Project project) {
            this.project = project;
        }
        
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                final String insert = "INSERT INTO project (name) VALUES (?)";
                PreparedStatement stmt = con.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, project.getName());
                
                return stmt;
        }
    
    }

    @Override
    public Project read(Identifier id) throws EntityNotFoundException {
        String identifier = id.getIdentifier();
        
        Session session = sessionFactory.getCurrentSession();
        Project load = (Project) session.load(Project.class, Long.parseLong(identifier));
        
        IntegerIdentifier readId = new IntegerIdentifier(load.getId().intValue());
        load.setIdentifier(readId);
        
        return load;
    }

    @Override
    public Collection<Project> readMultiple(Collection<Identifier> idents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Project update(Identifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        String update = "UPDATE project SET ";
       
        List<String> args = new ArrayList<>();
        for(String key : updatedFields.keySet()){
            Object value = updatedFields.get(key);
            update += " "+key+"=? ";
            args.add(value.toString());
        }
        update += " WHERE id=?";
        
        args.add(id.getIdentifier());
        
        this.jdbcTemplate.update(update,args.toArray());
        
        return this.read(id);
    }

    @Override
    public void delete(Identifier id) throws EntityNotFoundException {
        if(!exists(id)){
            throw new StorageException("Entity with id " + id.getIdentifier() + " cannot be deleted because it doesn't exists");
        }
        String update = "DELETE FROM project WHERE id=?";
        
        this.jdbcTemplate.update(update,id.getIdentifier());
    }

    @Override
    public List<Project> list() {
        return list(0, 20, null, Order.NONE);
    }

    @Override
    public List<Project> list(int page, int size, String sortProperty, Order order) {
        
        Session session = sessionFactory.getCurrentSession();       
        
        List<Project> query1;
        String query = "FROM Project";
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
        
        for(Project p : query1){
            p = buildObject(p);
        }        
        
        return query1;
    }

    @Override
    public Boolean exists(Identifier id) {
        String query = "SELECT count(id) FROM project WHERE id=?";
        Integer queryForObject = this.jdbcTemplate.queryForObject(query, Integer.class,id.getIdentifier());
        
        return queryForObject > 0;
    }

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
