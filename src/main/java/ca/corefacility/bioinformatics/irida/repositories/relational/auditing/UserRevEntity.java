package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@RevisionEntity(UserRevListener.class)
@Table(name = "Revisions")
public class UserRevEntity extends DefaultRevisionEntity {

	private static final long serialVersionUID = 3278915140026625641L;

	@OneToOne
	@JoinColumn(name = "user_id")
	User user;

	// OAuth2 clientId will be populated if the user logged in via OAuth2
	String clientId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}