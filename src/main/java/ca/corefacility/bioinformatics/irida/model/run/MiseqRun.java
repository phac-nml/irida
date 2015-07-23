package ca.corefacility.bioinformatics.irida.model.run;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;

/**
 *
 */
@Entity
@Table(name = "miseq_run")
@Audited
public class MiseqRun extends SequencingRun {

	private String investigatorName;

	private String projectName;

	private String experimentName;

	/*
	 * Workflow is the only required field we're capturing from the Miseq Sample
	 * Sheet reference guide.
	 */
	@NotNull
	private final String workflow;

	private String application;

	private String assay;

	private String chemistry;

	@Column(name = "read_lengths")
	private Integer readLengths;
	
	public MiseqRun(final LayoutType layoutType, final String workflow) {
		super(layoutType, SequencingRunUploadStatus.UPLOADING);
		this.workflow = workflow;
	}
	
	public MiseqRun(final LayoutType layoutType, final String workflow, final SequencingRunUploadStatus uploadStatus) {
		super(layoutType, uploadStatus);
		this.workflow = workflow;
	}

	public String getInvestigatorName() {
		return investigatorName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public String getWorkflow() {
		return workflow;
	}

	public String getApplication() {
		return application;
	}

	public String getAssay() {
		return assay;
	}

	public String getChemistry() {
		return chemistry;
	}

	public Integer getReadLengths() {
		return readLengths;
	}

	@Override
	public String getLabel() {
		return "MiseqRun: " + projectName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), application, assay, chemistry, experimentName, investigatorName,
				projectName, workflow, readLengths);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MiseqRun)) {
			return false;
		}
		final MiseqRun other = (MiseqRun) obj;
		if (super.equals(obj) && Objects.equals(this.investigatorName, other.investigatorName)
				&& Objects.equals(this.projectName, other.projectName)
				&& Objects.equals(this.experimentName, other.experimentName)
				&& Objects.equals(this.workflow, other.workflow) && Objects.equals(this.application, other.application)
				&& Objects.equals(this.assay, other.assay) && Objects.equals(this.chemistry, other.chemistry)
				&& Objects.equals(this.readLengths, other.readLengths)) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "MiseqRun{" + "id=" + getId() + ", investigatorName=" + investigatorName + ", projectName="
				+ projectName + '}';
	}

	@Override
	public String getSequencerType() {
		return "MiSeq";
	}

	@Override
	public int compareTo(SequencingRun o) {
		return this.getCreatedDate().compareTo(o.getCreatedDate());
	}

}
