package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.Identifier;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserMemoryRepository implements CRUDRepository<Identifier, User> {

    private static final Map<Identifier, User> store = new HashMap<>();

    static {
        Identifier id = new Identifier();
        store.put(id, new User(id, "jsadam", "j@me.com", "pass1234", "Jake", "Penner", "787-9998"));
        id = new Identifier();
        store.put(id, new User(id, "hjadam", "h@me.com", "pass5678", "Hammy", "Penner", "787-1234"));
        id = new Identifier();
        store.put(id, new User(id, "njadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
    }

    @Override
    public User create(User u) throws IllegalArgumentException {
        Identifier id = new Identifier();
        u.setId(id);
        store.put(id, u);
        return u;
    }

    @Override
    public User read(Identifier id) throws IllegalArgumentException {
        if (store.containsKey(id)) {
            return store.get(id);
        }
        throw new IllegalArgumentException("No such user exists with id [" + id + ".");
    }

    @Override
    public User update(User u) throws IllegalArgumentException {
        Identifier id = u.getId();
        if (store.containsKey(u.getId())) {

            return store.put(id, u);
        }
        throw new IllegalArgumentException("No such user exists with id [" + id + ".");
    }

    @Override
    public void delete(Identifier id) throws IllegalArgumentException {
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
    public Boolean exists(Identifier id) {
        return store.containsKey(id);
    }
}
