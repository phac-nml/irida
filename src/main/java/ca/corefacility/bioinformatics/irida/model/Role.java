package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Role implements Comparable<Role>, GrantedAuthority, Auditable<Audit>, Identifiable<Identifier> {

    private Identifier id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Audit audit;

    public Role() {
        audit = new Audit();
    }

    public Role(Identifier id) {
        this();
        this.id = id;
    }

    public Role(String name) {
        this();
        this.name = name;
    }

    @Override
    public int compareTo(Role r) {
        return audit.compareTo(r.audit);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Role) {
            Role r = (Role) other;
            return Objects.equals(id, r.id)
                    && Objects.equals(name, r.name)
                    && Objects.equals(description, r.description);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.id = identifier;
    }
}
