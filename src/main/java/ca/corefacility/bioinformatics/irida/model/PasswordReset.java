package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A password reset object.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "password_reset")
public class PasswordReset implements Comparable<PasswordReset> {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@OneToOne
	private User user;

	@Id
	@NotNull
	private String id;

	private PasswordReset() {
	}

	public PasswordReset(User user) {
		this.createdDate = new Date();
		this.user = user;
		this.id = UUID.randomUUID().toString();
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public User getUser() {
		return user;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int compareTo(PasswordReset passwordReset) {
		return 0;
	}
}
