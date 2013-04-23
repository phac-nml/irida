package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import java.util.Collection;
import java.util.HashSet;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserMemoryRepository extends CRUDMemoryRepository<UserIdentifier, User> implements UserRepository {

    public UserMemoryRepository() {
        super(User.class);
        UserIdentifier id = new UserIdentifier("jsadam");
        User u = new User(id, "jsadam", "j@me.com", "pass1234", "Jake", "Penner", "787-9998");
        Project p = new Project();
        p.setIdentifier(new Identifier());
        p.setName("The super project.");
        u.addProject(p, new Role());
        store.put(id, u);
        
        for (char i = 'a'; i <= 'z'; i++) {
            String username = i + "adam";
            id = new UserIdentifier(username);
            store.put(id, new User(id, username, "h@me.com", "pass5678", "Hammy", "Penner", "787-1234"));
        }
    }

    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        User u = null;

        for (User entry : store.values()) {
            if (entry.getUsername().equals(username)) {
                u = entry;
                break;
            }
        }

        if (u == null) {
            throw new EntityNotFoundException("No user with username [" + username + "] exists.");
        }

        return u;
    }

    @Override
    public Collection<User> getUsersForProject(Project project) {
        return new HashSet<>(project.getUsers().keySet());
    }
    
    @Override
    protected Identifier generateIdentifier(User object) {
        UserIdentifier uid = new UserIdentifier(object.getUsername());
        return uid;
    }
}
