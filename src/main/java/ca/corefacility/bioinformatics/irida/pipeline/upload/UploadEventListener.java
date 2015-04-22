package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;

/**
 * Interface for definining code to run on events when uploading files.
 *
 */
public interface UploadEventListener {
	
	/**
	 * Triggered whenever a new sample is being uploaded.
	 * @param totalSamples  The total number of samples to upload.
	 * @param currentSample  The current sample completed.
	 * @param sampleName  The name of the current sample completed.
	 */
	public void sampleProgressUpdate(int totalSamples, int currentSample, UploadFolderName sampleName);
}