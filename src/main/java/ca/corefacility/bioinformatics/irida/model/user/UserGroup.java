package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * Join class between {@link User} and {@link Group}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "user_group", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "logicalGroup_id" }))
@Audited
public class UserGroup implements Join<User, Group> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "logicalGroup_id")
	private Group logicalGroup;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public int hashCode() {
		return Objects.hash(user, logicalGroup);
	}

	public boolean equals(Object o) {
		if (o instanceof UserGroup) {
			UserGroup ug = (UserGroup) o;
			return Objects.equals(user, ug.user) && Objects.equals(logicalGroup, ug.logicalGroup);
		}

		return false;
	}

	@Override
	public Long getId() {
		return this.id;
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
		return createdDate;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}

}
