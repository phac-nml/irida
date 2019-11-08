package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
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
	@JoinTable(joinColumns = @JoinColumn(name = "metadata_template_id"))
	private List<MetadataTemplateField> fields;

	@NotNull
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
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
