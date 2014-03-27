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
	 * Default class for running when upload is finished.
	 */
	public static UploadFinishedRunner DEFAULT_FINISHED = new UploadFinishedRunner(){
		@Override
		public void finish(UploadResult result) {
		}
	};
	
	/**
	 * Default class for running when an upload exception has occured.
	 */
	public static UploadExceptionRunner DEFAULT_EXCEPTION = new UploadExceptionRunner(){
		@Override
		public void exception(UploadException uploadException) {
		}
	};

	/**
	 * Interface for definining code to run when upload is finished.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public interface UploadFinishedRunner {
		
		/**
		 * Run when upload has finished.
		 * @param result  The resulting object and status of the upload.
		 */
		public void finish(UploadResult result);
	}
	
	/**
	 * Interface for defining code to run when an upload exception is raised.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public interface UploadExceptionRunner {
		
		/**
		 * Run when an upload exception is raised.
		 * @param uploadException  The exception raised.
		 */
		public void exception(UploadException uploadException);
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
	 * Attaches code to run when the upload has finished running.
	 * @param finishedRunner  The UploadFinishedRunner with code to be run when the upload is finished.
	 */
	public void runOnUploadFinished(UploadFinishedRunner finishedRunner);
	
	/**
	 * Attaches code to run when an upload exception has been raised.
	 * @param finishedRunner  The UploadExceptionRunner with code to be run when an upload exception is raised.
	 */
	public void runOnUploadException(UploadExceptionRunner exceptionRunner);
	
	/**
	 * Set a SampleProgressListener to this UploadWorker to recieve messages about upload progress.
	 * @param progressListener  The progressListener to set.
	 */
	public void setSampleProgressListener(SampleProgressListener progressListener);
}
