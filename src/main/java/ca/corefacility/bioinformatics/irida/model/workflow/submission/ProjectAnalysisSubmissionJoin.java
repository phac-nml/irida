package ca.corefacility.bioinformatics.irida.model.workflow.submission;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * {@link Join} connection between an {@link AnalysisSubmission} and
 * {@link Project}
 */
@Entity
@Table(name = "project_analysis_submission", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id",
		"analysis_submission_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectAnalysisSubmissionJoin implements Join<Project, AnalysisSubmission> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "analysis_submission_id")
	private AnalysisSubmission analysisSubmission;

	public ProjectAnalysisSubmissionJoin() {
	}

	public ProjectAnalysisSubmissionJoin(Project project, AnalysisSubmission analysisSubmission) {
		this.project = project;
		this.analysisSubmission = analysisSubmission;
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
	public AnalysisSubmission getObject() {
		return analysisSubmission;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

}
