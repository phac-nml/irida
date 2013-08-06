package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.PropertyAccessException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * A universal CRUD service for all types. Specialized services should extend this class to get basic CRUD methods for
 * free.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDServiceImpl<KeyType, ValueType extends Comparable<ValueType> >
        implements CRUDService<KeyType, ValueType> {
    protected final CRUDRepository<KeyType, ValueType> repository;
    protected final Validator validator;
    protected final Class<ValueType> valueType;

    public CRUDServiceImpl(CRUDRepository<KeyType, ValueType> repository, Validator validator, Class<ValueType> valueType) {
        this.repository = repository;
        this.validator = validator;
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ValueType create(ValueType object) throws ConstraintViolationException, EntityExistsException {

        Set<ConstraintViolation<ValueType>> constraintViolations = validator.validate(object);
        if (constraintViolations.isEmpty()) {
            return repository.create(object);
        }

        // this is simplified in bean validation spec 1.1
        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ValueType read(KeyType id) throws EntityNotFoundException {
        return repository.read(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(KeyType id) throws EntityNotFoundException {
        if (!exists(id)) {
            throw new EntityNotFoundException("No such identifier exists in the database.");
        }

        repository.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ValueType> list() {
        return repository.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean exists(KeyType id) {
        return repository.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ValueType> list(int page, int size, final String sortProperty, final Order order)
        throws IllegalArgumentException {

        try{
            valueType.getDeclaredField(sortProperty);
        }
        catch (NoSuchFieldException | SecurityException ex) {
            throw new IllegalArgumentException("Unable to access field [" + sortProperty + "]");
        }


        List<ValueType> values = repository.list(page, size, sortProperty, order);

        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Integer count() {
        return repository.count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ValueType update(KeyType id, Map<String, Object> updatedFields)
            throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {
        // check if you can actually update the properties requested

        for(String key : updatedFields.keySet()){
            Object value = updatedFields.get(key);
            ValueType newInstance;
            
            try{
                newInstance = valueType.newInstance();
            }
            catch(IllegalAccessException| InstantiationException ex){
                throw new IllegalArgumentException("Unable to instantiate object of type " + valueType.getName());
            }
            
            DirectFieldAccessor acc = new DirectFieldAccessor(newInstance);
            
            try{
                acc.setPropertyValue(key, value);
            }
            catch (InvalidPropertyException| PropertyAccessException | TypeMismatchException | NotWritablePropertyException ex) {
                throw new InvalidPropertyException("Unable to access field [" + key + "]");
            }


        }

            
        // now that you know all of the requested methods exist, validate the supplied values
        Set<ConstraintViolation<ValueType>> constraintViolations = new HashSet<>();

        // try to validate all of the properties that are attempted to be updated.
        for (String propertyName : updatedFields.keySet()) {
            Set<ConstraintViolation<ValueType>> propertyViolations = validator.validateValue(valueType, propertyName,
                    updatedFields.get(propertyName));
            constraintViolations.addAll(propertyViolations);
        }

        // if any validations fail, throw a constraint violation exception.
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }

        // check if the entity exists in the database
        if (!exists(id)) {
            throw new EntityNotFoundException("Entity not found.");
        }

        // at this point, everything is A-OK, so go through the act of updating the entity:
        return repository.update(id, updatedFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ValueType> list(int page, int size, Order order) {
        List<ValueType> values = repository.list(page, size, null, order);

        Collections.sort(values);

        if (order == Order.DESCENDING) {
            Collections.reverse(values);
        }

        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ValueType> readMultiple(Collection<KeyType> idents) {
        return repository.readMultiple(idents);
    }

}
