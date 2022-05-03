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
import ca.corefacility.bioinformatics.irida.model.project.Project;

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

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinTable(name = "metadata_template_metadata_field", joinColumns = @JoinColumn(name = "metadata_template_id"))
	private List<MetadataTemplateField> fields;

	@NotNull
	private String name;

	@Lob
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	@LastModifiedDate
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@NotNull
	private Project project;

	@Column(name = "project_default")
	@NotNull
	private boolean projectDefault;

	public MetadataTemplate() {
	}

	public MetadataTemplate(String name, List<MetadataTemplateField> fields) {
		this.name = name;
		this.fields = fields;
		this.projectDefault = false;
	}

	public MetadataTemplate(String name, List<MetadataTemplateField> fields, Project project) {
		this.name = name;
		this.fields = fields;
		this.project = project;
		this.projectDefault = false;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public boolean isProjectDefault() {
		return projectDefault;
	}

	public void setProjectDefault(boolean projectDefault) {
		this.projectDefault = projectDefault;
	}
}
