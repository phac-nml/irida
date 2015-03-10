package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;

/**
 * An interface for a class used to perform the actual uploading of files to a
 * remote site.
 * 
 *
 */
public interface UploadWorker extends Runnable, UploadEventListener {

	/**
	 * Returns the final upload result when the worker is finished running.
	 * 
	 * @return The final upload result when the worker is finished running, or
	 *         null if no such result.
	 */
	public UploadResult getUploadResult();

	/**
	 * Returns an UploadException if any such exception was thrown.
	 * 
	 * @return An UploadException if one was thrown, or null if no exceptions
	 *         where thrown.
	 */
	public UploadException getUploadException();

	/**
	 * Gets the proportion of this upload that is complete.
	 * 
	 * @return A number from [0,1] giving the proportion of this upload that is
	 *         complete.
	 */
	public float getProportionComplete();

	/**
	 * True if an exception occured while uploading, false otherwise.
	 * 
	 * @return True if an exception occured while uploading, false otherwise.
	 */
	public boolean exceptionOccured();

	/**
	 * True if the upload is finished (or an execption occured), false
	 * otherwise.
	 * 
	 * @return True if the upload is finished, or false otherwise.
	 */
	public boolean isFinished();

	/**
	 * Gets the total samples being uploaded by this worker.
	 * 
	 * @return The total number of samples being uploaded by this worker.
	 * @throws NoSuchValueException
	 *             If the total samples value is not currently known.
	 */
	public int getTotalSamples() throws NoSuchValueException;

	/**
	 * Gets the current sample being uploaded by this worker.
	 * 
	 * @return The current sample being uploaded by this worker.
	 * @throws NoSuchValueException
	 *             If the current samples value is not currently known.
	 */
	public int getCurrentSample() throws NoSuchValueException;

	/**
	 * Gets the name of the sample currently being uploaded.
	 * 
	 * @return The name of the sample currently being uploaded.
	 * @throws NoSuchValueException
	 *             If the sample name is not currently known.
	 */
	public UploadFolderName getSampleName() throws NoSuchValueException;
}
