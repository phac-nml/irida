/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 * @author Franklin Bristow
 */
public class UserMemoryRepository implements CRUDRepository<Long, User> {

    private static Long id = 1l;
    private static final Map<Long, User> store = new HashMap<>();

    @Override
    public User create(User u) throws IllegalArgumentException {
        u.setId(id);
        store.put(id, u);
        id++;
        return u;
    }

    @Override
    public User read(Long id) throws IllegalArgumentException {
        if (store.containsKey(id)) {
            return store.get(id);
        }
        throw new IllegalArgumentException("No such user exists with id [" + id + ".");
    }

    @Override
    public User update(User u) throws IllegalArgumentException {
        if (store.containsKey(u.getId())) {
            return store.put(id, u);
        }
        throw new IllegalArgumentException("No such user exists with id [" + id + ".");
    }

    @Override
    public void delete(Long id) throws IllegalArgumentException {
        if (!store.containsKey(id)) {
            throw new IllegalArgumentException("No such user exists with id [" + id + ".");
        }
        store.remove(id);
    }

    @Override
    public List<User> list() {
        List<User> users = new ArrayList<>(store.values());
        Collections.sort(users);
        return users;
    }

    @Override
    public Boolean exists(Long id) {
        return store.containsKey(id);
    }
}
