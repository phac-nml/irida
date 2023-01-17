package ca.corefacility.bioinformatics.irida.utils.model;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.IridaThing;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 *
 */
@Entity
@Table(name = "identifiable")
@Audited
public class IdentifiableTestEntity extends IridaRepresentationModel
		implements IridaThing, Comparable<IdentifiableTestEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String nonNull;
	private Integer integerValue;

	private String label;

	private Date createdDate;

	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "identifiableTestEntity")
	private List<EntityJoin> otherEntity;

	public IdentifiableTestEntity() {
		createdDate = new Date();
		modifiedDate = createdDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("created: ").append(createdDate);
		return builder.toString();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNonNull() {
		return nonNull;
	}

	public void setNonNull(String nonNull) {
		this.nonNull = nonNull;
	}

	@Override
	public int compareTo(IdentifiableTestEntity o) {
		return createdDate.compareTo(o.createdDate);
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nonNull, integerValue, createdDate);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof IdentifiableTestEntity) {
			IdentifiableTestEntity u = (IdentifiableTestEntity) other;
			return Objects.equals(nonNull, u.nonNull) && Objects.equals(integerValue, u.integerValue)
					&& Objects.equals(createdDate, u.createdDate);
		}

		return false;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
