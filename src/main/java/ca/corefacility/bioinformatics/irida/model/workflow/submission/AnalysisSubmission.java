package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines a submission to an AnalysisService for executing a remote workflow.
 */
@Entity
@Table(name = "analysis_submission")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AnalysisSubmission extends IridaResourceSupport implements MutableIridaThing, Comparable<AnalysisSubmission> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 3)
	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
	@JoinColumn(name = "submitter", nullable = false)
	private User submitter;

	/**
	 * Defines the id of an installed workflow in IRIDA for performing this
	 * analysis.
	 */
	@NotNull
	@Column(name = "workflow_id")
	@Type(type = "uuid-char")
	private UUID workflowId;

	/**
	 * Defines the remote id for the location where an analysis was run. With
	 * Galaxy this represents the History id.
	 */
	@Column(name = "remote_analysis_id")
	private String remoteAnalysisId;

	/**
	 * Defines the remote id for a location where input data can be uploaded to
	 * for an analysis.
	 */
	@Column(name = "remote_input_data_id")
	private String remoteInputDataId;

	/**
	 * Defines the remote id of the workflow being executed. With Galaxy this
	 * represents the Workflow id.
	 */
	@Column(name = "remote_workflow_id")
	private String remoteWorkflowId;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinTable(name = "analysis_submission_sequencing_object", joinColumns = @JoinColumn(name = "analysis_submission_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "sequencing_object_id", nullable = false))
	private Set<SequencingObject> inputFiles;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "name", nullable = false)
	@Column(name = "value", nullable = false)
	@CollectionTable(name = "analysis_submission_parameters", joinColumns = @JoinColumn(name = "id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"id", "name" }, name = "UK_ANALYSIS_SUBMISSION_PARAMETER_NAME"))
	private Map<String, String> inputParameters;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "analysis_state")
	private AnalysisState analysisState;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "analysis_cleaned_state")
	private AnalysisCleanedState analysisCleanedState;

	// Analysis entity for this analysis submission. Cascading everything except
	// removals
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "analysis_id")
	@NotAudited
	private Analysis analysis;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "reference_file_id")
	private ReferenceFile referenceFile;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "analysisSubmission")
	private List<ProjectAnalysisSubmissionJoin> projects;

	@NotAudited
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "analysisSubmission")
	private List<JobError> jobErrors;

	@NotAudited
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "named_parameters_id")
	private IridaWorkflowNamedParameters namedParameters;
	
	@NotNull
	@Column(name = "update_samples")
	private boolean updateSamples;

	protected AnalysisSubmission() {
		this.createdDate = new Date();
		this.analysisState = AnalysisState.NEW;
		this.analysisCleanedState = AnalysisCleanedState.NOT_CLEANED;
	}

	@Column(name = "analysis_description")
	@Lob
	private String analysisDescription;

	@NotNull
	@Column(name = "email_pipeline_result")
	private boolean emailPipelineResult;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Priority priority;

	/**
	 * Builds a new {@link AnalysisSubmission} with the given {@link Builder}.
	 * 
	 * @param builder
	 *            The {@link Builder} to build the {@link AnalysisSubmission}.
	 */
	public AnalysisSubmission(Builder builder) {
		this();
		checkNotNull(builder.workflowId, "workflowId is null");

		checkArgument(builder.inputFiles != null,
				"input file collection is null.  You must supply at least one set of input files");

		this.name = (builder.name != null) ? builder.name : "Unknown";
		this.inputFiles = builder.inputFiles;
		this.inputParameters = (builder.inputParameters != null) ? ImmutableMap.copyOf(builder.inputParameters)
				: ImmutableMap.of();
		this.referenceFile = builder.referenceFile;
		this.workflowId = builder.workflowId;
		this.namedParameters = builder.namedParameters;
		this.analysisDescription = (builder.analysisDescription);
		this.emailPipelineResult = builder.emailPipelineResult;
		this.updateSamples = builder.updateSamples;
		this.priority = builder.priority;
	}

	/**
	 * Sets the reference file.
	 * 
	 * @param referenceFile
	 *            The reference file.
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

	/**
	 * Gets an analysis id for this workflow
	 * 
	 * @return An analysis id for this workflow.
	 */
	@JsonIgnore
	public String getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	/**
	 * Gets the id of a remote location to store input files.
	 * 
	 * @return The id of a remote location to store input files.
	 */
	public String getRemoteInputDataId() {
		return remoteInputDataId;
	}

	/**
	 * Sets the id of a remote location to store input files.
	 * 
	 * @param remoteInputDataId
	 *            The id of a remote location to store input files.
	 */
	public void setRemoteInputDataId(String remoteInputDataId) {
		this.remoteInputDataId = remoteInputDataId;
	}

	/**
	 * Sets the remote analysis id.
	 * 
	 * @param remoteAnalysisId
	 *            The remote analysis id to set.
	 */
	public void setRemoteAnalysisId(String remoteAnalysisId) {
		this.remoteAnalysisId = remoteAnalysisId;
	}

	/**
	 * Gets the remote workflow id.
	 * 
	 * @return The remote workflow id.
	 */
	public String getRemoteWorkflowId() {
		return remoteWorkflowId;
	}

	/**
	 * Sets the remote workflow id.
	 * 
	 * @param remoteWorkflowId
	 *            The remote workflow id.
	 */
	public void setRemoteWorkflowId(String remoteWorkflowId) {
		this.remoteWorkflowId = remoteWorkflowId;
	}

	/**
	 * Gets the state of this analysis.
	 * 
	 * @return The state of this analysis.
	 */
	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	/**
	 * Sets the state of this analysis.
	 * 
	 * @param analysisState
	 *            The state of this analysis.
	 */
	public void setAnalysisState(AnalysisState analysisState) {
		this.analysisState = analysisState;
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

	/**
	 * @return the analysis
	 */
	@JsonIgnore
	public Analysis getAnalysis() {
		return analysis;
	}

	@JsonIgnore
	public User getSubmitter() {
		return submitter;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Sets the {@link User} who is submitting this analysis.
	 * 
	 * @param submitter
	 *            The {@link User} who is submitting this analysis.
	 */
	public void setSubmitter(User submitter) {
		checkNotNull(submitter, "the submitter is null");
		this.submitter = submitter;
	}

	/**
	 * Set the {@link Analysis} generated as a result of this submission. Note:
	 * {@link AnalysisSubmission#setAnalysis(Analysis)} can only be set
	 * **once**; if the current {@link Analysis} is non-null, then this method
	 * will throw a {@link AnalysisAlreadySetException}.
	 * 
	 * @param analysis
	 *            the analysis to set
	 * @throws AnalysisAlreadySetException
	 *             if the {@link Analysis} reference has already been created
	 *             for this submission.
	 */
	public void setAnalysis(Analysis analysis) throws AnalysisAlreadySetException {
		if (this.analysis == null) {
			this.analysis = analysis;
		} else {
			throw new AnalysisAlreadySetException("The analysis has already been set for this submission.");
		}
	}

	@Override
	public String toString() {
		String userName = (submitter == null) ? "null" : submitter.getUsername();
		return "AnalysisSubmission [id=" + id + ", name=" + name + ", submitter=" + userName + ", workflowId="
				+ workflowId + ", analysisState=" + analysisState + ", analysisCleanedState=" + analysisCleanedState
				+ "]";
	}

	/**
	 * @return The {@link AnalysisCleanedState}.
	 */
	public AnalysisCleanedState getAnalysisCleanedState() {
		return analysisCleanedState;
	}

	/**
	 * Sets the {@link AnalysisCleanedState}.
	 * 
	 * @param analysisCleanedState
	 *            The {@link AnalysisCleanedState}.
	 */
	public void setAnalysisCleanedState(AnalysisCleanedState analysisCleanedState) {
		this.analysisCleanedState = analysisCleanedState;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param workflowId
	 *            The id of the workflow for this analysis.
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
	 * @return The description of the analysis
     */
	public String getAnalysisDescription() {
		return this.analysisDescription;
	}

	/**
	 * Set the description of the analysis for this submission
	 * @param description
	 * 				The description of the analysis
     */
	public void setAnalysisDescription(String description) {
		this.analysisDescription = description;
	}


	/**
	 * Used to build up an {@link AnalysisSubmission}.
	 * 
	 */
	public static class Builder {
		private String name;
		private Set<SequencingObject> inputFiles;
		private ReferenceFile referenceFile;
		private UUID workflowId;
		private Map<String, String> inputParameters;
		private IridaWorkflowNamedParameters namedParameters;
		private String analysisDescription;
		private boolean updateSamples = false;
		private Priority priority = Priority.MEDIUM;
		private boolean emailPipelineResult = false;

		/**
		 * Creates a new {@link Builder} with a workflow id.
		 * 
		 * @param workflowId
		 *            The workflow id for this submission.
		 */
		public Builder(UUID workflowId) {
			checkNotNull(workflowId, "workflowId is null");

			this.workflowId = workflowId;
			this.inputParameters = Maps.newHashMap();
		}

		/**
		 * Sets a name for this submission.
		 * 
		 * @param name
		 *            A name for this submission.
		 * @return A {@link Builder}.
		 */
		public Builder name(String name) {
			checkNotNull(name, "name is null");

			this.name = name;
			return this;
		}

		/**
		 * Sets the inputFilesPaired for this submission.
		 * 
		 * @param inputFiles
		 *            The inputFilesPaired for this submission.
		 * @return A {@link Builder}.
		 */
		public Builder inputFiles(Set<SequencingObject> inputFiles) {
			checkNotNull(inputFiles, "inputFiles is null");
			checkArgument(!inputFiles.isEmpty(), "inputFiles is empty");

			this.inputFiles = inputFiles;
			return this;
		}

		/**
		 * Sets the referenceFile for this submission.
		 * 
		 * @param referenceFile
		 *            The referenceFile for this submission.
		 * @return A {@link Builder}.
		 */
		public Builder referenceFile(ReferenceFile referenceFile) {
			checkNotNull(referenceFile, "referenceFile is null");

			this.referenceFile = referenceFile;
			return this;
		}

		/**
		 * Sets the input parameters for this submission.
		 * 
		 * @param inputParameters
		 *            A map of parameters for this submission.
		 * @return A {@link Builder}.
		 */
		public Builder inputParameters(Map<String, String> inputParameters) {
			checkNotNull(inputParameters, "inputParameters is null");
			checkArgument(!inputParameters.isEmpty(), "inputParameters is empty");

			if (namedParameters != null) {
				throw new UnsupportedOperationException("You cannot change named parameters once set.");
			}

			this.inputParameters.clear();
			this.inputParameters.putAll(inputParameters);
			return this;
		}

		/**
		 * Adds an individual input parameter.
		 * 
		 * @param name
		 *            The name of the parameter.
		 * @param value
		 *            The value of the parameter.
		 * @return A {@link Builder}.
		 */
		public Builder inputParameter(final String name, final String value) {
			checkNotNull(name, "key is null");
			checkNotNull(value, "value is null");
			checkArgument(!inputParameters.containsKey(name), "key=" + name + " already exists as a parameter");

			if (namedParameters != null) {
				throw new UnsupportedOperationException("You cannot change named parameters once set.");
			}
			inputParameters.put(name, value);

			return this;
		}

		/**
		 * Use the specified set of named parameters to run this workflow.
		 * 
		 * @param parameters
		 *            the named parameters to use.
		 * @return A {@link Builder}.
		 */
		public Builder withNamedParameters(final IridaWorkflowNamedParameters parameters) {
			checkNotNull(parameters, "named parameters cannot be null.");
			this.namedParameters = parameters;
			return this;
		}

		/**
		 * Sets the description of the analysis run
		 * @param analysisDescription
		 * 				description of the analysis
         * @return A {@link Builder}
         */
		public Builder analysisDescription(final String analysisDescription) {
			this.analysisDescription = analysisDescription;

			return this;
		}

		/**
		 * Sets the {@link Priority} of the analysis run
		 *
		 * @param priority the priority of the analysis
		 * @return a {@link Builder}
		 */
		public Builder priority(final Priority priority){
			this.priority = priority;
			return this;
		}

		/**
		 * Turns on/off updating of samples from results for this analysis
		 * submission.
		 * 
		 * @param updateSamples
		 *            Turn on/off updating samples.
		 * @return A {@link Builder}
		 */
		public Builder updateSamples(boolean updateSamples) {
			this.updateSamples = updateSamples;

			return this;
		}

		/**
		 * Sets if user should be emailed on
		 * pipeline completion or error
		 *
		 * @param emailPipelineResult If user should be emailed or not
		 * @return A {@link Builder}
		 */
		public Builder emailPipelineResult(boolean emailPipelineResult) {
			this.emailPipelineResult = emailPipelineResult;

			return this;
		}

		/**
		 * Build the analysis submission from the set parameters
		 * @return the new AnalysisSubmission
		 */
		public AnalysisSubmission build() {
			checkArgument(inputFiles != null,
					"input file collection is null.  You must supply at least one set of input files");

			return new AnalysisSubmission(this);
		}
	}

	/**
	 * Gets a {@link Builder}.
	 * 
	 * @param workflowId
	 *            The id of the workflow to submit.
	 * 
	 * @return A {@link Builder}.
	 */
	public static Builder builder(UUID workflowId) {
		return new AnalysisSubmission.Builder(workflowId);
	}

	/**
	 * Whether or not a remoteAnalysisId exists for this submission.
	 * 
	 * @return True if a remoteAnalysisId exists for this submission, false
	 *         otherwise.
	 */
	public boolean hasRemoteAnalysisId() {
		return remoteAnalysisId != null;
	}

	/**
	 * Whether or not a remoteWorkflowId exists for this submission.
	 * 
	 * @return True if a remoteWorkflowId exists for this submission, false
	 *         otherwise.
	 */
	public boolean hasRemoteWorkflowId() {
		return remoteWorkflowId != null;
	}

	/**
	 * Whether or not a remoteInputDataId exists for this submission.
	 * 
	 * @return True if a remoteInputDataId exists for this submission, false
	 *         otherwise.
	 */
	public boolean hasRemoteInputDataId() {
		return remoteInputDataId != null;
	}
	
	/**
	 * Sets flag to indicate whether or not samples in the submission should be updated with analysis results following completion.
	 * @param updateSamples If true, updates samples from results on completion.
	 */
	public void setUpdateSamples(boolean updateSamples) {
		this.updateSamples = updateSamples;
	}

	/**
	 * Whether or not to update samples from results on completion.
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
		return Objects.hash(name, workflowId, remoteAnalysisId, remoteInputDataId, remoteWorkflowId, createdDate,
				modifiedDate, analysisState, analysisCleanedState, analysis, referenceFile, namedParameters, submitter, priority);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AnalysisSubmission) {
			AnalysisSubmission p = (AnalysisSubmission) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(modifiedDate, p.modifiedDate)
					&& Objects.equals(name, p.name) && Objects.equals(workflowId, p.workflowId)
					&& Objects.equals(remoteAnalysisId, p.remoteAnalysisId)
					&& Objects.equals(remoteInputDataId, p.remoteInputDataId)
					&& Objects.equals(remoteWorkflowId, p.remoteWorkflowId)
					&& Objects.equals(analysisState, p.analysisState)
					&& Objects.equals(analysisCleanedState, p.analysisCleanedState)
					&& Objects.equals(referenceFile, p.referenceFile) && Objects
					.equals(namedParameters, p.namedParameters) && Objects.equals(submitter, p.submitter) && Objects
					.equals(priority, p.priority);
		}

		return false;
	}

	@Override
	public int compareTo(AnalysisSubmission o) {
		return modifiedDate.compareTo(o.modifiedDate);
	}

	/**
	 * Enum encoding the priority of analysis submissions
	 */
	public enum Priority {
		LOW,
		MEDIUM,
		HIGH;
	}
}
