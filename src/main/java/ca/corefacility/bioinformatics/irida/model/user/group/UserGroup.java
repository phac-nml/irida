package ca.corefacility.bioinformatics.irida.model.user.group;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.event.UserGroupRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A collection of {@link User} accounts is a {@link UserGroup}. The
 * {@link UserGroup} can be assigned permissions, similar to a {@link User}
 * account, but applies the permissions to all members of the {@link UserGroup}.
 */
@Entity
@Table(name = "user_group")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class UserGroup implements MutableIridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final Long id;

	@NotNull(message = "{group.name.notnull}")
	@Size(min = 3, message = "{group.name.size}")
	@Column(unique = true)
	private String name;

	@CreatedDate
	@Column(name = "created_date")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@Column(name = "modified_date")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "group")
	private Set<UserGroupJoin> users;

	@NotAudited
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "userGroup")
	private List<UserGroupRoleSetProjectEvent> events;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "userGroup")
	private Set<UserGroupProjectJoin> userGroupProjects;
	
	@Column(name = "description")
	@Lob
	private String description;

	/**
	 * Create a new {@link UserGroup}.
	 */
	private UserGroup() {
		this.id = null;
		this.createdDate = new Date();
	}
	
	public UserGroup(final String name) {
		this();
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, createdDate, modifiedDate);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof UserGroup) {
			final UserGroup u = (UserGroup) o;
			return Objects.equals(u.name, this.name) && Objects.equals(u.createdDate, this.createdDate)
					&& Objects.equals(u.modifiedDate, this.modifiedDate);
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(Long id) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
}
