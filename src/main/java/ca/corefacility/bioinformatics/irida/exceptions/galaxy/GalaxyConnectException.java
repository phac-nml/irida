package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class GalaxyConnectException extends UploadException
{
	private static final long serialVersionUID = 2395605818272983294L;

	public GalaxyConnectException()
	{
		super();
	}

	public GalaxyConnectException(String message, Throwable cause,
	        boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GalaxyConnectException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GalaxyConnectException(String message)
	{
		super(message);
	}

	public GalaxyConnectException(Throwable cause)
	{
		super(cause);
	}
}
