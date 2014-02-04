package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

public class GalaxyUserNotFoundException extends UploadException
{
	private static final long serialVersionUID = 2496168579584339258L;

	public GalaxyUserNotFoundException()
	{
		super();
	}

	public GalaxyUserNotFoundException(String message, Throwable cause,
	        boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GalaxyUserNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GalaxyUserNotFoundException(String message)
	{
		super(message);
	}

	public GalaxyUserNotFoundException(Throwable cause)
	{
		super(cause);
	}
}
