package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * Envers Revision class storing the timestamp, user, and oauth client used to make a change.
 */
@Entity
@RevisionEntity(UserRevListener.class)
@Table(name = "Revisions")
public class UserRevEntity {

	private static final long serialVersionUID = -3574490742045694417L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	private int id;

	@RevisionTimestamp
	private long timestamp;

	// The user ID of the user to make the change.
	// This column "refers" to the Users table, but is not a foreign key so that
	// Users can be deleted.
	@Column(name = "user_id")
	private Long userId;

	// The client ID of the user to make the change.
	// This column "refers" to the Users table, but is not a foreign key so that
	// Users can be deleted.
	@Column(name = "client_id")
	private Long clientId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date( timestamp );
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( !(o instanceof UserRevEntity) ) {
			return false;
		}

		final UserRevEntity that = (UserRevEntity) o;
		return id == that.id
				&& timestamp == that.timestamp;
	}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "UserRevEntity(id = " + id
				+ ", revisionDate = " + DateFormat.getDateTimeInstance().format( getRevisionDate() ) + ")";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

}