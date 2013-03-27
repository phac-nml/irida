package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.validators.Patterns;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

/**
 * A user object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class User implements Comparable<User> {

    private UUID id;
    private URI uri;
    @NotNull
    @Size(min = 3)
    private String username;
    @Email
    @NotNull
    private String email;
    @NotNull
    @Size(min = 6) // passwords must be at least six characters long
    @Patterns({
        @Pattern(regexp = "^.*[A-Z].*$"), // passwords must contain an upper-case letter
        @Pattern(regexp = "^.*[0-9].*$") // passwords must contain a number
    })
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;
    private Map<Project, Role> projects;

    public User() {
        projects = new HashMap<>();
    }

    public User(UUID id, String username, String email, String password, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
        hash = 59 * hash + Objects.hashCode(this.email);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + Objects.hashCode(this.firstName);
        hash = 59 * hash + Objects.hashCode(this.lastName);
        hash = 59 * hash + Objects.hashCode(this.phoneNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void addProject(Project project, Role role) {
        this.projects.put(project, role);
    }

    public void removeProject(Project project) {
        this.projects.remove(project);
    }

    public Map<Project, Role> getProjects() {
        return this.projects;
    }

    public void setProjects(Map<Project, Role> projects) {
        this.projects = projects;
    }

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public int compareTo(User o) {
        return id.compareTo(o.id);
    }
}
