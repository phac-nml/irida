package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;

/**
 * Class describing {@link MetadataTemplate}s that have been added to a
 * {@link Project}
 */
@Entity
@Table(name = "project_metadata_template")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectMetadataTemplateJoin implements Join<Project, MetadataTemplate> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "template_id")
	private MetadataTemplate template;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	public ProjectMetadataTemplateJoin() {
	}

	public ProjectMetadataTemplateJoin(Project project, MetadataTemplate template) {
		this.template = template;
		this.project = project;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Project getSubject() {
		return project;
	}

	@Override
	public MetadataTemplate getObject() {
		return template;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

}
