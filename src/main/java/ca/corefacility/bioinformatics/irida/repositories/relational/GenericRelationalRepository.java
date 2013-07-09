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
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.IntegerIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Transactional
@Repository
public class GenericRelationalRepository<Type extends IridaThing> implements CRUDRepository<Long, Type> {
    private String tableName;
    protected JdbcTemplate jdbcTemplate;
    protected SessionFactory sessionFactory;
    private Class classType;
    
    public GenericRelationalRepository(){}
    
    public GenericRelationalRepository(DataSource source,Class classType){
        this.jdbcTemplate = new JdbcTemplate(source);
        this.classType = classType;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    @Override
    public Type create(Type object) throws IllegalArgumentException {                
        Session session = sessionFactory.getCurrentSession();
        Serializable save = session.save(object);        
        
        String toString = save.toString();
        IntegerIdentifier id = new IntegerIdentifier(Integer.parseInt(toString));
        object.setIdentifier(id);
        return object;
    }
    
    @Override
    public Type read(Long id) throws EntityNotFoundException {
        
        Session session = sessionFactory.getCurrentSession();
        Type load = (Type) session.get(classType, id);
                
        return load;    
    }

    @Override
    public Collection<Type> readMultiple(Collection<Identifier> idents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    @Override
    public Type update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        Session session = sessionFactory.getCurrentSession();
        Type base = read(id);
        
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(base);
       
        for(String key : updatedFields.keySet()){
            Object value = updatedFields.get(key);

            fieldAccessor.setPropertyValue(key, value);
        }
        
        session.save(base);
        
        return base;
    }

    @Transactional
    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Session session = sessionFactory.getCurrentSession();

        if(!exists(id)){
            throw new StorageException("Entity with id " + id + " cannot be deleted because it doesn't exists");
        }
        
        Type read = read(id);
        session.delete(read);
    }

    @Override
    public List<Type> list() {
        return list(0, 20, null, Order.NONE);
    }

    @Override
    public List<Type> list(int page, int size, String sortProperty, Order order) {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria crit = session.createCriteria(classType.getName());

        
        List<Type> results;
        
        
        if(sortProperty != null){
            if(order == Order.ASCENDING){
                crit.addOrder(org.hibernate.criterion.Order.asc(sortProperty));
            }
            else if(order == Order.DESCENDING){
                crit.addOrder(org.hibernate.criterion.Order.desc(sortProperty));
            }
        }
        
        if(size > 0){
            crit.setMaxResults(size);
        }
        if(page > 0){
            int offset = page * size;
            crit.setFirstResult(offset);
        }
        
        results = crit.list();
        
        return results;
    }

    @Override
    public Boolean exists(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String name = classType.getName();
        String queryStr = "SELECT 1 FROM "+name+" WHERE id = :id";
        Query query = session.createQuery(queryStr);
        query.setLong("id", id );
        return (query.uniqueResult() != null);    
    }

    @Override
    public Integer count() {
        Session session = sessionFactory.getCurrentSession();
        String name = classType.getName();
        
        Number uniqueResult = (Number) session.createCriteria(name).setProjection(Projections.rowCount()).uniqueResult();
        int intValue = uniqueResult.intValue();
        
        return intValue; 
    }

    
}
