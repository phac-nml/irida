package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class NoGalaxyContentFoundException extends UploadException
{
    private static final long serialVersionUID = -4971585560286283917L;

	public NoGalaxyContentFoundException()
    {
	    super();
    }

	public NoGalaxyContentFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace)
    {
	    super(message, cause, enableSuppression, writableStackTrace);
    }

	public NoGalaxyContentFoundException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public NoGalaxyContentFoundException(String message)
    {
	    super(message);
    }

	public NoGalaxyContentFoundException(Throwable cause)
    {
	    super(cause);
    }
}
