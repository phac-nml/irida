package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

public class CreateLibraryException extends Exception
{
	public CreateLibraryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CreateLibraryException(String message)
	{
		super(message);
	}

	public CreateLibraryException(Throwable cause)
	{
		super(cause);
	}
}
