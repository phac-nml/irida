package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.validators.Patterns;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private Identifier id;
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
    @NotNull
    private Date created;

    public User() {
        projects = new HashMap<>();
        created = new Date();
    }

    public User(String username, String email, String password, String firstName, String lastName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.created = new Date();
    }

    public User(Identifier id, String username, String email, String password, String firstName, String lastName, String phoneNumber) {
        this(username, email, password, firstName, lastName, phoneNumber);
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, firstName, lastName, phoneNumber, created);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User u = (User) other;
            return Objects.equals(id, u.id)
                    && Objects.equals(username, u.username)
                    && Objects.equals(email, u.email)
                    && Objects.equals(password, u.password)
                    && Objects.equals(firstName, u.firstName)
                    && Objects.equals(phoneNumber, u.phoneNumber)
                    && Objects.equals(created, u.created);
        }

        return false;
    }

    @Override
    public int compareTo(User u) {
        return created.compareTo(u.created);
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
}
