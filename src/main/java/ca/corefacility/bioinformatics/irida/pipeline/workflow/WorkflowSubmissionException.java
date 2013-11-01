package ca.corefacility.bioinformatics.irida.pipeline.workflow;

public class WorkflowSubmissionException extends Exception
{
	public WorkflowSubmissionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WorkflowSubmissionException(String message)
	{
		super(message);
	}

	public WorkflowSubmissionException(Throwable cause)
	{
		super(cause);
	}

	public WorkflowSubmissionException(Exception e)
	{
		initCause(e);
	}
}
