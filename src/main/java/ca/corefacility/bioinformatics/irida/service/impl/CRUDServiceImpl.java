package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * A universal CRUD service for all types. Specialized services should extend
 * this class to get basic CRUD methods for free.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDServiceImpl<KeyType, ValueType extends Comparable<ValueType>> implements CRUDService<KeyType, ValueType> {

    protected CRUDRepository<KeyType, ValueType> repository;
    protected Validator validator;
    protected Class<ValueType> valueType;

    public CRUDServiceImpl(CRUDRepository<KeyType, ValueType> repository, Validator validator, Class<ValueType> valueType) {
        this.repository = repository;
        this.validator = validator;
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public ValueType read(KeyType id) throws EntityNotFoundException {
        return repository.read(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueType update(ValueType object) throws ConstraintViolationException, EntityExistsException {
        Set<ConstraintViolation<ValueType>> constraintViolations = validator.validate(object);
        if (constraintViolations.isEmpty()) {
            return repository.update(object);
        }

        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public List<ValueType> list() {
        return repository.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean exists(KeyType id) {
        return repository.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValueType> list(int page, int size, final String sortProperty, final Order order) throws IllegalArgumentException {
        if (!methodAvailable(sortProperty)) {
            throw new IllegalArgumentException("No method available for property [" + sortProperty + "]");
        }

        List<ValueType> values = repository.list(page, size, sortProperty, order);

        Collections.sort(values, new Comparator<ValueType>() {
            @Override
            public int compare(ValueType o1, ValueType o2) throws IllegalArgumentException {
                if (order.equals(Order.ASCENDING)) {
                    return getValue(o1, sortProperty).compareTo(getValue(o2, sortProperty));
                } else {
                    return getValue(o2, sortProperty).compareTo(getValue(o1, sortProperty));
                }
            }
        });

        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer count() {
        return repository.count();
    }

    /**
     * Get the value of property from the instance of
     * <code>ValueType</code>.
     *
     * @param object the instance to get the property value from.
     * @param property the property name to get the value of.
     * @return the value of the property on the instance.
     * @throws IllegalArgumentException if no public method exists on the object
     * corresponding to the property.
     */
    private <PropertyType extends Comparable> PropertyType getValue(ValueType object, String property) throws IllegalArgumentException {
        String methodName = getMethodName(property);
        try {
            Method m = valueType.getDeclaredMethod(methodName);
            return (PropertyType) m.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("No method exists on type [" + valueType + "] for property [" + property + "] (is the method public?)");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property [" + property + "] is not comparable.");
        }
    }

    /**
     * Tests whether or not the supplied property actually applies to the type
     * of object that we're persisting with this class.
     *
     * @param property the name of the property to test
     * @return true if a getter exists for the property, false otherwise
     */
    private boolean methodAvailable(String property) {
        String methodName = getMethodName(property);
        try {
            Method m = valueType.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the standard getter name for the specified property.
     *
     * @param property the name of the property.
     * @return the standard getter name for the property.
     */
    private String getMethodName(String property) {
        StringBuilder builder = new StringBuilder("get");
        builder.append(property.substring(0, 1).toUpperCase());
        builder.append(property.substring(1));
        return builder.toString();
    }

    @Override
    public List<ValueType> list(int page, int size, Order order) {
        List<ValueType> values = repository.list(page, size, null, order);

        Collections.sort(values);
        
        if (order == Order.DESCENDING) {
            Collections.reverse(values);
        }

        return values;
    }
}
