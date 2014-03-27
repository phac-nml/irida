package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.SampleProgressListener;

/**
 * SampleProgressListener class which keeps track of all progress events sent to it.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class SampleProgressListenerExample implements SampleProgressListener {

	private List<ProgressUpdate> progressUpdates;
	
	/**
	 * Creates a new SampleProgressListenerTest and sets up the tracking of events in a list.
	 */
	public SampleProgressListenerExample() {
		progressUpdates = new ArrayList<ProgressUpdate>();
	}
	
	@Override
	public void progressUpdate(int totalSamples, int currentSample,
			UploadFolderName sampleName) {
		progressUpdates.add(new ProgressUpdate(totalSamples, currentSample, sampleName));
	}
	
	/**
	 * Gets the recorded events.
	 * @return  A list containing the recorded events.
	 */
	public List<ProgressUpdate> getProgressUpdates() {
		return progressUpdates;
	}
}
