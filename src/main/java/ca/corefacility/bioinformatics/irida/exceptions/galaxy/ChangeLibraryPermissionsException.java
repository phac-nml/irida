package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class ChangeLibraryPermissionsException extends UploadException
{
    private static final long serialVersionUID = 4857770214742199369L;

	public ChangeLibraryPermissionsException()
    {
	    super();
    }

	public ChangeLibraryPermissionsException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace)
    {
	    super(message, cause, enableSuppression, writableStackTrace);
    }

	public ChangeLibraryPermissionsException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public ChangeLibraryPermissionsException(String message)
    {
	    super(message);
    }

	public ChangeLibraryPermissionsException(Throwable cause)
    {
	    super(cause);
    }
}
