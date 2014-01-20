package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

public class LibraryUploadException extends Exception
{
    private static final long serialVersionUID = -5915057695904796185L;

	public LibraryUploadException()
	{
		super();
	}

	public LibraryUploadException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LibraryUploadException(String message)
	{
		super(message);
	}

	public LibraryUploadException(Throwable cause)
	{
		super(cause);
	}
}
