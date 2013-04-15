package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserMemoryRepository extends CRUDMemoryRepository<User> implements UserRepository {

    public UserMemoryRepository() {
        super(User.class);
        Identifier id = new Identifier();
        store.put(id, new User(id, "jsadam", "j@me.com", "pass1234", "Jake", "Penner", "787-9998"));
        id = new Identifier();
        store.put(id, new User(id, "hjadam", "h@me.com", "pass5678", "Hammy", "Penner", "787-1234"));
        id = new Identifier();
        store.put(id, new User(id, "njadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "badam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "cadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "dadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "eadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "fadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "gadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "hadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "iadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "jadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "kadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "ladam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "madam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "nadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "oadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "padam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "jlky", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "jlouh", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "nklfr", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "jkgtffjh", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "kljkhjlkjlk", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "klj;lkkj;l", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "jkhkjh", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
        id = new Identifier();
        store.put(id, new User(id, "kljlkj", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
    }

    @Override
    public User getUserByUsername(String username) {
        User u = null;
        
        for (User entry : store.values()) {
            if (entry.getUsername().equals(username)) {
                u = entry;
                break;
            }
        }
        
        return u;
    }
}
