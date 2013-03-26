package ca.corefacility.bioinformatics.irida.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Role implements Comparable<Role>, GrantedAuthority {

    private String id;
    private String name;
    private String description;

    public Role() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Role o) {
        return id.compareTo(o.id);
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
