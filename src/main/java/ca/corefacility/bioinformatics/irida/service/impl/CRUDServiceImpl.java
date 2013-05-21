package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * A universal CRUD service for all types. Specialized services should extend this class to get basic CRUD methods for
 * free.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDServiceImpl<KeyType extends Identifier, ValueType extends Comparable<ValueType> & Auditable<Audit>>
        implements CRUDService<KeyType, ValueType> {

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
        // the audit information must be initialized by the crud service:
        Audit audit = new Audit();
        object.setAuditInformation(audit);

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
    public List<ValueType> list(int page, int size, final String sortProperty, final Order order)
            throws IllegalArgumentException {
        if (!methodAvailable(sortProperty, MethodType.GETTER)) {
            throw new IllegalArgumentException("No method available for property [" + sortProperty + "]");
        }

        List<ValueType> values = repository.list(page, size, sortProperty, order);
        Collections.sort(values, new Comparator<ValueType>() {
            @Override
            public int compare(ValueType o1, ValueType o2) throws IllegalArgumentException {
                return getValue(o1, sortProperty).compareTo(getValue(o2, sortProperty));
            }
        });

        if (order.equals(Order.DESCENDING)) {
            Collections.reverse(values);
        }

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
     * {@inheritDoc}
     */
    @Override
    public ValueType update(KeyType id, Map<String, Object> updatedFields)
            throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {
        // check if you can actually call all of the methods requested
        for (Entry<String, Object> field : updatedFields.entrySet()) {
            if (!methodAvailable(field.getKey(), MethodType.SETTER, field.getValue())) {
                throw new InvalidPropertyException("Cannot set property [" + field.getKey() + "].");
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

        // 1) load the entity from the database
        ValueType entity = repository.read(id);

        // 2) update the appropriate fields
        for (Entry<String, Object> field : updatedFields.entrySet()) {
            setValue(entity, field.getKey(), field.getValue());
        }

        // 3) set the date that the entity was updated on
        entity.getAuditInformation().setUpdated(new Date());

        // check that it doesn't violate any constraints
        Set<ConstraintViolation<ValueType>> constraintViolations = validator.validate(entity);
        if (constraintViolations.isEmpty()) {
            //return repository.update(entity);
            return repository.update(id, updatedFields);
            //return entity;
        }

        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
    }

    /**
     * Get the value of property from the instance of <code>ValueType</code>.
     *
     * @param object   the instance to get the property value from.
     * @param property the property name to get the value of.
     * @return the value of the property on the instance.
     * @throws IllegalArgumentException if no public method exists on the object corresponding to the property.
     */
    private <PropertyType extends Comparable> PropertyType getValue(ValueType object, String property)
            throws IllegalArgumentException {
        try {
            Method m = getMethod(property, MethodType.GETTER);
            return (PropertyType) m.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "No method exists on type [" + valueType + "] for property [" + property + "] (is the method public?)");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property [" + property + "] is not comparable.");
        }
    }

    /**
     * Set the value of a property on the specific instance.
     *
     * @param object   the instance to change the value of.
     * @param property the property name to change.
     * @param value    the new value of the property.
     * @throws IllegalArgumentException if no appropriate setter can be found.
     */
    private void setValue(ValueType object, String property, Object value) throws IllegalArgumentException {
        try {
            Method m = getMethod(property, MethodType.SETTER, value);
            m.invoke(object, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "No method exists on type [" + valueType + "] for property [" + property + "] (is the method public?)");
        }
    }

    /**
     * Tests whether or not the supplied property actually applies to the type of object that we're persisting with this
     * class.
     *
     * @param property the name of the property to test
     * @param type     the type of method that you want to test for.
     * @param params   the list of actual values that you want to test for
     * @return true if a getter exists for the property, false otherwise
     */
    private boolean methodAvailable(String property, MethodType type, Object... params) {
        try {
            getMethod(property, type, params);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Gets the method that we can invoke to get or set a property.
     *
     * @param property the name of the property to test
     * @param type     the type of method that you want to test for.
     * @param params   the list of actual values that you want to test for
     * @return the method that we're looking for
     * @throws NoSuchMethodException if no such method exists.
     */
    private Method getMethod(String property, MethodType type, Object... params) throws NoSuchMethodException {
        String methodName = getMethodName(property, type);

        if (MethodType.GETTER.equals(type)) {
            return valueType.getDeclaredMethod(methodName);
        } else {
            // check to see if the parameter is null
            Object param = params[0];
            if (param != null) {
                // if not, throw it on through
                return valueType.getDeclaredMethod(methodName, param.getClass());
            } else {
                // if the parameter is null, just see if we can find a
                // method with the correct name.
                Method[] methods = valueType.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(methodName)) {
                        return m;
                    }
                }
            }
        }
        throw new NoSuchMethodException();
    }

    /**
     * Get the standard getter name for the specified property.
     *
     * @param property the name of the property.
     * @return the standard getter name for the property.
     */
    private String getMethodName(String property, MethodType type) {
        StringBuilder builder = new StringBuilder(type.toString());
        // append the upper-case first character of the property name
        builder.append(property.substring(0, 1).toUpperCase());
        // then append the remainder of the property name, as passed
        builder.append(property.substring(1));
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValueType> list(int page, int size, Order order) {
        List<ValueType> values = repository.list(page, size, null, order);

        Collections.sort(values);

        if (order == Order.DESCENDING) {
            Collections.reverse(values);
        }

        return values;
    }

    private enum MethodType {

        GETTER("get"),
        SETTER("set");
        String value;

        MethodType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
