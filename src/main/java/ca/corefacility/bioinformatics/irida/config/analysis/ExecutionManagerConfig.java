package ca.corefacility.bioinformatics.irida.config.analysis;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.*;

import com.github.jmchilton.blend4j.galaxy.*;
import com.google.common.collect.ImmutableMap;

/**
 * Configuration for connections to an ExecutionManager in IRIDA.
 */
@Configuration
@Profile({ "dev", "prod", "it", "analysis", "ncbi", "processing", "sync", "email", "web" })
public class ExecutionManagerConfig {
	private static final Logger logger = LoggerFactory.getLogger(ExecutionManagerConfig.class);

	/**
	 * Property names for a Galaxy instance to execution jobs on.
	 */
	private static final String URL_EXECUTION_PROPERTY = "galaxy.execution.url";
	private static final String API_KEY_EXECUTION_PROPERTY = "galaxy.execution.apiKey";
	private static final String EMAIL_EXECUTION_PROPERTY = "galaxy.execution.email";
	private static final String DATA_STORAGE_EXECUTION_PROPERTY = "galaxy.execution.dataStorage";

	private static final Map<String, DataStorage> VALID_STORAGE = ImmutableMap.of("remote", DataStorage.REMOTE, "local",
			DataStorage.LOCAL);

	private static final DataStorage DEFAULT_DATA_STORAGE = DataStorage.REMOTE;

	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	@Value("${galaxy.library.upload.timeout}")
	private int libraryTimeout;

	/**
	 * Polling time in seconds to poll a Galaxy library to check if datasets have been properly uploaded.
	 */
	@Value("${galaxy.library.upload.polling.time}")
	private int pollingTime;

	/**
	 * Number of independent threads to use for uploading files to a Galaxy library.
	 */
	@Value("${galaxy.library.upload.threads}")
	private int libraryUploadThreads;

	@Autowired
	private Environment environment;

	@Autowired
	private Validator validator;

	/**
	 * Builds a new ExecutionManagerGalaxy from the given properties.
	 * 
	 * @return An ExecutionManagerGalaxy.
	 * @throws ExecutionManagerConfigurationException If no execution manager is configured.
	 */
	@Lazy
	@Bean
	public ExecutionManagerGalaxy executionManager() throws ExecutionManagerConfigurationException {
		return buildExecutionManager(URL_EXECUTION_PROPERTY, API_KEY_EXECUTION_PROPERTY, EMAIL_EXECUTION_PROPERTY,
				DATA_STORAGE_EXECUTION_PROPERTY);
	}

	/**
	 * Builds a new ExecutionManagerGalaxy given the following environment properties.
	 * 
	 * @param urlProperty         The property defining the URL to Galaxy.
	 * @param apiKeyProperty      The property defining the API key to Galaxy.
	 * @param emailProperty       The property defining the account email in Galaxy.
	 * @param dataStorageProperty The property defning the data storage method.
	 * @return An ExecutionManagerGalaxy.
	 * @throws ExecutionManagerConfigurationException If there was an issue building an ExecutionManagerGalaxy from the
	 *                                                given properties.
	 */
	private ExecutionManagerGalaxy buildExecutionManager(String urlProperty, String apiKeyProperty,
			String emailProperty, String dataStorageProperty) throws ExecutionManagerConfigurationException {

		URL galaxyURL = getGalaxyURL(urlProperty);
		GalaxyAccountEmail galaxyEmail = getGalaxyEmail(emailProperty);
		String apiKey = getAPIKey(apiKeyProperty);
		DataStorage dataStorage = getDataStorage(dataStorageProperty);

		return new ExecutionManagerGalaxy(galaxyURL, apiKey, galaxyEmail, dataStorage);
	}

	/**
	 * Gets and validates a GalaxyAccountEmail from the given property.
	 * 
	 * @param emailProperty The property to find the email address.
	 * @return A valid GalaxyAccountEmail.
	 * @throws ExecutionManagerConfigurationException If the properties value was invalid.
	 */
	private GalaxyAccountEmail getGalaxyEmail(String emailProperty) throws ExecutionManagerConfigurationException {
		String galaxyEmailString = environment.getProperty(emailProperty);
		GalaxyAccountEmail galaxyEmail = new GalaxyAccountEmail(galaxyEmailString);

		Set<ConstraintViolation<GalaxyAccountEmail>> violations = validator.validate(galaxyEmail);

		if (!violations.isEmpty()) {
			throw new ExecutionManagerConfigurationException("Invalid email address", emailProperty,
					new ConstraintViolationException(violations));
		}

		return galaxyEmail;
	}

