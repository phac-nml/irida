package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Project implements Comparable<Project> {

    private UUID id;
    private URI uri;
    @NotNull
    private String name;
    @NotEmpty // projects must have at least 1 user (a manager)
    private Map<User, Role> users;

    public Project() {
        users = new HashMap<>();
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsersByRole(Role role) {
        Set<User> filtered = new HashSet<>(users.keySet().size());
        for (Entry<User, Role> entry : users.entrySet()) {
            if (entry.getValue().equals(role)) {
                filtered.add(entry.getKey());
            }
        }
        return filtered;
    }

    public void addUserToProject(User u, Role r) {
        users.put(u, r);
    }

    public Map<User, Role> getUsers() {
        return users;
    }

    public void setUsers(Map<User, Role> members) {
        this.users = members;
    }

    @Override
    public int compareTo(Project o) {
        return id.compareTo(o.id);
    }
}
