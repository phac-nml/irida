package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * Join class between {@link User} and {@link Group}.
 * 
 *
 */
@Entity
@Table(name = "user_group", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "logicalGroup_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class UserGroupJoin implements Join<User, Group> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	private User user;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "logicalGroup_id", nullable = false)
	@NotNull
	private Group logicalGroup;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	protected UserGroupJoin() {
		this.createdDate = new Date();
	}

	public UserGroupJoin(User u, Group g) {
		this();
		this.user = u;
		this.logicalGroup = g;
	}

	public int hashCode() {
		return Objects.hash(user, logicalGroup);
	}

	public boolean equals(Object o) {
		if (o instanceof UserGroupJoin) {
			UserGroupJoin ug = (UserGroupJoin) o;
			return Objects.equals(user, ug.user) && Objects.equals(logicalGroup, ug.logicalGroup);
		}

		return false;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public User getSubject() {
		return user;
	}

	@Override
	public void setSubject(User subject) {
		this.user = subject;
	}

	@Override
	public Group getObject() {
		return logicalGroup;
	}

	@Override
	public void setObject(Group object) {
		this.logicalGroup = object;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}
}
