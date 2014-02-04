package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class NoLibraryFoundException extends UploadException
{
	private static final long serialVersionUID = -2968750017497563652L;

	public NoLibraryFoundException()
	{
		super();
	}

	public NoLibraryFoundException(String message, Throwable cause,
	        boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoLibraryFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NoLibraryFoundException(String message)
	{
		super(message);
	}

	public NoLibraryFoundException(Throwable cause)
	{
		super(cause);
	}
}
