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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
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
    protected JdbcTemplate jdbcTemplate;
    protected SessionFactory sessionFactory;
    private Class classType;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
            GenericRelationalRepository.class); //Logger to use for this repository    
    
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
    
    /**
     * {@inheritDoc }
     */
    @Transactional
    @Override
    public Type create(Type object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }
        
        if(object.getId() != null && exists(object.getId())) {
            throw new EntityExistsException("Object " + object + " already exists in the database");
        }
        
        Session session = sessionFactory.getCurrentSession();
        session.persist(object);        
        
        return object;
    }
    
    /**
     * Perform operations on an object after it's been loaded, but before returning.
     * In this default case, nothing will be done.  Can be overridden to perform other operations
     * @param object The object to perform operations on
     */
    protected Type postLoad(Type object){
        return object;
    }
    
    /**
     * {@inheritDoc }
     */    
    @Override
    public Type read(Long id) throws EntityNotFoundException {
        
        Session session = sessionFactory.getCurrentSession();
        Type load = (Type) session.get(classType, id);
        
        if(load == null){
            throw new EntityNotFoundException("Entity " + id + " couldn't be found in the database.");
        }
        
        postLoad(load);
                
        return load;    
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Type> readMultiple(Collection<Long> idents) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(classType);
        crit.add(Restrictions.in("id", idents));
        List<Type> list = crit.list();
        
        return list;
    }
    
    /**
     * {@inheritDoc }
     */
    @Transactional
    @Override
    public Type update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        Session session = sessionFactory.getCurrentSession();
        
        Type base = read(id);
        
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(base);
       
        for(String key : updatedFields.keySet()){
            Object value = updatedFields.get(key);

            try{
                fieldAccessor.setPropertyValue(key, value);
            }
            catch(BeansException ex){
                logger.error(ex.getMessage());
                throw new InvalidPropertyException("Couldn't update property "+key+": "+ex.getMessage());
            }
        }
        
        base.setModifiedDate(new Date());
        
        session.update(base);
        
        return base;
    }
    
    /**
     * {@inheritDoc }
     */
    @Transactional
    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Session session = sessionFactory.getCurrentSession();

        if(!exists(id)){
            throw new EntityNotFoundException("Entity with id " + id + " cannot be deleted because it doesn't exists");
        }
        
        Type read = read(id);
        read.setEnabled(false);
        session.update(read);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Type> list() {
        return list(0, 20, null, Order.NONE);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Type> list(int page, int size, String sortProperty, Order order) {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria crit = session.createCriteria(classType.getName()).add(Restrictions.eq("enabled", true));

        
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
        if(page > 1){
            int offset = (page-1) * size;
            crit.setFirstResult(offset);
        }
        
        results = crit.list();
        
        for(Type t : results){
            postLoad(t);
        }
        
        return results;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Boolean exists(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String name = classType.getName();
        String queryStr = "SELECT 1 FROM "+name+" WHERE id = :id";
        Query query = session.createQuery(queryStr);
        query.setLong("id", id );
        return (query.uniqueResult() != null);    
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Integer count() {
        Session session = sessionFactory.getCurrentSession();
        String name = classType.getName();
        
        Number uniqueResult = (Number) session.createCriteria(name).setProjection(Projections.rowCount()).uniqueResult();
        int intValue = uniqueResult.intValue();
        
        return intValue; 
    }

    
}
