package ca.corefacility.bioinformatics.irida.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Project implements Comparable<Project> {

    private Identifier id;
    @NotNull
    private String name;
    @NotEmpty // projects must have at least 1 user (a manager)
    private Map<User, Role> users;
    @NotNull
    private Audit audit;

    public Project() {
        users = new HashMap<>();
        audit = new Audit();
    }

    public Project(Identifier id) {
        this();
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Project) {
            Project p = (Project) other;
            return Objects.equals(id, p.id)
                    && Objects.equals(name, p.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Identifier getId() {
        return this.id;
    }

    public void setId(Identifier id) {
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

    public Audit getAudit() {
        return this.audit;
    }

    public void setAudit(Audit auditInformation) {
        this.audit = auditInformation;
    }

    @Override
    public int compareTo(Project p) {
        return audit.getCreated().compareTo(p.audit.getCreated());
    }
}
