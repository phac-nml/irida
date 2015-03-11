package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;

/**
 * Class for storing together any progress update messages so I can verify
 * that they got sent later.
 */
public class ProgressUpdate {

	private int totalSamples;
	private int currentSample;
	private UploadFolderName sampleName;
	
	/**
	 * Builds new progress update object for storing progress update messages.
	 * @param totalSamples  The total number of samples from the event.
	 * @param currentSample  The current sample in the event.
	 * @param sampleName  The name of the sample in the event.
	 */
	public ProgressUpdate(int totalSamples, int currentSample,
			UploadFolderName sampleName) {
		super();
		this.totalSamples = totalSamples;
		this.currentSample = currentSample;
		this.sampleName = sampleName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(totalSamples, currentSample, sampleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgressUpdate other = (ProgressUpdate) obj;
		
		return Objects.equals(this.totalSamples, other.totalSamples) &&
				Objects.equals(this.currentSample, other.currentSample)&&
				Objects.equals(this.sampleName, other.sampleName);
	}
}
