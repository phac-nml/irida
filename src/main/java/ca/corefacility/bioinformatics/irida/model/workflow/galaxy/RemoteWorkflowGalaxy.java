package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * A RemoteWorkflow that can be submitted to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "remote_workflowgalaxy")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class RemoteWorkflowGalaxy implements RemoteWorkflow {
	
	@Id
	private String workflowId;
	
	@NotNull
	private String workflowChecksum;
	
	/**
	 * Builds a new RemoteWorkflowGalaxy.
	 * @param workflowId The id of the workflow in Galaxy.
	 * @param workflowChecksum  The checksum of the workflow in Galaxy.
	 */
	public RemoteWorkflowGalaxy(String workflowId, String workflowChecksum) {
		this.workflowId = workflowId;
		this.workflowChecksum = workflowChecksum;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public String getWorkflowId() {
		return workflowId;
	}

	@Override
	public String getWorkflowChecksum() {
		return workflowChecksum;
	}
	
	public void setWorkflowChecksum(String workflowChecksum) {
		this.workflowChecksum = workflowChecksum;
	}

	@Override
	public String toString() {
		return "RemoteWorkflowGalaxy [workflowId=" + workflowId
				+ ", workflowChecksum=" + workflowChecksum + "]";
	}
}
