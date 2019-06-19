package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Timestamped;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * A universal CRUD service for all types. Specialized services should extend
 * this class to get basic CRUD methods for free.
 *
 * @param <KeyType>   Key in the database for the type stored
 * @param <ValueType> Type of object stored by this service
 */
public class CRUDServiceImpl<KeyType extends Serializable, ValueType extends Timestamped<KeyType>> implements
		CRUDService<KeyType, ValueType> {
	private static final String NO_SUCH_ID_EXCEPTION = "No such identifier exists in the database: ";

	protected static final String CREATED_DATE_SORT_PROPERTY = "createdDate";

	private final static String[] DEFAULT_SORT_PROPERTIES = { CREATED_DATE_SORT_PROPERTY };

	protected final IridaJpaRepository<ValueType, KeyType> repository;
	protected final Validator validator;
	protected final Class<ValueType> valueType;

	public CRUDServiceImpl(IridaJpaRepository<ValueType, KeyType> repository, Validator validator,
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
		if (object.getId() != null) {
			// check if the entity exists in the database
			if (exists(object.getId())) {
				throw new EntityExistsException("Entity with this ID is already in the database.");
			}
		}

		return repository.save(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ValueType read(KeyType id) throws EntityNotFoundException {
		ValueType value = repository.findOne(id);
		if (value == null) {
			throw new EntityNotFoundException(NO_SUCH_ID_EXCEPTION + id);
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
			throw new EntityNotFoundException(NO_SUCH_ID_EXCEPTION + id);
		}

		repository.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	@Override
	public Page<ValueType> list(int page, int size, Sort sort)
			throws IllegalArgumentException {
		return repository.findAll(new PageRequest(page, size, sort));
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
	public ValueType updateFields(KeyType id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		// check if you can actually update the properties requested
		ValueType instance = read(id);

		for (String key : updatedFields.keySet()) {
			Object value = updatedFields.get(key);

			try {
				// setProperty doesn't throw an exception if the field name
				// can't be found, so we have to force an exception to be thrown
				// by calling getProperty manually first.
				DirectFieldAccessor dfa = new DirectFieldAccessor(instance);
				dfa.setPropertyValue(key, value);
			} catch (IllegalArgumentException | NotWritablePropertyException | TypeMismatchException e) {
				throw new InvalidPropertyException("Unable to access field [" + key + "]", valueType, e);
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

		// at this point, everything is A-OK, so go through the act of updating
		// the entity:
		return repository.save(instance);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueType update(ValueType object) {
		KeyType id = object.getId();

		// check if the entity exists in the database
		if (!exists(id)) {
			throw new EntityNotFoundException("Entity not found.");
		}
		
		Set<ConstraintViolation<ValueType>> constraintViolations = validator.validate(object);
		// if any validations fail, throw a constraint violation exception.
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}

		return repository.save(object);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ValueType> updateMultiple(Collection<ValueType> objects) {
		return objects.stream().map(s -> update(s)).collect(Collectors.toList());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@Transactional(readOnly = true)
	public Page<ValueType> search(Specification<ValueType> specification, int page, int size, Direction order,
			String... sortProperties) {
		// if the sort properties are null, empty, or are an empty string, use
		// CREATED_DATE
		if (sortProperties == null || sortProperties.length == 0
				|| (sortProperties.length == 1 && sortProperties[0].equals(""))) {
			sortProperties = DEFAULT_SORT_PROPERTIES;
		}

		return repository.findAll(specification, new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ValueType> search(Specification<ValueType> specification, PageRequest pageRequest) {
		return repository.findAll(specification, pageRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Revisions<Integer, ValueType> findRevisions(KeyType id) throws EntityRevisionDeletedException {
		try {
			return repository.findRevisions(id);
		} catch (InvalidDataAccessApiUsageException e) {
			throw new EntityRevisionDeletedException(String.format("Resource with id [%d] was deleted.", id), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Revision<Integer, ValueType>> findRevisions(KeyType id, Pageable pageable)
			throws EntityRevisionDeletedException {
		try {
			return repository.findRevisions(id, pageable);
		} catch (InvalidDataAccessApiUsageException e) {
			throw new EntityRevisionDeletedException(String.format("Resource with id [%d] was deleted.", id), e);
		}
	}
}
