package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.Timestamped;

/**
 * A reference to a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "remote_workflow")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class RemoteWorkflow implements Timestamped {
	
	@Id
	private String workflowId;
	
	@NotNull
	private String workflowChecksum;
	
	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	protected RemoteWorkflow() {
		createdDate = new Date();
	}
	
	/**
	 * Builds a new RemoteWorkflow with the given id.
	 * @param workflowId  The id for the workflow.
	 * @param workflowChecksum A checksum for this workflow.
	 */
	public RemoteWorkflow(String workflowId, String workflowChecksum) {
		this();
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
	public String toString() {
		return "RemoteWorkflow [workflowId=" + workflowId
				+ ", workflowChecksum=" + workflowChecksum + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(workflowId, workflowChecksum);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof RemoteWorkflow) {
			RemoteWorkflow other = (RemoteWorkflow)obj;
			
			return Objects.equals(workflowId, other.workflowId) &&
					Objects.equals(workflowChecksum, other.workflowChecksum);
		}
		
		return false;
	}
}
