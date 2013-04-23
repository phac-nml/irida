package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import java.util.Collection;
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
public class Project implements Comparable<Project>, Auditable<Audit>, Identifiable<Identifier> {

    private Identifier id;
    @NotNull
    private String name;
    @NotEmpty // projects must have at least 1 user (a manager)
    private Map<User, Role> users;
    @NotNull
    private Audit audit;
    private Collection<Sample> samples;

    public Project() {
        users = new HashMap<>();
        samples = new HashSet<>();
        audit = new Audit();
    }

    public Project(Identifier id) {
        this();
        this.id = id;
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Project) {
            Project p = (Project) other;
            return Objects.equals(id, p.id)
                    && Objects.equals(name, p.name)
                    && Objects.equals(samples, p.samples);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
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

    public Collection<Sample> getSamples() {
        return samples;
    }

    public void setSamples(Collection<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public int compareTo(Project p) {
        return audit.getCreated().compareTo(p.audit.getCreated());
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }
}
