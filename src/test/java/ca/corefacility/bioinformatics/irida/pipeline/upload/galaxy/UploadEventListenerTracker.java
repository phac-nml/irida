package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.LinkedList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadEventListener;

/**
 * Class which keeps track of events it recieves for testing.
 *
 */
public class UploadEventListenerTracker implements UploadEventListener {

	private List<ProgressUpdate> progressUpdates = new LinkedList<ProgressUpdate>();

	@Override
	public void sampleProgressUpdate(int totalSamples, int currentSample,
			UploadFolderName sampleName) {
		progressUpdates.add(new ProgressUpdate(totalSamples, currentSample, sampleName));
	}

	/**
	 * Gets the list of progress updates recorded.
	 * @return  The list of progress updates recorded.
	 */
	public List<ProgressUpdate> getProgressUpdates() {
		return progressUpdates;
	}
}
