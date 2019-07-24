package ca.corefacility.bioinformatics.irida.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;

/**
 * A user object.
 *
 */
@Entity
@Table(name = "user", uniqueConstraints = {
		@UniqueConstraint(name = User.USER_EMAIL_CONSTRAINT_NAME, columnNames = "email"),
		@UniqueConstraint(name = User.USER_USERNAME_CONSTRAINT_NAME, columnNames = "username") })
@Audited
@EntityListeners(AuditingEntityListener.class)
public class User extends IridaResourceSupport implements MutableIridaThing, Comparable<User>, UserDetails {

	private static final long serialVersionUID = -7516211470008791995L;

	public static final String USER_EMAIL_CONSTRAINT_NAME = "user_email_constraint";
	public static final String USER_USERNAME_CONSTRAINT_NAME = "user_username_constraint";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{user.username.notnull}")
	@Size(min = 3, message = "{user.username.size}")
	private String username;

	@NotNull(message = "{user.email.notnull}")
	@Size(min = 5, message = "{user.email.size}")
	@Email(message = "{user.email.invalid}")
	private String email;

	@NotNull(message = "{user.password.notnull}")
	// passwords must be at least 8 characters long, but prohibit passwords
	// longer than 1024 (who's going to remember a password that long anyway?)
	// to prevent DOS attacks on our password hashing.
	@Size(min = 8, max = 1024, message = "{user.password.size}")
	@Pattern.List({ @Pattern(regexp = "^.*[A-Z].*$", message = "{user.password.uppercase}"),
			@Pattern(regexp = "^.*[0-9].*$", message = "{user.password.number}"),
			@Pattern(regexp = "^.*[a-z].*$", message = "{user.password.lowercase}"),
			@Pattern(regexp = "^.*[!@#$%^&*()+?/<>=.\\\\{}].*$", message = "{user.password.special}") })
	private String password;

	@NotNull(message = "{user.firstName.notnull}")
	@Size(min = 2, message = "{user.firstName.size}")
	private String firstName;

	@NotNull(message = "{user.lastName.notnull}")
	@Size(min = 2, message = "{user.lastName.size}")
	private String lastName;

	@NotNull(message = "{user.phoneNumber.notnull}")
	@Size(min = 4, message = "{user.phoneNumber.size}")
	private String phoneNumber;

	@NotNull
	private boolean enabled = true;

	@NotNull(message = "{user.systemRole.notnull}")
	@Enumerated(EnumType.STRING)
	@Column(name = "system_role")
	private Role systemRole;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@NotAudited
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date lastLogin;

	private String locale;

	private boolean credentialsNonExpired;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
	private List<ProjectUserJoin> projects;

	@OneToMany(mappedBy = "user")
	private Collection<RemoteAPIToken> tokens;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
	private List<AnnouncementUserJoin> announcements;

	/**
	 * Construct an instance of {@link User} with no properties set.
	 */
	public User() {
		createdDate = new Date();
		locale = "en";
		credentialsNonExpired = true;
		this.systemRole = Role.ROLE_USER;
	}

	/**
	 * Construct an instance of {@link User} with all properties (except
	 * identifier) set.
	 *
	 * @param username
	 *            the username for this {@link User}.
	 * @param email
	 *            the e-mail for this {@link User}.
	 * @param password
	 *            the password for this {@link User}.
	 * @param firstName
	 *            the first name of this {@link User}.
	 * @param lastName
	 *            the last name of this {@link User}.
	 * @param phoneNumber
	 *            the phone number of this {@link User}.
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
	 * @param id
	 *            the identifier for this {@link User}.
	 * @param username
	 *            the username for this {@link User}.
	 * @param email
	 *            the e-mail for this {@link User}.
	 * @param password
	 *            the password for this {@link User}.
	 * @param firstName
	 *            the first name of this {@link User}.
	 * @param lastName
	 *            the last name of this {@link User}.
	 * @param phoneNumber
	 *            the phone number of this {@link User}.
	 */
	public User(Long id, String username, String email, String password, String firstName, String lastName,
			String phoneNumber) {
		this(username, email, password, firstName, lastName, phoneNumber);
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(username, email, password, firstName, lastName, phoneNumber, createdDate, modifiedDate,
				credentialsNonExpired);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			User u = (User) other;
			return Objects.equals(username, u.username) && Objects.equals(email, u.email)
					&& Objects.equals(password, u.password) && Objects.equals(firstName, u.firstName)
					&& Objects.equals(lastName, u.lastName) && Objects.equals(phoneNumber, u.phoneNumber)
					&& Objects.equals(createdDate, u.createdDate) && Objects.equals(modifiedDate, u.modifiedDate)
					&& Objects.equals(credentialsNonExpired, u.credentialsNonExpired);
		}

		return false;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(User u) {
		return modifiedDate.compareTo(u.modifiedDate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(User.class).add("username", username).add("email", email)
				.add("firstName", firstName).add("lastName", lastName).add("phoneNumber", phoneNumber).toString();
	}

	@Override
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

	@JsonIgnore
	@Override
	public String getPassword() {
		return password;
	}

	/*
	 * JsonProperty must be here to enable user to set password via REST API
	 */
	@JsonProperty
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
	public String getLabel() {
		return firstName + " " + lastName;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<Role> roles = new ArrayList<>();
		roles.add(systemRole);
		return roles;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean valid) {
		this.enabled = valid;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Role getSystemRole() {
		return systemRole;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setSystemRole(Role systemRole) {
		this.systemRole = systemRole;
	}

	@JsonIgnore
	public Date getLastLogin() {
		return lastLogin;
	}
}
