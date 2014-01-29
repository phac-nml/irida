package ca.corefacility.bioinformatics.irida.model.upload;

import java.net.URL;

public interface UploadResult
{
	/**
	 * Gets the URL the uploaded data is located in.
	 * @return  The URL for the uploaded data.
	 */
	public abstract URL getDataLocation();

	/**
	 * Gets the name under which the data was uploaded.
	 * @return  The name under which the data was uploaded.
	 */
	public abstract String getLocationName();
}