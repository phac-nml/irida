package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Defines a submission to an AnalysisService for executing a remote workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T>
 *            Defines the RemoteWorkflow implementing this analysis.
 */
@Entity
@Table(name = "analysis_submission")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AnalysisSubmission implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@NotNull
	@Size(min = 3)
	@Column(name="name")
	private String name;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional=false)
	@JoinColumn(name="submitter", nullable=false)
	private User submitter;
	
	/**
	 * Defines the id of an installed workflow in IRIDA for performing this analysis.
	 */
	@NotNull
	@Column(name="workflow_id")
	private UUID workflowId;

	/**
	 * Defines the remote id for the location where an analysis was run. With
	 * Galaxy this represents the History id.
	 */
	@Column(name="remote_analysis_id")
	private String remoteAnalysisId;

	/**
	 * Defines the remote id of the workflow being executed. With Galaxy this
	 * represents the Workflow id.
	 */
	@Column(name="remote_workflow_id")
	private String remoteWorkflowId;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinTable(name = "analysis_submission_sequence_file_single", joinColumns = @JoinColumn(name = "analysis_submission_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "sequence_file_id", nullable = false))
	private Set<SequenceFile> inputFilesSingle;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinTable(name = "analysis_submission_sequence_file_pair", joinColumns = @JoinColumn(name = "analysis_submission_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "sequence_file_pair_id", nullable = false))
	private Set<SequenceFilePair> inputFilesPaired;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "name", nullable = false)
	@Column(name = "value", nullable = false)
	@CollectionTable(name = "analysis_submission_parameters", joinColumns = @JoinColumn(name = "id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"id", "name" }, name = "UK_ANALYSIS_SUBMISSION_PARAMETER_NAME"))
	private Map<String,String> inputParameters;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date")
	private Date modifiedDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="analysis_state")
	private AnalysisState analysisState;

	// Analysis entity for this analysis submission. Cascading everything except
	// removals
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name="analysis_id")
	private Analysis analysis;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "reference_file_id")
	private ReferenceFile referenceFile;

	protected AnalysisSubmission() {
		this.createdDate = new Date();
		this.analysisState = AnalysisState.NEW;
	}
	
	/**
	 * Builds a new {@link AnalysisSubmission} with the given
	 * {@link AnalysisSubmission.Builder}.
	 * 
	 * @param builder
	 *            The {@link AnalyisSubmission.Builder} to build the
	 *            {@link AnalysisSubmission}.
	 */
	public AnalysisSubmission(Builder builder) {
		this();
		checkNotNull(builder.workflowId, "workflowId is null");
		checkArgument(builder.inputFilesSingle != null || builder.inputFilesPaired != null,
				"both inputFilesSingle and inputFilesPaired are null.  You must supply at least one set of input files");

		this.name = (builder.name != null) ? builder.name : "Unknown";
		this.inputFilesSingle = (builder.inputFilesSingle != null) ? builder.inputFilesSingle : Sets.newHashSet();
		this.inputFilesPaired = (builder.inputFilesPaired != null) ? builder.inputFilesPaired : Sets.newHashSet();
		this.inputParameters = ImmutableMap.copyOf(builder.inputParameters);
		this.referenceFile = builder.referenceFile;
		this.workflowId = builder.workflowId;
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
	public Optional<ReferenceFile> getReferenceFile() {
		return (referenceFile != null) ? Optional.of(referenceFile) : Optional.empty();
	}

	/**
	 * Gets an analysis id for this workflow
	 * 
	 * @return An analysis id for this workflow.
	 */
	public String getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	/**
	 * Gets the set of single-end input sequence files.
	 * 
	 * @return The set of single-end input sequence files.
	 */
	public Set<SequenceFile> getSingleInputFiles() {
		return inputFilesSingle;
	}
	
	/**
	 * Gets the set of paired-end input sequence files.
	 * 
	 * @return The set of paired-end input sequence files.
	 */
	public Set<SequenceFilePair> getPairedInputFiles() {
		return inputFilesPaired;
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
	public Analysis getAnalysis() {
		return analysis;
	}

	public User getSubmitter() {
		return submitter;
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
	 * @param analysis
	 *            the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public String toString() {
		return "AnalysisSubmission [id=" + id + ", name=" + name + ", submitter=" + submitter + ", workflowId="
				+ workflowId + ", remoteAnalysisId=" + remoteAnalysisId + ", remoteWorkflowId=" + remoteWorkflowId
				+ ", inputFilesSingle=" + inputFilesSingle + ", inputFilesPaired=" + inputFilesPaired
				+ ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", analysisState="
				+ analysisState + ", analysis=" + analysis + ", referenceFile=" + referenceFile + ", inputParameters=" + inputParameters + "]";
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
		return inputParameters;
	}
	
	/**
	 * Used to build up an {@link AnalysisSubmission}.
	 * 
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 */
	public static class Builder {
		private String name;
		private Set<SequenceFile> inputFilesSingle;
		private Set<SequenceFilePair> inputFilesPaired;
		private ReferenceFile referenceFile;
		private UUID workflowId;
		private Map<String,String> inputParameters;

		/**
		 * Sets a name for this submission.
		 * 
		 * @param name
		 *            A name for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Sets the inputFilesSingle for this submission.
		 * 
		 * @param inputFilesSingle
		 *            The inputFilesSingle for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder inputFilesSingle(Set<SequenceFile> inputFilesSingle) {
			this.inputFilesSingle = inputFilesSingle;
			return this;
		}

		/**
		 * Sets the inputFilesPaired for this submission.
		 * 
		 * @param inputFilesSingle
		 *            The inputFilesPaired for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder inputFilesPaired(Set<SequenceFilePair> inputFilesPaired) {
			this.inputFilesPaired = inputFilesPaired;
			return this;
		}

		/**
		 * Sets the referenceFile for this submission.
		 * 
		 * @param inputFilesSingle
		 *            The referenceFile for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder referenceFile(ReferenceFile referenceFile) {
			this.referenceFile = referenceFile;
			return this;
		}

		/**
		 * Sets the workflowId for this submission.
		 * 
		 * @param inputFilesSingle
		 *            The workflowId for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder workflowId(UUID workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		/**
		 * Sets the input parameters for this submission.
		 * 
		 * @param inputParameters
		 *            A map of parameters for this submission.
		 * @return An {@link AnalysisSubmission.Builder}.
		 */
		public Builder inputParameters(Map<String, String> inputParameters) {
			this.inputParameters = inputParameters;
			return this;
		}

		public AnalysisSubmission build() {
			return new AnalysisSubmission(this);
		}
	}
	
	/**
	 * Gets an {@link AnalysisSubmission.Builder}.
	 * 
	 * @return An {@link AnalysisSubmission.Builder}.
	 */
	public static Builder builder() {
		return new AnalysisSubmission.Builder();
	}
}
