package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;

/**
 * Galaxy Job failure information when a tool in a IRIDA workflow produces an error
 * state for an AnalysisSubmission
 */
@Audited
@Entity
@Table(name = "job_error")
@EntityListeners(AuditingEntityListener.class)
public class JobError extends IridaResourceSupport implements IridaThing, Comparable<JobError> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	/**
	 * Galaxy Tool id (e.g. "toolshed.g2.bx.psu.edu/repos/nml/spades/spades/1.4")
	 */
	@Column(name = "tool_id")
	private final String toolId;

	/**
	 * Galaxy Tool name (e.g. "spades")
	 */
	@NotNull
	@Column(name = "tool_name")
	private final String toolName;

	/**
	 * Galaxy Tool version (e.g. "1.4")
	 */
	@NotNull
	@Column(name = "tool_version")
	private final String toolVersion;

	/**
	 * Galaxy Tool description (e.g. "SPAdes genome assembler for regular and single-cell projects")
	 */
	@Column(name = "tool_description")
	private final String toolDescription;

	/**
	 * Tool command-line executed by Galaxy (e.g. "perl
	 * /shed_tools/toolshed.g2.bx.psu.edu/repos/nml/spades/35cb17bd8bf9/spades/spades.pl ..."
	 */
	@NotNull
	@Lob
	@Column(name = "command_line")
	private final String commandLine;

	/**
	 * Galaxy History Contents Provenance parameters for workflow
	 * (e.g. "{iontorrent=\"false\",
	 * libraries_1|files_0|unpaired_reads|__identifier__=\"fail\",
	 * ...}")
	 */
	@Lob
	@Column(name = "parameters")
	private final String parameters;

	/**
	 * Galaxy Job standard error if any
	 */
	@Lob
	@Column(name = "standard_error")
	private final String standardError;

	/**
	 * Galaxy Job standard output if any
	 */
	@Lob
	@Column(name = "standard_output")
	private final String standardOutput;

	/**
	 * Galaxy History Contents Provenance UUID
	 */
	@Column(name = "provenance_uuid")
	@Type(type = "uuid-char")
	private final UUID provenanceUUID;

	/**
	 * Galaxy History Contents Provenance id
	 */
	@Column(name = "provenance_id")
	private final String provenanceId;

	/**
	 * Galaxy Job id
	 */
	@Column(name = "job_id")
	private final String jobId;

	/**
	 * Galaxy History id
	 */
	@Column(name = "history_id")
	private final String historyId;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private final Date createdDate;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private final Date updatedDate;

	@NotNull
	@Column(name = "exit_code")
	private final int exitCode;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "analysis_submission_id", foreignKey = @ForeignKey(name = "FK_JOB_ERROR_ANALYSIS_SUBMISSION"))
	private AnalysisSubmission analysisSubmission;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	public JobError() {
		id = null;
		toolName = null;
		commandLine = null;
		exitCode = 0;
		toolVersion = null;
		parameters = null;
		standardError = null;
		updatedDate = new Date();
		createdDate = new Date();
		historyId = null;
		standardOutput = null;
		jobId = null;
		provenanceUUID = null;
		provenanceId = null;
		toolId = null;
		toolDescription = null;
	}

	public JobError(AnalysisSubmission submission, JobDetails job, HistoryContentsProvenance provenance, Tool tool) {
		id = null;
		this.analysisSubmission = submission;
		exitCode = job.getExitCode();
		commandLine = job.getCommandLine();
		provenanceId = provenance.getId();
		provenanceUUID = UUID.fromString(provenance.getUuid());
		createdDate = job.getCreated();
		updatedDate = job.getUpdated();
		historyId = submission.getRemoteAnalysisId();
		standardError = provenance.getStandardError();
		standardOutput = provenance.getStandardOutput();
		toolId = provenance.getToolId();
		jobId = provenance.getJobId();
		toolVersion = tool.getVersion();
		toolName = tool.getName();
		toolDescription = tool.getDescription();
		parameters = provenance.getParameters()
				.toString();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLabel() {
		return toolId + "-" + updatedDate.toString();
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getToolId() {
		return toolId;
	}

	/**
	 * @return Galaxy {@link Tool} name
	 */
	public String getToolName() {
		return toolName;
	}

	/**
	 * @return Galaxy {@link Tool} version
	 */
	public String getToolVersion() {
		return toolVersion;
	}

	/**
	 * @return Galaxy {@link Tool} description
	 */
	public String getToolDescription() {
		return toolDescription;
	}

	/**
	 * @return Galaxy tool command-line
	 */
	public String getCommandLine() {
		return commandLine;
	}

	/**
	 * @return Galaxy tool parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @return Galaxy tool standard error
	 */
	public String getStandardError() {
		return standardError;
	}

	/**
	 * @return Galaxy tool standard output
	 */
	public String getStandardOutput() {
		return standardOutput;
	}

	public UUID getProvenanceUUID() {
		return provenanceUUID;
	}

	/**
	 * @return Galaxy {@link HistoryContentsProvenance} id
	 */
	public String getProvenanceId() {
		return provenanceId;
	}

	/**
	 * @return Galaxy {@link JobDetails} id
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @return Galaxy {@link com.github.jmchilton.blend4j.galaxy.beans.History} id
	 */
	public String getHistoryId() {
		return historyId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public int getExitCode() {
		return exitCode;
	}

	@JsonIgnore
	public AnalysisSubmission getAnalysisSubmission() {
		return analysisSubmission;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JobError jobError = (JobError) o;
		return getExitCode() == jobError.getExitCode() &&
		       Objects.equals(getId(), jobError.getId()) &&
		       Objects.equals(getToolId(), jobError.getToolId()) &&
		       Objects.equals(getToolName(), jobError.getToolName()) &&
		       Objects.equals(getToolVersion(), jobError.getToolVersion()) &&
		       Objects.equals(getToolDescription(), jobError.getToolDescription()) &&
		       Objects.equals(getCommandLine(), jobError.getCommandLine()) &&
		       Objects.equals(getParameters(), jobError.getParameters()) &&
		       Objects.equals(getStandardError(), jobError.getStandardError()) &&
		       Objects.equals(getStandardOutput(), jobError.getStandardOutput()) &&
		       Objects.equals(getProvenanceUUID(), jobError.getProvenanceUUID()) &&
		       Objects.equals(getProvenanceId(), jobError.getProvenanceId()) &&
		       Objects.equals(getJobId(), jobError.getJobId()) &&
		       Objects.equals(getHistoryId(), jobError.getHistoryId()) &&
		       Objects.equals(getCreatedDate(), jobError.getCreatedDate()) &&
		       Objects.equals(getUpdatedDate(), jobError.getUpdatedDate()) &&
		       Objects.equals(getAnalysisSubmission(), jobError.getAnalysisSubmission());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(),
				getToolId(),
				getToolName(),
				getToolVersion(),
				getToolDescription(),
				getCommandLine(),
				getParameters(),
				getStandardError(),
				getStandardOutput(),
				getProvenanceUUID(),
				getProvenanceId(),
				getJobId(),
				getHistoryId(),
				getCreatedDate(),
				getUpdatedDate(),
				getExitCode(),
				getAnalysisSubmission());
	}

	@Override
	public String toString() {
		return "JobError{" + "id=" + id +
				", toolId='" + toolId + '\'' +
				", toolName='" + toolName + '\'' +
				", toolVersion='" + toolVersion + '\'' +
				", toolDescription='" + toolDescription + '\'' +
				", commandLine='" + commandLine + '\'' +
				", parameters='" + parameters + '\'' +
				", standardError='" + standardError + '\'' +
				", standardOutput='" + standardOutput + '\'' +
				", provenanceUUID=" + provenanceUUID +
				", provenanceId='" + provenanceId + '\'' +
				", jobId='" + jobId + '\'' +
				", historyId='" + historyId + '\'' +
				", createdDate=" + createdDate +
				", updatedDate=" + updatedDate +
				", exitCode=" + exitCode +
				", analysisSubmission=" + analysisSubmission +
				'}';
	}

	@Override
	public int compareTo(JobError o) {
		return updatedDate.compareTo(o.updatedDate);
	}
}
