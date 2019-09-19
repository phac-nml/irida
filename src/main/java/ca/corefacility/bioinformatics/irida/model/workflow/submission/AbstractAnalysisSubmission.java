package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract superclass for analysis submissions.
 */
@Entity
@Table(name = "analysis_submission")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAnalysisSubmission extends IridaResourceSupport implements MutableIridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	protected Long id;

	@NotNull
	@Size(min = 3)
	@Column(name = "name")
	protected String name;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
	@JoinColumn(name = "submitter", nullable = false)
	protected User submitter;

	/**
	 * Defines the id of an installed workflow in IRIDA for performing this analysis.
	 */
	@NotNull
	@Column(name = "workflow_id")
	@Type(type = "uuid-char")
	protected UUID workflowId;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "name", nullable = false)
	@Column(name = "value", nullable = false)
	@CollectionTable(name = "analysis_submission_parameters", joinColumns = @JoinColumn(name = "id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"id", "name" }, name = "UK_ANALYSIS_SUBMISSION_PARAMETER_NAME"))
	protected Map<String, String> inputParameters;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	protected Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	protected Date modifiedDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "reference_file_id")
	protected ReferenceFile referenceFile;

	@NotAudited
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "named_parameters_id")
	protected IridaWorkflowNamedParameters namedParameters;

	@NotNull
	@Column(name = "update_samples")
	protected boolean updateSamples;

	@Column(name = "analysis_description")
	@Lob
	protected String analysisDescription;

	@NotNull
	@Column(name = "email_pipeline_result")
	protected boolean emailPipelineResult;

	protected AbstractAnalysisSubmission() {
		this.createdDate = new Date();
	}

	/**
	 * Sets the reference file.
	 *
	 * @param referenceFile The reference file.
	 */
	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	/**
	 * Gets the ReferenceFile.
	 *
	 * @return The ReferenceFile.
	 */
	@JsonIgnore
	public Optional<ReferenceFile> getReferenceFile() {
		return (referenceFile != null) ? Optional.of(referenceFile) : Optional.empty();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public User getSubmitter() {
		return submitter;
	}

	/**
	 * Sets the {@link User} who is submitting this analysis.
	 *
	 * @param submitter The {@link User} who is submitting this analysis.
	 */
	public void setSubmitter(User submitter) {
		checkNotNull(submitter, "the submitter is null");
		this.submitter = submitter;
	}

	@Override
	public String toString() {
		String userName = (submitter == null) ? "null" : submitter.getUsername();
		return this.getClass().getName() + " [id=" + id + ", name=" + name + ", submitter=" + userName + ", workflowId="
				+ workflowId + "]";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the id of the implementing workflow for this analysis.
	 *
	 * @return The id of the implementing workflow for this analysis.
	 */
	public UUID getWorkflowId() {
		return workflowId;
	}

	/**
	 * Sets the id of the workflow for this analysis.
	 *
	 * @param workflowId The id of the workflow for this analysis.
	 */
	public void setWorkflowId(UUID workflowId) {
		this.workflowId = workflowId;
	}

	/**
	 * Gets the input parameters for this submission.
	 *
	 * @return The input parameters for this submission.
	 */
	public Map<String, String> getInputParameters() {
		if (this.namedParameters != null) {
			return this.namedParameters.getInputParameters();
		} else {
			return inputParameters;
		}
	}

	/**
	 * Get the named parameters object used to build this submission.
	 *
	 * @return The {@link IridaWorkflowNamedParameters} for this submission.
	 */
	@JsonIgnore
	public final IridaWorkflowNamedParameters getNamedParameters() {
		return namedParameters;
	}

	/**
	 * Get the description of the analysis
	 *
	 * @return The description of the analysis
	 */
	public String getAnalysisDescription() {
		return this.analysisDescription;
	}

	/**
	 * Set the description of the analysis for this submission
	 *
	 * @param description The description of the analysis
	 */
	public void setAnalysisDescription(String description) {
		this.analysisDescription = description;
	}

	/**
	 * Sets flag to indicate whether or not samples in the submission should be updated with analysis results following
	 * completion.
	 *
	 * @param updateSamples If true, updates samples from results on completion.
	 */
	public void setUpdateSamples(boolean updateSamples) {
		this.updateSamples = updateSamples;
	}

	/**
	 * Whether or not to update samples from results on completion.
	 *
	 * @return Update samples from results on completion.
	 */
	public boolean getUpdateSamples() {
		return updateSamples;
	}

	/**
	 * Sets flag to indicate whether or not user should be emailed upon pipeline completion or error.
	 * @param emailPipelineResult If true, email pipeline result to user.
	 */
	public void setEmailPipelineResult(boolean emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}

	/**
	 * Whether or not to send an email upon pipeline completion or error.
	 * @return Email pipeline result on completion or error.
	 */
	public boolean getEmailPipelineResult() {
		return emailPipelineResult;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, workflowId, createdDate, modifiedDate, referenceFile, namedParameters, submitter);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractAnalysisSubmission) {
			AbstractAnalysisSubmission p = (AbstractAnalysisSubmission) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(modifiedDate, p.modifiedDate)
					&& Objects.equals(name, p.name) && Objects.equals(workflowId, p.workflowId) && Objects.equals(
					referenceFile, p.referenceFile) && Objects.equals(namedParameters, p.namedParameters)
					&& Objects.equals(submitter, p.submitter);
		}

		return false;
	}
}
