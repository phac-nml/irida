package ca.corefacility.bioinformatics.irida.pipeline.workflow;

public interface WorkflowSubmitter
{
	/**
	 * Submits the passed workflow to a workflow engine (translating to an executable workflow plan).
	 * @param workflow  The Workflow to submit.  
	 * @return True if successfully submitted, false otherwise.
	 * @throws WorkflowSubmissionException if there was an error submitting a workflow.
	 */
	public boolean submitWorkflow(Workflow workflow) throws WorkflowSubmissionException;
}
