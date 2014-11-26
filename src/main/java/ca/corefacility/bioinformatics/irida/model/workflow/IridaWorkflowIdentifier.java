package ca.corefacility.bioinformatics.irida.model.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

/**
 * Class used to identify a workflow within Irida.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowIdentifier {
	private String workflowName;
	private String workflowVersion;

	/**
	 * Construct a new identifier for an IRIDA workflow.
	 * 
	 * @param workflowName
	 *            The name of the workflow.
	 * @param workflowVersion
	 *            The version of the workflow.
	 */
	public IridaWorkflowIdentifier(String workflowName, String workflowVersion) {
		checkNotNull(workflowName, "workflowName is null");
		checkNotNull(workflowVersion, "workflowVersion is null");

		this.workflowName = workflowName;
		this.workflowVersion = workflowVersion;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public String getWorkflowVersion() {
		return workflowVersion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(workflowName, workflowVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowIdentifier) {
			IridaWorkflowIdentifier other = (IridaWorkflowIdentifier) obj;

			return Objects.equals(workflowName, other.workflowName)
					&& Objects.equals(workflowVersion, other.workflowVersion);
		}

		return false;
	}

	@Override
	public String toString() {
		return "IridaWorkflowIdentifier [workflowName=" + workflowName + ", workflowVersion=" + workflowVersion + "]";
	}
}
