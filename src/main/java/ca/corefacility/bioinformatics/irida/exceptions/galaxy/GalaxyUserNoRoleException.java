package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class GalaxyUserNoRoleException extends UploadException
{
    private static final long serialVersionUID = 7783940011662578668L;

	public GalaxyUserNoRoleException()
    {
	    super();
    }

	public GalaxyUserNoRoleException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace)
    {
	    super(message, cause, enableSuppression, writableStackTrace);
    }

	public GalaxyUserNoRoleException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public GalaxyUserNoRoleException(String message)
    {
	    super(message);
    }

	public GalaxyUserNoRoleException(Throwable cause)
    {
	    super(cause);
    }
}
