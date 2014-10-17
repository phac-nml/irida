package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;

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