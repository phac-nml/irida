package ca.corefacility.bioinformatics.irida.model;

import org.hibernate.envers.Audited;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "password_reset")
@Audited
public class PasswordReset implements Comparable<PasswordReset> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@NotNull
	@OneToOne
	private User user;

	public PasswordReset() {
		this.createdDate = new Date();
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public int compareTo(PasswordReset o) {
		return 0;
	}
}
