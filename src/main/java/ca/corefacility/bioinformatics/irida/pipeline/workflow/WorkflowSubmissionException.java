package ca.corefacility.bioinformatics.irida.pipeline.workflow;

public class WorkflowSubmissionException extends Exception
{
	public WorkflowSubmissionException(Exception e)
	{
		initCause(e);
	}
}
