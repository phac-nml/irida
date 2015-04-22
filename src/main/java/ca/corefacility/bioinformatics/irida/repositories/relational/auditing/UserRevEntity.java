package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * 
 */
@Entity
@RevisionEntity(UserRevListener.class)
@Table(name = "Revisions")
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