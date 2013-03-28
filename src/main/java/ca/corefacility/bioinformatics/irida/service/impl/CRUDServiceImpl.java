package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
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
public class CRUDServiceImpl<KeyType, Type> implements CRUDService<KeyType, Type> {

    protected CRUDRepository<KeyType, Type> repository;
    protected Validator validator;

    public CRUDServiceImpl(CRUDRepository<KeyType, Type> repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Type create(Type object) throws ConstraintViolationException, IllegalArgumentException {
        Set<ConstraintViolation<Type>> constraintViolations = validator.validate(object);
        if (constraintViolations.isEmpty()) {
            return repository.create(object);
        }

        // this is simplified in bean validation spec 1.1
        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
    }

    @Override
    public Type read(KeyType id) throws IllegalArgumentException {
        return repository.read(id);
    }

    @Override
    public Type update(Type object) throws ConstraintViolationException, IllegalArgumentException {
        Set<ConstraintViolation<Type>> constraintViolations = validator.validate(object);
        if (constraintViolations.isEmpty()) {
            return repository.update(object);
        }

        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
    }

    @Override
    public void delete(KeyType id) throws IllegalArgumentException {
        repository.delete(id);
    }

    @Override
    public List<Type> list() {
        return repository.list();
    }

    @Override
    public Boolean exists(KeyType id) {
        return repository.exists(id);
    }
}
