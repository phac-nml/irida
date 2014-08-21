package ca.corefacility.bioinformatics.irida.model.workflow;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * A reference to a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "remote_workflow")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class RemoteWorkflow {
	
	@Id
	private String workflowId;
	
	@NotNull
	private String workflowChecksum;
	
	protected RemoteWorkflow() {
	}
	
	/**
	 * Builds a new RemoteWorkflow with the given id.
	 * @param workflowId  The id for the workflow.
	 * @param workflowChecksum A checksum for this workflow.
	 */
	public RemoteWorkflow(String workflowId, String workflowChecksum) {
		this.workflowId = workflowId;
		this.workflowChecksum = workflowChecksum;
	}
	
	/**
	 * Gets the id of this remote workflow.
	 * @return The id of this remote workflow.
	 */
	public String getWorkflowId() {
		return workflowId;
	}
	
	/**
	 * Gets the checksum of this remote workflow.
	 * @return The checksum of this remote workflow.
	 */
	public String getWorkflowChecksum() {
		return workflowChecksum;
	}

	@Override
	public String toString() {
		return "RemoteWorkflow [workflowId=" + workflowId
				+ ", workflowChecksum=" + workflowChecksum + "]";
	}
}
