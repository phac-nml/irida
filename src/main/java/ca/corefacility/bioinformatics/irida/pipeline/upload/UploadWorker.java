package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;

/**
 * An interface for a class used to perform the actual uploading of files to a remote site.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface UploadWorker extends Runnable {
	
	/**
	 * Returns the final upload result when the worker is finished running.
	 * @return The final upload result when the worker is finished running, or null if no such result.
	 */
	public UploadResult getUploadResult();
	
	/**
	 * Returns an UploadException if any such exception was thrown.
	 * @return  An UploadException if one was thrown, or null if no exceptions where thrown.
	 */
	public UploadException getUploadException();
	
	/**
	 * Gets the proportion of this upload that is complete.
	 * @return  A number from [0,1] giving the proportion of this upload that is complete.
	 */
	public float getProportionComplete();
	
	/**
	 * True if an exception occured while uploading, false otherwise.
	 * @return True if an exception occured while uploading, false otherwise.
	 */
	public boolean exceptionOccured();
	
	/**
	 * Adds a new UploadEventListener to listen for upload events.
	 * @param eventListener  The UploadEventListener to add.
	 */
	public void addUploadEventListener(UploadEventListener eventListener);
}
