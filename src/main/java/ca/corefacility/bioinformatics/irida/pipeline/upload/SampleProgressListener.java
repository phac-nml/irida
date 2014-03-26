package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;

/**
 * Interface for listening to uploaded sample progress events.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface SampleProgressListener {
	
	public static SampleProgressListener DEFAULT_LISTENER = new SampleProgressListener(){
		@Override
		public void progressUpdate(int totalSamples, int currentSample, UploadFolderName sampleName) {
		}
	};
	
	/**
	 * Triggered whenever a new sample is being uploaded.
	 * @param totalSamples  The total number of samples to upload.
	 * @param currentSample  The current sample completed.
	 * @param sampleName  The name of the current sample completed.
	 */
	public void progressUpdate(int totalSamples, int currentSample, UploadFolderName sampleName); 
}