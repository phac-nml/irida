package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowIdentifier;

/**
 * Exception that gets thrown if a workflow is not found.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowNotFoundException extends Exception {

	private static final long serialVersionUID = 5002643601663546169L;

	/**
	 * Constructs a new {@link IridaWorkflowNotFoundException} with the given
	 * workflow identifier.
	 * 
	 * @param workflowIdentifier
	 *            The identifier of the workflow.
	 */
	public IridaWorkflowNotFoundException(IridaWorkflowIdentifier workflowIdentifier) {
		super("No workflow found for " + workflowIdentifier);
	}
}
