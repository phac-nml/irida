package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

public class CreateLibraryException extends Exception
{
    private static final long serialVersionUID = -5461414386915764417L;

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
