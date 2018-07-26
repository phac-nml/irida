package ca.corefacility.bioinformatics.irida.exceptions;

import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;


/**
 * Exception that gets thrown if a workflow is not found.
 * 
 *
 */
public class IridaWorkflowNotFoundException extends IridaWorkflowLoadException {

	private static final long serialVersionUID = 5002643601663546169L;

	/**
	 * Constructs a new {@link IridaWorkflowNotFoundException} with the given
	 * workflow identifier.
	 * 
	 * @param workflowId
	 *            The identifier of the workflow.
	 */
	public IridaWorkflowNotFoundException(UUID workflowId) {
		super("No workflow found for " + workflowId);
	}

	/**
	 * Constructs a new {@link IridaWorkflowNotFoundException} with the given
	 * analysis type.
	 * 
	 * @param analysisType
	 *            The analysis type of the workflow.
	 */
	public IridaWorkflowNotFoundException(AnalysisType analysisType) {
		super("No workflows found for " + analysisType);
	}
	
	/**
	 * Constructs a new {@link IridaWorkflowNotFoundException} with the given
	 * workflow name.
	 * 
	 * @param workflowName
	 *            The name of the workflow.
	 */
	public IridaWorkflowNotFoundException(String workflowName) {
		super("No workflows found for workflow named " + workflowName);
	}
}
