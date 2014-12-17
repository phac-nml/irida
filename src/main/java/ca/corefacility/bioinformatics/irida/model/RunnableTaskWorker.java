package ca.corefacility.bioinformatics.irida.model;

/**
 * Defines a task to be executed in another thread to perform tasks.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RunnableTaskWorker extends Runnable {
	/**
	 * True if an exception occurred while uploading, false otherwise.
	 * 
	 * @return True if an exception occurred while uploading, false otherwise.
	 */
	public boolean exceptionOccured();

	/**
	 * True if the upload is finished (or an exception occurred), false
	 * otherwise.
	 * 
	 * @return True if the upload is finished, or false otherwise.
	 */
	public boolean isFinished();
}