	/**
	 * Gets and validates a Galaxy API key from the given property.
	 * 
	 * @param apiKeyProperty The API key property to get.
	 * @return A API key for Galaxy.
	 * @throws ExecutionManagerConfigurationException If the given properties value was invalid.
	 */
	private String getAPIKey(String apiKeyProperty) throws ExecutionManagerConfigurationException {
		String apiKey = environment.getProperty(apiKeyProperty);

		if (apiKey == null) {
			throw new ExecutionManagerConfigurationException("Missing apiKey", apiKeyProperty);
		} else {
			return apiKey;
		}
	}

	/**
	 * Gets and validates the given property for a Galaxy url.
	 * 
	 * @param urlProperty The property with the Galaxy URL.
	 * @return A valid Galaxy URL.
	 * @throws ExecutionManagerConfigurationException If the properties value was invalid.
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
	 * 
	 * @param dataStorageProperty The property with the storage strategy for Galaxy.
	 * @return The corresponding storage strategy object, defaults to DEFAULT_DATA_STORAGE if invalid.
	 */
	private DataStorage getDataStorage(String dataStorageProperty) {
		String dataStorageString = environment.getProperty(dataStorageProperty, "");
		DataStorage dataStorage = VALID_STORAGE.get(dataStorageString.toLowerCase());

		if (dataStorage == null) {
			dataStorage = DEFAULT_DATA_STORAGE;

			logger.warn("Invalid configuration property \"" + dataStorageProperty + "\"=\"" + dataStorageString
					+ "\" must be one of " + VALID_STORAGE.keySet() + ": using default ("
					+ DEFAULT_DATA_STORAGE.toString().toLowerCase() + ")");
		}

		return dataStorage;
	}

	/**
	 * @return A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public GalaxyWorkflowService galaxyWorkflowService() throws ExecutionManagerConfigurationException {
		return new GalaxyWorkflowService(workflowsClient(), StandardCharsets.UTF_8);
	}

	/**
	 * @return A GalaxyToolDataService for interacting with Galaxy Tool Data Tables.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public GalaxyToolDataService galaxyToolDataService() throws ExecutionManagerConfigurationException {
		return new GalaxyToolDataService(toolDataClient());
	}

	/**
	 * @return A ToolDataClient for interacting with Galaxy.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public ToolDataClient toolDataClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getToolDataClient();
	}

	/**
	 * @return A RolesClient for dealing with roles in Galaxy.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public RolesClient rolesClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getRolesClient();
	}

	/**
	 * @return A WorkflowsClient for interacting with Galaxy.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public WorkflowsClient workflowsClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getWorkflowsClient();
	}

	/**
	 * @return A LibrariesClient for interacting with Galaxy.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public LibrariesClient librariesClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getLibrariesClient();
	}

	/**
	 * @return A GalaxyHistoriesService for interacting with Galaxy histories.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() throws ExecutionManagerConfigurationException {
		return new GalaxyHistoriesService(historiesClient(), toolsClient(), galaxyLibrariesService());
	}

	/**
	 * @return A GalaxyHistoriesService for interacting with Galaxy histories.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public GalaxyLibrariesService galaxyLibrariesService() throws ExecutionManagerConfigurationException {
		return new GalaxyLibrariesService(librariesClient(), pollingTime, libraryTimeout, libraryUploadThreads);
	}

	/**
	 * @return A ToolsClient for interacting with Galaxy tools.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public ToolsClient toolsClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getToolsClient();
	}

	/**
	 * @return A JobsClient for interacting with Galaxy jobs.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public JobsClient jobsClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getJobsClient();
	}

	/**
	 * @return A HistoriesClient for interacting with Galaxy histories.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public HistoriesClient historiesClient() throws ExecutionManagerConfigurationException {
		return galaxyInstance().getHistoriesClient();
	}

	@Lazy
	@Bean
	public GalaxyJobErrorsService galaxyJobErrorsService() throws ExecutionManagerConfigurationException {
		return new GalaxyJobErrorsService(historiesClient(), toolsClient(), jobsClient());
	}

	/**
	 * @return An instance of a connection to Galaxy.
	 * @throws ExecutionManagerConfigurationException If there is an issue building the execution manager.
	 */
	@Lazy
	@Bean
	public GalaxyInstance galaxyInstance() throws ExecutionManagerConfigurationException {
		return GalaxyInstanceFactory.get(executionManager().getLocation().toString(), executionManager().getAPIKey());
	}
}
