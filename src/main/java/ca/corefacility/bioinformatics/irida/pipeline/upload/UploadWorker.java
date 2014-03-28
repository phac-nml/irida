package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;

/**
 * An interface for a class used to perform the actual uploading of files to a remote site.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface UploadWorker extends Runnable {
	
	/**
	 * Interface for definining code to run on events when uploading files.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public interface UploadEventListener {
		
		/**
		 * Run when upload has finished.
		 * @param result  The resulting object and status of the upload.
		 */
		public void finish(UploadResult result);
		
		/**
		 * Run when an upload exception is raised.
		 * @param uploadException  The exception raised.
		 */
		public void exception(UploadException uploadException);
		
		/**
		 * Triggered whenever a new sample is being uploaded.
		 * @param totalSamples  The total number of samples to upload.
		 * @param currentSample  The current sample completed.
		 * @param sampleName  The name of the current sample completed.
		 */
		public void sampleProgressUpdate(int totalSamples, int currentSample, UploadFolderName sampleName);
	}
	
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
