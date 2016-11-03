package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;

/**
 * Stores a collection of {@link LineListField}s that will often used together
 */
@Entity
@Table(name = "metadata_template")
@Audited
public class MetadataTemplate implements MutableIridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private List<LineListField> fields;

	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	public MetadataTemplate() {
	}

	public MetadataTemplate(String name, List<LineListField> fields) {

	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public List<LineListField> getFields() {
		return fields;
	}

	public void setFields(List<LineListField> fields) {
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
