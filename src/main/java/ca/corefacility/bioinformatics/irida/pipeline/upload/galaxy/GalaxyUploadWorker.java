package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadConnectionException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

import com.sun.jersey.api.client.ClientHandlerException;

/**
 * Class for performing the actual work of uploading files to Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploadWorker implements UploadWorker {

	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyUploadWorker.class);
	
	private static final int NO_TOTAL_SAMPLES = -1;
	private static final int NO_CURRENT_SAMPLE = -1;
	private static final UploadFolderName NO_SAMPLE_NAME = null;

	private GalaxyUploaderAPI galaxyAPI;

	private List<UploadSample> samples;
	private GalaxyProjectName dataLocation;
	private GalaxyAccountEmail userName;

	private UploadResult uploadResult = null;
	private UploadException uploadException = null;

	private int totalSamples = NO_TOTAL_SAMPLES;
	private int currentSample = NO_CURRENT_SAMPLE;
	private UploadFolderName sampleName = NO_SAMPLE_NAME;
	private float proportionComplete = 0.0f;
	private boolean finished = false;

	/**
	 * Constructs a new GalaxyUploadWorker for performing the upload of files to
	 * Galaxy within a new Thread.
	 * 
	 * @param galaxyAPI
	 *            The GalaxyAPI to connect to an instance of Galaxy.
	 * @param samples
	 *            The list of samples to upload.
	 * @param dataLocation
	 *            The location of the Galaxy project to upload into.
	 * @param userName
	 *            The user to upload the data as.
	 */
	public GalaxyUploadWorker(GalaxyUploaderAPI galaxyAPI,
			List<UploadSample> samples, GalaxyProjectName dataLocation,
			GalaxyAccountEmail userName) {
		checkNotNull(galaxyAPI, "galaxyAPI is null");
		checkNotNull(samples, "samples is null");
		checkNotNull(dataLocation, "dataLocation is null");
		checkNotNull(userName, "userName is null");

		this.galaxyAPI = galaxyAPI;
		this.samples = samples;
		this.dataLocation = dataLocation;
		this.userName = userName;

		galaxyAPI.addUploadEventListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			uploadResult = uploadSamples(samples, dataLocation, userName);
		} catch (ConstraintViolationException e) {
			this.uploadException = new UploadException(e);
		} catch (UploadException e) {
			this.uploadException = e;
		} catch (RuntimeException e) {
			this.uploadException = new UploadException(e);
		} catch (Exception e) {
			// handle any remaining exceptions
			this.uploadException = new UploadException(e);
		}

		finished = true;
		proportionComplete = 1.0f;
	}

	/**
	 * Uploads the given list of samples to the passed data location name with
	 * the passed user.
	 * 
	 * @param samples
	 *            The set of samples to upload.
	 * @param dataLocation
	 *            The name of the data location to upload to.
	 * @param userName
	 *            The name of the user who should own the files.
	 * @return An UploadResult containing information about the location of the
	 *         uploaded files.
	 * @throws UploadException
	 *             If an error occurred.
	 * @throws ConstraintViolationException
	 *             If the samples, dataLocation or userName are invalid.
	 */
	private UploadResult uploadSamples(List<UploadSample> samples,
			GalaxyProjectName dataLocation, GalaxyAccountEmail userName)
			throws UploadException, ConstraintViolationException {

		logger.debug("Uploading samples to Galaxy Library " + dataLocation
				+ ", userEmail=" + userName + ", samples=" + samples);

		try {
			return galaxyAPI.uploadSamples(samples, dataLocation, userName);
		} catch (ClientHandlerException e) {
			throw new UploadConnectionException("Could not upload to Galaxy", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized UploadResult getUploadResult() {
		return uploadResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized UploadException getUploadException() {
		return uploadException;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean exceptionOccured() {
		return uploadException != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized float getProportionComplete() {
		return proportionComplete;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTotalSamples() throws NoSuchValueException {
		if (totalSamples == NO_TOTAL_SAMPLES) {
			throw new NoSuchValueException("No total samples");
		} else {
			return totalSamples;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCurrentSample() throws NoSuchValueException {
		if (currentSample == NO_CURRENT_SAMPLE) {
			throw new NoSuchValueException("No current sample");
		} else {
			return currentSample;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadFolderName getSampleName() throws NoSuchValueException {
		if (sampleName == NO_SAMPLE_NAME) {
			throw new NoSuchValueException("No sample name");
		} else {
			return sampleName;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void sampleProgressUpdate(int totalSamples,
			int currentSample, UploadFolderName sampleName) {
		checkNotNull(sampleName, "sampleName is null");
		checkArgument(totalSamples > 0, "totalSamples=" + totalSamples
				+ " is invalid, must be positive");
		checkArgument(currentSample >= 0 && currentSample < totalSamples,
				"currentSample=" + currentSample + " must be in range [0,"
						+ totalSamples + ").");

		this.totalSamples = totalSamples;
		this.currentSample = currentSample;
		this.sampleName = sampleName;
		this.proportionComplete = (float) currentSample / (float) totalSamples;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isFinished() {
		return finished;
	}
}
