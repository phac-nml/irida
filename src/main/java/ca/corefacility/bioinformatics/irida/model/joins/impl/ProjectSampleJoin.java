package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;
import java.util.Objects;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * 
 */
@Entity
@Table(name = "project_sample", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id", "sample_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectSampleJoin implements Join<Project, Sample> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@Column(name = "owner")
	@NotNull
	private boolean owner;

	public ProjectSampleJoin() {
		createdDate = new Date();
		owner = true;
	}

	public ProjectSampleJoin(Project subject, Sample object, boolean owner) {
		this();
		this.project = subject;
		this.sample = object;
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProjectSampleJoin) {
			ProjectSampleJoin j = (ProjectSampleJoin) o;
			return Objects.equals(project, j.project) && Objects.equals(sample, j.sample);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, sample);
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public Project getSubject() {
		return project;
	}

	@Override
	public Sample getObject() {
		return sample;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Whether the {@link Project} has modification rights to the {@link Sample}
	 * 
	 * @return true if the {@link Project} owns the {@link Sample}
	 */
	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Project: " + project.getName() + " Sample: " + sample.getSampleName();
	}
}
