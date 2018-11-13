package ca.corefacility.bioinformatics.irida.exceptions;

import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;

/**
 * Exception that gets thrown if a workflow is not displayable.
 * 
 *
 */
public class IridaWorkflowNotDisplayableException extends IridaWorkflowLoadException {

	private static final long serialVersionUID = 4583888416199829376L;

	/**
	 * Constructs a new {@link IridaWorkflowNotDisplayableException} with the given
	 * workflow identifier.
	 * 
	 * @param workflowId
	 *            The identifier of the workflow.
	 */
	public IridaWorkflowNotDisplayableException(UUID workflowId) {
		super("The workflow " + workflowId + " has been disabled");
	}

	/**
	 * Constructs a new {@link IridaWorkflowNotDisplayableException} with the given
	 * analysis type.
	 * 
	 * @param analysisType
	 *            The analysis type of the workflow.
	 */
	public IridaWorkflowNotDisplayableException(AnalysisType analysisType) {
		super("Workflows for type " + analysisType + " have been disabled");
	}
	
	/**
	 * Constructs a new {@link IridaWorkflowNotDisplayableException} with the given
	 * workflow name.
	 * 
	 * @param workflowName
	 *            The name of the workflow.
	 */
	public IridaWorkflowNotDisplayableException(String workflowName) {
		super("Workflows with name " + workflowName + " have been disabled");
	}
}
