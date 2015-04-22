package ca.corefacility.bioinformatics.irida.pipeline.upload;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;

/**
 * Defines an interface for a class used to send data from the archive into a remote site (e.g. Galaxy).
 *
 * @param <ProjectName>  The name of the project to upload into.
 * @param <AccountName>  The name of the user account to make an owner of a new data location.
 * 
 */
public interface Uploader<ProjectName extends UploadProjectName, AccountName extends UploaderAccountName> {
	/**
	 * Defines the location of data to upload on a filesystem.
	 * 
	 * 
	 */
	public enum DataStorage {
		/**
		 * LOCAL implies the data to upload is on the same filesystem as the
		 * remote site (e.g. NFS shared filesystem).
		 */
		LOCAL,

		/**
		 * REMOTE implies the data to upload is on a separate filesystem as the
		 * remote site (e.g. no NFS shared filesystem, so requires uploading a
		 * copy of the files).
		 */
		REMOTE
	}

	/**
	 * Creates an UploadWorker object to be used to upload the given list of samples
	 * to the passed data location name with the passed user.
	 * 
	 * @param samples
	 *            The set of samples to upload.
	 * @param dataLocation
	 *            The name of the data location to upload to.
	 * @param userName
	 *            The name of the user who should own the files.
	 * @return An UploadWorker object which can be used to start the upload.
	 * @throws ConstraintViolationException
	 *             If the samples, dataLocation or userName are invalid.
	 */
	public UploadWorker uploadSamples(
			@Valid List<UploadSample> samples,
			@Valid ProjectName dataLocation,
			@Valid AccountName userName) throws ConstraintViolationException;

	/**
	 * Whether or not this uploader is attached to a data location.
	 * 
	 * @return True if this uploader is attached to a data location
	 *         instance, false otherwise.
	 */
	public boolean isDataLocationAttached();
	
	/**
	 * Whether or not the connection to a data location is properly working.
	 * 
	 * @return True if this uploader is connected to a data location
	 *         instance, false otherwise.
	 */
	public boolean isDataLocationConnected();

	/**
	 * Sets up the type of data storage for this uploader.
	 * 
	 * @param dataStorage
	 *            How the data should be stored on the remote site.
	 */
	public void setDataStorage(DataStorage dataStorage);

	/**
	 * Gets the current DataStorage method.
	 * 
	 * @return The DataStorage currently in use.
	 */
	public DataStorage getDataStorage();

	/**
	 * Gets the URL of the connected data location instance.
	 * 
	 * @return The URL of the connected data location instance
	 */
	public URL getUrl();
}