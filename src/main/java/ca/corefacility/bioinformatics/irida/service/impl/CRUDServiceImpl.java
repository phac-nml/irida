package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.beanutils.BeanUtils;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

import java.lang.reflect.InvocationTargetException;

/**
 * A universal CRUD service for all types. Specialized services should extend
 * this class to get basic CRUD methods for free.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDServiceImpl<KeyType extends Serializable, ValueType extends Comparable<ValueType>> implements
		CRUDService<KeyType, ValueType> {
	private static final String NO_SUCH_ID_EXCEPTION = "No such identifier exists in the database.";

	private static final String CREATED_DATE_SORT_PROPERTY = "createdDate";

	protected final PagingAndSortingRepository<ValueType, KeyType> repository;
	protected final Validator validator;
	protected final Class<ValueType> valueType;

	public CRUDServiceImpl(PagingAndSortingRepository<ValueType, KeyType> repository, Validator validator,
			Class<ValueType> valueType) {
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
			return repository.save(object);
		}

		throw new ConstraintViolationException(constraintViolations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ValueType read(KeyType id) throws EntityNotFoundException {
		ValueType value = repository.findOne(id);
		if (value == null) {
			throw new EntityNotFoundException(NO_SUCH_ID_EXCEPTION);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(KeyType id) throws EntityNotFoundException {
		if (!exists(id)) {
			throw new EntityNotFoundException(NO_SUCH_ID_EXCEPTION);
		}

		repository.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Iterable<ValueType> findAll() {
		return repository.findAll();
		// return repository.list();
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
	public Page<ValueType> list(int page, int size, final Direction order, final String... sortProperties)
			throws IllegalArgumentException {
		return repository.findAll(new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public long count() {
		return repository.count();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ValueType update(KeyType id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		// check if you can actually update the properties requested
		ValueType instance = read(id);

		for (String key : updatedFields.keySet()) {
			Object value = updatedFields.get(key);

			try {
				// setProperty doesn't throw an exception if the field name
				// can't be found, so we have to force an exception to be thrown
				// by calling getProperty manually first.
				BeanUtils.getProperty(instance, key);
				BeanUtils.setProperty(instance, key, value);
			} catch (IllegalAccessException | InvocationTargetException | java.lang.IllegalArgumentException
					| NoSuchMethodException e) {
				throw new InvalidPropertyException("Unable to access field [" + key + "]");
			}
		}

		// now that you know all of the requested methods exist, validate the
		// supplied values
		Set<ConstraintViolation<ValueType>> constraintViolations = new HashSet<>();

		// try to validate all of the properties that are attempted to be
		// updated.
		for (String propertyName : updatedFields.keySet()) {
			Set<ConstraintViolation<ValueType>> propertyViolations = validator.validateValue(valueType, propertyName,
					updatedFields.get(propertyName));
			constraintViolations.addAll(propertyViolations);
		}

		// if any validations fail, throw a constraint violation exception.
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}

		// check if the entity exists in the database
		if (!exists(id)) {
			throw new EntityNotFoundException("Entity not found.");
		}

		// at this point, everything is A-OK, so go through the act of updating
		// the entity:
		// return repository.update(id, updatedFields);
		return repository.save(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ValueType> list(int page, int size, Direction order) {
		return repository.findAll(new PageRequest(page, size, order, CREATED_DATE_SORT_PROPERTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Iterable<ValueType> readMultiple(Iterable<KeyType> idents) {
		return repository.findAll(idents);
	}
}
