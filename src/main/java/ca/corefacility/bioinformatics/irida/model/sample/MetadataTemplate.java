package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;

/**
 * Stores a collection of {@link MetadataTemplateField}s that will often used together
 */
@Entity
@Table(name = "metadata_template")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MetadataTemplate implements MutableIridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private List<MetadataTemplateField> fields;

	@NotNull
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	@CreatedDate
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	@LastModifiedDate
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "template")
	private List<ProjectMetadataTemplateJoin> projects;

	public MetadataTemplate() {
	}

	public MetadataTemplate(String name, List<MetadataTemplateField> fields) {
		this.name = name;
		this.fields = fields;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public List<MetadataTemplateField> getFields() {
		return fields;
	}

	public void setFields(List<MetadataTemplateField> fields) {
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
