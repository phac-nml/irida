package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.validators.Patterns;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;
import org.openrdf.annotations.Iri;

/**
 * A user object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Iri(User.PREFIX + User.TYPE)
public class User implements IridaThing<Audit,UserIdentifier>, Comparable<User> {
    public static final String PREFIX = "http://xmlns.com/foaf/0.1/";
    public static final String TYPE = "Person";
    
    private UserIdentifier id;
    @NotNull(message = "{user.username.notnull}")
    @Size(min = 3, message = "{user.username.size}")
    @Iri(PREFIX + "nick")
    private String username;
    @NotNull(message = "{user.email.notnull}")
    @Size(min = 5, message = "{user.email.size}")
    @Email(message = "{user.email.invalid}")
    @Iri(PREFIX + "mbox")
    private String email;
    @NotNull(message = "{user.password.notnull}")
    @Size(min = 6, message = "{user.password.size}") // passwords must be at least six characters long
    @Patterns({
            @Pattern(regexp = "^.*[A-Z].*$",
                    message = "{user.password.uppercase}"), // passwords must contain an upper-case letter
            @Pattern(regexp = "^.*[0-9].*$", message = "{user.password.number}") // passwords must contain a number
    })
    @Iri(PREFIX + "password")
    private String password;
    @NotNull(message = "{user.firstName.notnull}")
    @Size(min = 2, message = "{user.firstName.size}")
    @Iri(PREFIX + "firstName")
    private String firstName;
    @NotNull(message = "{user.lastName.notnull}")
    @Size(min = 2, message = "{user.lastName.size}")
    @Iri(PREFIX + "lastName")
    private String lastName;
    @NotNull(message = "{user.phoneNumber.notnull}")
    @Size(min = 4, message = "{user.phoneNumber.size}")
    @Iri(PREFIX + "phone")
    private String phoneNumber;
    @NotNull
    private Audit audit;

    /**
     * Construct an instance of {@link User} with no properties set.
     */
    public User() {
        audit = new Audit();
    }

    /**
     * Construct an instance of {@link User} with all properties (except {@link UserIdentifier}) set.
     *
     * @param username    the username for this {@link User}.
     * @param email       the e-mail for this {@link User}.
     * @param password    the password for this {@link User}.
     * @param firstName   the first name of this {@link User}.
     * @param lastName    the last name of this {@link User}.
     * @param phoneNumber the phone number of this {@link User}.
     */
    public User(String username, String email, String password, String firstName, String lastName, String phoneNumber) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Construct an instance of {@link User} with all properties set.
     *
     * @param id          the {@link UserIdentifier} for this {@link User}.
     * @param username    the username for this {@link User}.
     * @param email       the e-mail for this {@link User}.
     * @param password    the password for this {@link User}.
     * @param firstName   the first name of this {@link User}.
     * @param lastName    the last name of this {@link User}.
     * @param phoneNumber the phone number of this {@link User}.
     */
    public User(UserIdentifier id, String username, String email, String password, String firstName, String lastName, String phoneNumber) {
        this(username, email, password, firstName, lastName, phoneNumber);
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, firstName, lastName, phoneNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User u = (User) other;
            return Objects.equals(id, u.id)
                    && Objects.equals(username, u.username)
                    && Objects.equals(email, u.email)
                    && Objects.equals(password, u.password)
                    && Objects.equals(firstName, u.firstName)
                    && Objects.equals(lastName, u.lastName)
                    && Objects.equals(phoneNumber, u.phoneNumber);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(User u) {
        return audit.compareTo(u.audit);
    }

    /**
     * {@inheritDoc}
     */
    //@Override
    public String stringValue() {
        return com.google.common.base.Objects.toStringHelper(User.class)
                .add("username", username)
                .add("email", email)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("phoneNumber", phoneNumber)
                .toString();
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

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public UserIdentifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(UserIdentifier identifier) {
        this.id = identifier;
    }

    @Override
    public String getLabel() {
        return firstName + " " + lastName;
    }
}
