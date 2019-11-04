package ca.corefacility.bioinformatics.irida.model.user.group;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A relationship between an individual {@link User} account and a
 * {@link UserGroup}. This class closely mirrors the {@link ProjectUserJoin} in
 * that a {@link UserGroup} is assigned a level of access using the
 * {@link ProjectRole} enum.
 * 
 * @see ProjectUserJoin
 * @see ProjectRole
 */
@Entity
@Table(name = "user_group_member")
@Audited
@EntityListeners(AuditingEntityListener.class)
public final class UserGroupJoin implements Join<User, UserGroup> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_id")
	@NotNull
	private final User user;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "group_id")
	@NotNull
	private final UserGroup group;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private UserGroupRole role;

	/**
	 * For hibernate
	 */
	@SuppressWarnings("unused")
	private UserGroupJoin() {
		this.createdDate = null;
		this.id = null;
		this.user = null;
		this.group = null;
		this.role = null;
	}

	/**
	 * Create a new {@link UserGroupJoin}.
	 *
	 * @param user  the {@link User} in the {@link UserGroup}.
	 * @param group the {@link UserGroup} that the {@link User} is a member of.
	 * @param role  The role the user should have on the project
	 */
	public UserGroupJoin(final User user, final UserGroup group, final UserGroupRole role) {
		this.createdDate = new Date();
		this.id = null;
		this.user = user;
		this.group = group;
		this.role = role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, group, createdDate, role);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof UserGroupJoin) {
			final UserGroupJoin u = (UserGroupJoin) o;
			return Objects.equals(u.user, this.user) && Objects.equals(u.group, this.group)
					&& Objects.equals(u.createdDate, this.createdDate) && Objects.equals(u.role, this.role);
		}

		return false;
	}

	/**
	 * The role of a user in the group
	 */
	public enum UserGroupRole {
		GROUP_OWNER, GROUP_MEMBER;

		/**
		 * Get a role from the given string
		 *
		 * @param code string representation of the role
		 * @return the role
		 */
		public static UserGroupRole fromString(String code) {
			switch (code.toUpperCase()) {
			case "GROUP_MEMBER":
				return GROUP_MEMBER;
			case "GROUP_OWNER":
				return GROUP_OWNER;
			default:
				return GROUP_MEMBER;
			}
		}
	}

	@Override
	public String getLabel() {
		return group.getLabel() + " -> " + user.getLabel();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public User getSubject() {
		return this.user;
	}

	@Override
	public UserGroup getObject() {
		return this.group;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

	public UserGroupRole getRole() {
		return this.role;
	}

	public void setRole(final UserGroupRole role) {
		this.role = role;
	}
}