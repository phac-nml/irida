package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import java.text.DateFormat;

import javax.persistence.*;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * Envers Revision class storing the timestamp, user, and oauth client used to make a change.
 */
@Entity
@Table(name = "Revisions")
@RevisionEntity(UserRevListener.class)
public class UserRevEntity extends DefaultRevisionEntity {

	private static final long serialVersionUID = -3574490742045694417L;

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


	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( !(o instanceof UserRevEntity) ) {
			return false;
		}

		final UserRevEntity that = (UserRevEntity) o;
		return getId() == that.getId()
				&& getTimestamp() == that.getTimestamp();
	}

	@Override
	public String toString() {
		return "UserRevEntity(id = " + getId()
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