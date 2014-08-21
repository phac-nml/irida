package ca.corefacility.bioinformatics.irida.config.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

import com.google.common.collect.ImmutableMap;

@Configuration
@Profile({ "dev", "prod", "it" })
public class ExecutionManagerConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(ExecutionManagerConfig.class);

	private static final String URL_PROPERTY = "galaxy.url";
	private static final String API_KEY_PROPERTY = "galaxy.admin.apiKey";
	private static final String ADMIN_EMAIL_PROPERTY = "galaxy.admin.email";
	private static final String DATA_STORAGE_PROPERTY = "galaxy.dataStorage";

	private static final Map<String, Uploader.DataStorage> VALID_STORAGE = ImmutableMap
			.<String, Uploader.DataStorage> builder()
			.put("remote", Uploader.DataStorage.REMOTE)
			.put("local", Uploader.DataStorage.LOCAL).build();
	
	private static final Uploader.DataStorage DEFAULT_DATA_STORAGE = Uploader.DataStorage.REMOTE;

	@Autowired
	private Environment environment;

	@Autowired
	private Validator validator;

	/**
	 * Builds a new GalaxyUploader for uploading files to Galaxy.
	 * @return  A new GalaxyUploader object.
	 */
	@Bean
	public GalaxyUploader galaxyUploader() {
		GalaxyUploader galaxyUploader = new GalaxyUploader();

		try {
			GalaxyConnector galaxyConnector;
			
			ExecutionManagerGalaxy executionManager = buildExecutionManager(URL_PROPERTY, API_KEY_PROPERTY,
					ADMIN_EMAIL_PROPERTY, DATA_STORAGE_PROPERTY);

			galaxyConnector = new GalaxyConnector(executionManager.getLocation(),
					executionManager.getAccountEmail(),
					executionManager.getAdminAPIKey());
			galaxyConnector.setDataStorage(executionManager.getDataStorage());
			
			galaxyUploader.connectToGalaxy(galaxyConnector);

		} catch (ExecutionManagerConfigurationException e) {
			logger.error("Could not build ExecutionManagerGalaxy", e);
		} catch (ConstraintViolationException e) {
			logger.error("Exception attempting to connect to Galaxy", e);
		} catch (GalaxyConnectException e) {
			logger.error("Exception attempting to connect to Galaxy", e);
		}

		return galaxyUploader;
	}
	
	/**
	 * Builds a new ExecutionManagerGalaxy given the following environment properties.
	 * @param urlProperty The property defining the URL to Galaxy.
	 * @param apiKeyProperty  The property defining the API key to Galaxy.
	 * @param emailProperty  The property defining the account email in Galaxy.
	 * @param dataStorageProperty  The property defning the data storage method.
	 * @return  An ExecutionManagerGalaxy.
	 * @throws ExecutionManagerConfigurationException If there was an issue building an ExecutionManagerGalaxy
	 * 	from the given properties.
	 */
	private ExecutionManagerGalaxy buildExecutionManager(String urlProperty, String apiKeyProperty,
			String emailProperty, String dataStorageProperty) throws ExecutionManagerConfigurationException {

		URL galaxyURL = getGalaxyURL(urlProperty);
		GalaxyAccountEmail galaxyEmail = getGalaxyEmail(emailProperty);
		String apiKey = getAPIKey(apiKeyProperty);
		Uploader.DataStorage dataStorage = getDataStorage(dataStorageProperty);

		return new ExecutionManagerGalaxy(galaxyURL, apiKey, galaxyEmail, dataStorage);
	}
	
	private GalaxyAccountEmail getGalaxyEmail(String emailProperty) throws ExecutionManagerConfigurationException {
		String galaxyEmailString = environment.getProperty(emailProperty);
		GalaxyAccountEmail galaxyEmail = new GalaxyAccountEmail(galaxyEmailString);
		
		Set<ConstraintViolation<GalaxyAccountEmail>> violations = validator
				.validate(galaxyEmail);

		if (!violations.isEmpty()) {
			throw new ExecutionManagerConfigurationException(emailProperty, 
					new ConstraintViolationException(violations));
		}
		
		return galaxyEmail;
	}
	
	private String getAPIKey(String apiKeyProperty) throws ExecutionManagerConfigurationException {
		String apiKey = environment.getProperty(apiKeyProperty);
		
		if (apiKey == null) {
			throw new ExecutionManagerConfigurationException(apiKeyProperty);
		} else {
			return apiKey;
		}
	}
	
	private URL getGalaxyURL(String urlProperty) throws ExecutionManagerConfigurationException {
		String galaxyURLString = environment.getProperty(urlProperty);

		try {
			if (galaxyURLString == null) {
				throw new ExecutionManagerConfigurationException(urlProperty);
			} else {
				return new URL(galaxyURLString);
			}
		} catch (MalformedURLException e) {
			throw new ExecutionManagerConfigurationException(urlProperty, e);
		}
	}
	
	private Uploader.DataStorage getDataStorage(String dataStorageProperty) {
		String dataStorageString = environment.getProperty(dataStorageProperty);
		Uploader.DataStorage dataStorage = VALID_STORAGE.get(dataStorageString.toLowerCase());
		
		if (dataStorage == null) {
			dataStorage = DEFAULT_DATA_STORAGE;
			
			logger.warn("Invalid configuration property \""
					+ dataStorageProperty
					+ "="
					+ dataStorageString
					+ "\" must be one of "
					+ VALID_STORAGE.keySet()
					+ ": using default ("
					+ DEFAULT_DATA_STORAGE.toString()
							.toLowerCase() + ")");
		}
		
		return dataStorage;
	}
}
