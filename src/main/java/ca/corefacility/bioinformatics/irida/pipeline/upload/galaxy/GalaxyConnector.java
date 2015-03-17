package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.net.URL;

import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

/**
 * Used to create independent connections/objects to Galaxy.
 *
 */
public class GalaxyConnector {

	private URL galaxyURL;
	private GalaxyAccountEmail adminEmail;
	private String adminAPIKey;
	
	private DataStorage dataStorage = DataStorage.REMOTE;
	
	/**
	 * Builds a new GalaxyConnector with the given information.
	 * @param galaxyURL  The URL for the Galaxy instance.
	 * @param adminEmail  The administrators email address for this Galaxy instance.
	 * @param adminAPIKey  The API Key for this Galaxy instance.
	 */
	public GalaxyConnector(URL galaxyURL, GalaxyAccountEmail adminEmail, String adminAPIKey) {
		checkNotNull(galaxyURL, "galaxyURL is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(adminAPIKey, "adminAPIKey is null");
		
		this.galaxyURL = galaxyURL;
		this.adminEmail = adminEmail;
		this.adminAPIKey = adminAPIKey;
	}
	
	/**
	 * Whether or not the connection to Galaxy is properly working.
	 * 
	 * @return True if there is a proper connection to Galaxy, false otherwise.
	 */
	public boolean isConnected() {
		try {
			return createGalaxyConnection().isConnected();
		} catch (ConstraintViolationException | GalaxyConnectException e) {
			return false;
		}
	}

	/**
	 * Builds a new GalaxyAPI for interacting with Galaxy.
	 * @return  A new GalaxyAPI defining a connection to Galaxy.
	 * @throws ConstraintViolationException  If a constraint was violated.
	 * @throws GalaxyConnectException  If there was an issue connecting to Galaxy.
	 */
	public GalaxyUploaderAPI createGalaxyConnection() throws ConstraintViolationException, GalaxyConnectException {
		GalaxyUploaderAPI galaxyAPI = new GalaxyUploaderAPI(galaxyURL, adminEmail, adminAPIKey);
		galaxyAPI.setDataStorage(dataStorage);
		return galaxyAPI;
	}

	/**
	 * Sets up the type of data storage for this Galaxy connection.
	 * 
	 * @param dataStorage
	 *            How the data should be stored on the remote site.
	 */
	public void setDataStorage(DataStorage dataStorage) {
		checkNotNull(dataStorage, "dataStore is null");
		
		this.dataStorage = dataStorage;
	}

	/**
	 * Gets the current DataStorage method.
	 * 
	 * @return The DataStorage currently in use.
	 */
	public DataStorage getDataStorage() {
		return dataStorage;
	}
	
	/**
	 * Gets the URL of Galaxy.
	 * @return  The URL of galaxy.
	 */
	public URL getURL() {
		return galaxyURL;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GalaxyConnector [galaxyURL=" + galaxyURL + ", adminEmail="
				+ adminEmail + ", adminAPIKey=" + adminAPIKey
				+ ", dataStorage=" + dataStorage + "]";
	}
}
