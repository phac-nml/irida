package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import java.util.List;

/**
 * A universal CRUD service for all types. Specialized services should extend
 * this class to get basic CRUD methods for free.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CRUDServiceImpl<KeyType, Type> implements CRUDService<KeyType, Type> {

    protected CRUDRepository<KeyType, Type> repository;

    public CRUDServiceImpl(CRUDRepository<KeyType, Type> repository) {
        this.repository = repository;
    }

    @Override
    public Type create(Type object) throws IllegalArgumentException {
        //TODO: validate users before creating
        return repository.create(object);
    }

    @Override
    public Type read(KeyType id) throws IllegalArgumentException {
        return repository.read(id);
    }

    @Override
    public Type update(Type object) throws IllegalArgumentException {
        //TODO: validate user before updating
        return repository.update(object);
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
