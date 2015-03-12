package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Logical organization for user accounts.
 * 
 *
 */
@Entity
// group is a reserved word ('group by')
@Table(name = "logicalGroup")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Group implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{group.name.notnull}")
	@Size(min = 3, message = "{group.name.size}")
	private String name;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "logicalGroup")
	private Set<UserGroupJoin> userGroups;

	public Group() {
		this.createdDate = new Date();
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

	public void setId(Long id) {
		this.id = id;
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
	public Date getCreatedDate() {
		return createdDate;
	}
}
