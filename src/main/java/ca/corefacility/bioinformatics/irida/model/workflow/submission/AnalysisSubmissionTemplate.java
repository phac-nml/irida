package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Shell for an analysis template to be submitted automatically when data is uploaded to a project.
 */
@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AnalysisSubmissionTemplate extends AbstractAnalysisSubmission
		implements Comparable<AnalysisSubmissionTemplate> {

	@ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(name = "submitted_project_id")
	private Project submittedProject;

	@NotNull
	private boolean enabled;

	@Lob
	@Column(name = "status_message")
	private String statusMessage;

	protected AnalysisSubmissionTemplate() {
		super();
		enabled = true;
	}

	public AnalysisSubmissionTemplate(String name, UUID workflowId, IridaWorkflowNamedParameters namedParameters,
			ReferenceFile referenceFile, boolean updateSamples, String analysisDescription, Priority priority,
			boolean emailPipelineResult, Project submittedProject) {
		this();
		this.name = name;
		this.workflowId = workflowId;
		this.referenceFile = referenceFile;
		this.namedParameters = namedParameters;
		this.updateSamples = updateSamples;
		this.analysisDescription = analysisDescription;
		this.priority = priority;
		this.emailPipelineResult = emailPipelineResult;
		this.submittedProject = submittedProject;
	}

	public AnalysisSubmissionTemplate(String name, UUID workflowId, Map<String, String> inputParameters,
			ReferenceFile referenceFile, boolean updateSamples, String analysisDescription, Priority priority,
			boolean emailPipelineResult, Project submittedProject) {
		this();
		this.name = name;
		this.workflowId = workflowId;
		this.inputParameters = inputParameters;
		this.referenceFile = referenceFile;
		this.updateSamples = updateSamples;
		this.analysisDescription = analysisDescription;
		this.priority = priority;
		this.emailPipelineResult = emailPipelineResult;
		this.submittedProject = submittedProject;
	}

	/**
	 * Sets the reference file.
	 *
	 * @param referenceFile The reference file.
	 */
	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	public Project getSubmittedProject() {
		return submittedProject;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, workflowId, createdDate, modifiedDate, referenceFile, namedParameters, submitter,
				priority);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AnalysisSubmissionTemplate) {
			AnalysisSubmissionTemplate p = (AnalysisSubmissionTemplate) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(modifiedDate, p.modifiedDate)
					&& Objects.equals(name, p.name) && Objects.equals(workflowId, p.workflowId) && Objects.equals(
					referenceFile, p.referenceFile) && Objects.equals(namedParameters, p.namedParameters)
					&& Objects.equals(submitter, p.submitter) && Objects.equals(priority, p.priority);
		}

		return false;
	}

	@Override
	public int compareTo(AnalysisSubmissionTemplate o) {
		return modifiedDate.compareTo(o.modifiedDate);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
}
