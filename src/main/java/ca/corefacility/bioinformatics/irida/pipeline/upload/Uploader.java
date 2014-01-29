package ca.corefacility.bioinformatics.irida.pipeline.upload;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;

public interface Uploader
{
	/**
	 * Uploads the given list of samples to the passed data location name with the passed user.
	 * @param samples  The set of samples to upload.
	 * @param dataLocation  The name of the data location to upload to.
	 * @param userName  The name of the user who should own the files.
	 * @return An UploadResult containing information about the location of the uploaded files, or null
	 * 	if an error occurred.
	 * @throws UploadException  If an error occurred.
	 * @throws ConstraintViolationException If the samples, dataLocation or userName are invalid.
	 */
	public abstract UploadResult uploadSamples(
	        @Valid List<GalaxySample> samples,
	        @Valid UploadObjectName dataLocation,
	        @Valid UploaderAccountName userName) throws UploadException,
	        ConstraintViolationException;

	/**
	 * @return  Whether or not this uploader is connected to a data location instance.
	 */
	public abstract boolean isConnected();

	/**
	 * Sets a parameter to link up files within the data location (assumes files exist on same filesystem),
	 *  or copy the uploaded files.
	 * @param linkFiles  True if files should be linked, false otherwise.
	 */
	public abstract void setLinkUploadedFiles(boolean linkFiles);

	/**
	 * Gets the URL of the connected data location instance.
	 * @return  The URL of the connected data location instance
	 */
	public abstract URL getUrl();

}