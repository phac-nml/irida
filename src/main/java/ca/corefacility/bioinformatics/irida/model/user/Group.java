package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Logical organization for user accounts.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
// group is a reserved word ('group by')
@Table(name = "logicalGroup")
@Audited
public class Group implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private User owner;

	@NotNull(message = "{group.name.notnull}")
	@Size(min = 3, message = "{group.name.size}")
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "logicalGroup")
	private Set<UserGroup> userGroups;

	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}

}
