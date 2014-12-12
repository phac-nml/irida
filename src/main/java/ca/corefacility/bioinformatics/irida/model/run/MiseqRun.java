package ca.corefacility.bioinformatics.irida.model.run;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "miseq_run")
@Audited
public class MiseqRun extends SequencingRun implements IridaThing {

	private String investigatorName;

	private String projectName;

	private String experimentName;

	/*
	 * Workflow is the only required field we're capturing from the Miseq Sample
	 * Sheet reference guide.
	 */
	@NotNull
	private String workflow;

	private String application;

	private String assay;

	private String chemistry;

	public String getInvestigatorName() {
		return investigatorName;
	}

	public void setInvestigatorName(String investigatorName) {
		this.investigatorName = investigatorName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getAssay() {
		return assay;
	}

	public void setAssay(String assay) {
		this.assay = assay;
	}

	public String getChemistry() {
		return chemistry;
	}

	public void setChemistry(String chemistry) {
		this.chemistry = chemistry;
	}

	@Override
	public String getLabel() {
		return "MiseqRun: " + projectName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), application, assay, chemistry, experimentName, investigatorName,
				projectName, workflow);
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
				&& Objects.equals(this.assay, other.assay) && Objects.equals(this.chemistry, other.chemistry)) {
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
	public String getSequencerName() {
		return "MiSeq";
	}

}
