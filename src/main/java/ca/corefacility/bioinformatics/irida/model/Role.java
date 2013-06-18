package ca.corefacility.bioinformatics.irida.model;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class Role implements Comparable<Role>, GrantedAuthority {

    private String name;
    @NotNull
    private String description;

    public Role() {
    }


    public Role(String name) {
        this();
        this.name = name;
    }

    @Override
    public int compareTo(Role r) {
        return name.compareTo(r.name);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Role) {
            Role r = (Role) other;
            return Objects.equals(name, r.name)
                    && Objects.equals(description, r.description);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
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
