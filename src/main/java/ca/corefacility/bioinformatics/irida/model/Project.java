package ca.corefacility.bioinformatics.irida.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Project implements Comparable<Project> {

    private String id;
    private String name;
    private Map<User, Role> users;

    public Project() {
        users = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
