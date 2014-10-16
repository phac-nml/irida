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
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

import com.google.common.collect.ImmutableMap;

/**
 * Configuration for connections to an ExecutionManager in IRIDA.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile({ "dev", "prod", "it" })
public class ExecutionManagerConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(ExecutionManagerConfig.class);
	
	/**
	 * Property names for a Galaxy instance to execution jobs on.
	 */
	private static final String URL_EXECUTION_PROPERTY = "galaxy.execution.url";
	private static final String API_KEY_EXECUTION_PROPERTY = "galaxy.execution.apiKey";
	private static final String EMAIL_EXECUTION_PROPERTY = "galaxy.execution.email";
	private static final String DATA_STORAGE_EXECUTION_PROPERTY = "galaxy.execution.dataStorage";

	/**
	 * Property names for a Galaxy instance to upload files into.
	 */
	private static final String URL_UPLODER_PROPERTY = "galaxy.uploader.url";
	private static final String API_KEY_UPLOADER_PROPERTY = "galaxy.uploader.admin.apiKey";
	private static final String ADMIN_EMAIL_UPLOADER_PROPERTY = "galaxy.uploader.admin.email";
	private static final String DATA_STORAGE_UPLOADER_PROPERTY = "galaxy.uploader.dataStorage";

	private static final Map<String, Uploader.DataStorage> VALID_STORAGE = ImmutableMap.of(
					"remote", Uploader.DataStorage.REMOTE,
					"local", Uploader.DataStorage.LOCAL);
	
	private static final Uploader.DataStorage DEFAULT_DATA_STORAGE = Uploader.DataStorage.REMOTE;

	@Autowired
	private Environment environment;

	@Autowired
	private Validator validator;
	
	/**
	 * Builds a new ExecutionManagerGalaxy from the given properties.
	 * @return  An ExecutionManagerGalaxy.
	 * @throws NoExecutionManagerException If no execution manager is configured.
	 */
	@Lazy
	@Bean
	public ExecutionManagerGalaxy executionManager() throws ExecutionManagerConfigurationException {		
		return buildExecutionManager(URL_EXECUTION_PROPERTY, API_KEY_EXECUTION_PROPERTY,
				EMAIL_EXECUTION_PROPERTY, DATA_STORAGE_EXECUTION_PROPERTY);
	}

	/**
	 * Builds a new GalaxyUploader for uploading files to Galaxy.
	 * @return  A new GalaxyUploader object.
	 */
	@Bean
	public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader() {
		GalaxyUploader galaxyUploader = new GalaxyUploader();

		try {
			GalaxyConnector galaxyConnector;
			
			ExecutionManagerGalaxy executionManager = buildExecutionManager(URL_UPLODER_PROPERTY, API_KEY_UPLOADER_PROPERTY,
					ADMIN_EMAIL_UPLOADER_PROPERTY, DATA_STORAGE_UPLOADER_PROPERTY);

			galaxyConnector = new GalaxyConnector(executionManager.getLocation(),
					executionManager.getAccountEmail(),
					executionManager.getAPIKey());
			galaxyConnector.setDataStorage(executionManager.getDataStorage());
			
			galaxyUploader.connectToGalaxy(galaxyConnector);

		} catch (ExecutionManagerConfigurationException e) {
			logger.error("Could not build ExecutionManagerGalaxy: " + e.getMessage());
		} catch (ConstraintViolationException e) {
			logger.error("Could not build ExecutionManagerGalaxy: " + e.getMessage());
		} catch (GalaxyConnectException e) {
			logger.error("Exception attempting to connect to Galaxy: " + e.getMessage());
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
	
	/**
	 * Gets and validates a GalaxyAccountEmail from the given property.
	 * @param emailProperty  The property to find the email address.
	 * @return  A valid GalaxyAccountEmail.
	 * @throws ExecutionManagerConfigurationException  If the properties value was invalid.
	 */
	private GalaxyAccountEmail getGalaxyEmail(String emailProperty) throws ExecutionManagerConfigurationException {
		String galaxyEmailString = environment.getProperty(emailProperty);
		GalaxyAccountEmail galaxyEmail = new GalaxyAccountEmail(galaxyEmailString);
		
		Set<ConstraintViolation<GalaxyAccountEmail>> violations = validator
				.validate(galaxyEmail);

		if (!violations.isEmpty()) {
			throw new ExecutionManagerConfigurationException("Invalid email address", emailProperty, 
					new ConstraintViolationException(violations));
		}
		
		return galaxyEmail;
	}
	
	/**
	 * Gets and validates a Galaxy API key from the given property.
	 * @param apiKeyProperty  The API key property to get.
	 * @return  A API key for Galaxy.
	 * @throws ExecutionManagerConfigurationException  If the given properties value was invalid.
	 */
	private String getAPIKey(String apiKeyProperty) throws ExecutionManagerConfigurationException {
		String apiKey = environment.getProperty(apiKeyProperty);
		
		if (apiKey == null) {
			throw new ExecutionManagerConfigurationException("Missing apiKey",apiKeyProperty);
		} else {
			return apiKey;
		}
	}
	
	/**
	 * Gets and validates the given property for a Galaxy url.
	 * @param urlProperty  The property with the Galaxy URL.
	 * @return  A valid Galaxy URL.
	 * @throws ExecutionManagerConfigurationException  If the properties value was invalid.
	 */
	private URL getGalaxyURL(String urlProperty) throws ExecutionManagerConfigurationException {
		String galaxyURLString = environment.getProperty(urlProperty);

		try {
			if (galaxyURLString == null) {
				throw new ExecutionManagerConfigurationException("Missing Galaxy URL", urlProperty);
			} else {
				return new URL(galaxyURLString);
			}
		} catch (MalformedURLException e) {
			throw new ExecutionManagerConfigurationException("Invalid Galaxy URL", urlProperty, e);
		}
	}
	
	/**
	 * Gets and validates a property with the storage strategy for Galaxy.
	 * @param dataStorageProperty  The property with the storage strategy for Galaxy.
	 * @return  The corresponding storage strategy object, defaults to DEFAULT_DATA_STORAGE if invalid.
	 */
	private Uploader.DataStorage getDataStorage(String dataStorageProperty) {
		String dataStorageString = environment.getProperty(dataStorageProperty,"");
		Uploader.DataStorage dataStorage = VALID_STORAGE.get(dataStorageString.toLowerCase());
		
		if (dataStorage == null) {
			dataStorage = DEFAULT_DATA_STORAGE;
			
			logger.warn("Invalid configuration property \""
					+ dataStorageProperty
					+ "\"=\""
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
