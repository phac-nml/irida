package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Role implements Comparable<Role>, GrantedAuthority {

    private UUID id;
    private URI uri;
    @NotNull
    private String name;
    @NotNull
    private String description;

    public Role() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
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
