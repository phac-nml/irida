package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Role implements Comparable<Role>, GrantedAuthority {

    private Identifier id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Date created;

    public Role() {
        created = new Date();
    }

    @Override
    public int compareTo(Role r) {
        return created.compareTo(r.created);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Role) {
            Role r = (Role) other;
            return Objects.equals(id, r.id)
                    && Objects.equals(name, r.name)
                    && Objects.equals(description, r.description)
                    && Objects.equals(created, r.created);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, created);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
}
