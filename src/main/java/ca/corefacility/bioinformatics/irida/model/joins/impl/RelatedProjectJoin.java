package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Relationship between two {@link Project}s. A {@link Project} can have a list
 * of {@link RelatedProjectJoin}s where it will search for {@link Sample}s.
 * 
 *
 */
@Entity
@Table(name = "related_project", uniqueConstraints = @UniqueConstraint(columnNames = { "subject_id",
		"relatedProject_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class RelatedProjectJoin implements Join<Project, Project> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "subject_id")
	@NotNull
	private Project subject;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "relatedProject_id")
	@NotNull
	private Project relatedProject;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	public RelatedProjectJoin() {
		createdDate = new Date();
	}

	public RelatedProjectJoin(Project subject, Project object) {
		this();
		this.subject = subject;
		this.relatedProject = object;
	}

	@Override
	public String getLabel() {
		return "Related project: " + subject.getLabel() + " => " + relatedProject.getLabel();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Project getSubject() {
		return subject;
	}

	@Override
	public Project getObject() {
		return relatedProject;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

}
