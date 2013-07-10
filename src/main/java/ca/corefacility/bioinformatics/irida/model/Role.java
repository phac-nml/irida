package ca.corefacility.bioinformatics.irida.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/**
 * Roles for authorization in the application.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="system_role")
public class Role implements Comparable<Role>, GrantedAuthority {
    
    @Id
    @Column(name="id")
    Long id;
    
    @NotNull
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
    
    /**
     * Return a {@link Role} for the given string value
     * @param value The string value to create a {@link Role} for
     * @return A new {@link Role} instance for the given string value
     */
    public static Role valueOf(String value){
        return new Role(value);
    }

}
