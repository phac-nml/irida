package ca.corefacility.bioinformatics.irida.model.upload;

import java.net.URL;

/**
 * An interface for accessing information about the result of an upload.
 * 
 * 
 */
public interface UploadResult {
	/**
	 * Gets the URL the uploaded data is located in.
	 * 
	 * @return The URL for the uploaded data.
	 */
	public URL getDataLocation();

	/**
	 * Gets the owner of this upload result if a new data location was created.
	 * 
	 * @return The owner of a new data location, or null if no new data location
	 *         was created.
	 */
	public UploaderAccountName ownerOfNewLocation();

	/**
	 * Whether or not a new data location was created.
	 * 
	 * @return True if a new data location was created, false otherwise.
	 */
	public boolean newLocationCreated();

	/**
	 * Gets the name under which the data was uploaded.
	 * 
	 * @return The name under which the data was uploaded.
	 */
	public UploadProjectName getLocationName();
}