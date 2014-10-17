package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.LinkedList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadEventListener;

/**
 * Class which keeps track of events it recieves for testing.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class UploadEventListenerTracker implements UploadEventListener {

	private List<ProgressUpdate> progressUpdates = new LinkedList<ProgressUpdate>();
	private List<UploadResult> results = new LinkedList<UploadResult>();
	private List<UploadException> exceptions = new LinkedList<UploadException>();
	
	@Override
	public void finish(UploadResult result) {
		results.add(result);
	}

	@Override
	public void exception(UploadException uploadException) {
		exceptions.add(uploadException);
	}

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

	/**
	 * Gets the list of upload results recorded.
	 * @return  The list of upload results recorded.
	 */
	public List<UploadResult> getResults() {
		return results;
	}

	/**
	 * Gets the list of exceptions recorded.
	 * @return  The list of exceptions recorded.
	 */
	public List<UploadException> getExceptions() {
		return exceptions;
	}
}
