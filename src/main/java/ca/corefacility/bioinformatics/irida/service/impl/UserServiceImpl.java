/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import java.util.List;

/**
 * Implementation of the <ref>UserService</ref>.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImpl implements CRUDService<Long, User> {

    private CRUDRepository<Long, User> repository;

    public UserServiceImpl(CRUDRepository<Long, User> repository) {
        this.repository = repository;
    }

    @Override
    public User create(User user) throws IllegalArgumentException {
        //TODO: validate users before creating
        return repository.create(user);
    }

    @Override
    public User read(Long id) throws IllegalArgumentException {
        return repository.read(id);
    }

    @Override
    public User update(User user) throws IllegalArgumentException {
        //TODO: validate user before updating
        return repository.update(user);
    }

    @Override
    public void delete(Long id) throws IllegalArgumentException {
        repository.delete(id);
    }

    @Override
    public List<User> list() {
        return repository.list();
    }

    @Override
    public Boolean exists(Long id) {
        return repository.exists(id);
    }
}
