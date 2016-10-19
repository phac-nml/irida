package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * {@link Join} connection between an {@link AnalysisSubmission} and
 * {@link Project}
 */
public class ProjectAnalysisSubmissionJoin implements Join<Project, AnalysisSubmission> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "analysis_submission_id")
	private AnalysisSubmission analysisSubmission;

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
