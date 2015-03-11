package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * An organization or institution responsible for producing data related to
 * {@link Sample} and {@link Project}.
 * 
 *
 */
@Entity
@Table(name = "organization")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Organization implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{organization.centre.name.notnull}")
	@Size(min = 3, message = "{organization.centre.name.size}")
	private String centreName;

	@NotNull(message = "{organization.href.notnull}")
	@URL(message = "{organization.href.url}")
	private String href;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	public Organization() {
		this.createdDate = new Date();
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Organization) {
			Organization org = (Organization) o;
			return Objects.equals(id, org.id) && Objects.equals(centreName, org.centreName)
					&& Objects.equals(href, org.href) && Objects.equals(createdDate, org.createdDate)
					&& Objects.equals(modifiedDate, org.modifiedDate);
		}

		return false;
	}

	public int hashCode() {
		return Objects.hash(id, centreName, href, createdDate, modifiedDate);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCentreName() {
		return centreName;
	}

	public void setCentreName(String centreName) {
		this.centreName = centreName;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLabel() {
		return centreName;
	}

}
