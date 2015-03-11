package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;

/**
 * An uploader for deciding whether or not to upload sample files into Galaxy
 * 
 * 
 */
public class GalaxyUploader implements Uploader<GalaxyProjectName, GalaxyAccountEmail> {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyUploader.class);

	private GalaxyConnector galaxyConnector = null;
	
	private DataStorage dataStorage = DataStorage.REMOTE;

	/**
	 * Builds a new GalaxyUploader unconnected to any Galaxy instance.
	 */
	public GalaxyUploader() {
	}
	
	/**
	 * Builds a new GalaxyUploader with the given GalaxyConnector.
	 * 
	 * @param galaxyConnector
	 *            The GalaxyConnector to build the uploader with.
	 */
	public GalaxyUploader(GalaxyConnector galaxyConnector) {
		checkNotNull(galaxyConnector, "galaxyConnector is not null");

		this.galaxyConnector = galaxyConnector;
	}

	/**
	 * Connects this uploader to a GalaxyConnector.
	 * 
	 * @param galaxyConnector  A GalaxyConnector used to generate connections to a Galaxy instance.
	 * @throws ConstraintViolationException
	 *             If one of the parameters fails it's constraints (assumes this
	 *             is managed by Spring).
	 * @throws GalaxyConnectException
	 *             If an error occured when connecting to Galaxy.
	 */
	public void connectToGalaxy(GalaxyConnector galaxyConnector)
			throws ConstraintViolationException, GalaxyConnectException {
		checkNotNull(galaxyConnector, "galaxyURL is null");

		this.galaxyConnector = galaxyConnector;

		logger.info("Setup connection to Galaxy " + galaxyConnector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDataLocationAttached() {
		return galaxyConnector != null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDataLocationConnected() {
		return isDataLocationAttached() &&
				galaxyConnector.isConnected();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataStorage(DataStorage dataStorage) {
		this.dataStorage = dataStorage;
		if (galaxyConnector != null) {
			galaxyConnector.setDataStorage(dataStorage);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataStorage getDataStorage() {
		return dataStorage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getUrl() {
		if (galaxyConnector != null) {
			return galaxyConnector.getURL();
		} else {
			throw new RuntimeException(
					"Uploader is not connected to any instance of Galaxy");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadWorker uploadSamples(@Valid List<UploadSample> samples,
			@Valid GalaxyProjectName dataLocation,
			@Valid GalaxyAccountEmail userName) throws ConstraintViolationException {

		try {
			
			if (isDataLocationConnected()) {
				GalaxyUploaderAPI galaxyAPI = galaxyConnector.createGalaxyConnection();
				return new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
			} else {
				throw new RuntimeException("Uploader is not connected to any instance of Galaxy");
			}
			
		} catch (GalaxyConnectException e) {
			throw new RuntimeException("Uploader is not connected to any instance of Galaxy", e);
		}
	}
}
