package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.User;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserMemoryRepository extends CRUDMemoryRepository<User> {

    public UserMemoryRepository() {
        super(User.class);
        Identifier id = new Identifier();
        store.put(id, new User(id, "jsadam", "j@me.com", "pass1234", "Jake", "Penner", "787-9998"));
        id = new Identifier();
        store.put(id, new User(id, "hjadam", "h@me.com", "pass5678", "Hammy", "Penner", "787-1234"));
        id = new Identifier();
        store.put(id, new User(id, "njadam", "n@me.com", "1234pass", "Ninja", "Penner", "787-5678"));
    }
}
