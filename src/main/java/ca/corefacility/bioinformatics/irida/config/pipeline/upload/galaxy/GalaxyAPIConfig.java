package ca.corefacility.bioinformatics.irida.config.pipeline.upload.galaxy;

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

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

@Configuration
@Profile({ "dev", "prod" })
public class GalaxyAPIConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyAPIConfig.class);

	private static final String URL_PROPERTY = "galaxy.url";
	private static final String API_KEY_PROPERTY = "galaxy.admin.apiKey";
	private static final String ADMIN_EMAIL_PROPERTY = "galaxy.admin.email";
	private static final String DATA_STORAGE_PROPERTY = "galaxy.dataStorage";

	private static final Map<String, Uploader.DataStorage> VALID_STORAGE = ImmutableMap
			.<String, Uploader.DataStorage> builder()
			.put("remote", Uploader.DataStorage.REMOTE)
			.put("local", Uploader.DataStorage.LOCAL).build();

	@Autowired
	private Environment environment;

	@Autowired
	private Validator validator;

	/**
	 * Builds a new GalaxyUploader bean for connecting to an instance of Galaxy.
	 * @return  A new GalaxyUploader object.
	 */
	@Bean
	public GalaxyUploader galaxyUploader() {
		GalaxyUploader galaxyUploader = new GalaxyUploader();

		String galaxyURLString = environment.getProperty(URL_PROPERTY);
		String galaxyAdminAPIKey = environment.getProperty(API_KEY_PROPERTY);
		String galaxyAdminEmailString = environment
				.getProperty(ADMIN_EMAIL_PROPERTY);
		String dataStorageString = environment
				.getProperty(DATA_STORAGE_PROPERTY);

		// Only setup connection to Galaxy if it has been defined in the
		// configuration file
		if (galaxyURLString != null && galaxyAdminAPIKey != null
				&& galaxyAdminEmailString != null) {
			try {
				URL galaxyURL = new URL(galaxyURLString);
				GalaxyAccountEmail galaxyAdminEmail = new GalaxyAccountEmail(
						galaxyAdminEmailString);
				validateGalaxyAccountEmail(galaxyAdminEmail);

				galaxyUploader.setupGalaxyAPI(galaxyURL, galaxyAdminEmail,
						galaxyAdminAPIKey);

				if (dataStorageString != null) {
					Uploader.DataStorage dataStorage = VALID_STORAGE
							.get(dataStorageString.toLowerCase());

					if (dataStorage != null) {
						galaxyUploader.setDataStorage(dataStorage);
					} else {
						logger.warn("Invalid configuration property \""
								+ DATA_STORAGE_PROPERTY
								+ "="
								+ dataStorageString
								+ "\" must be one of "
								+ VALID_STORAGE.keySet()
								+ ": using default ("
								+ galaxyUploader.getDataStorage().toString()
										.toLowerCase() + ")");
					}
				}
			} catch (MalformedURLException e) {
				logger.error("Invalid configuration property \"" + URL_PROPERTY
						+ "=" + galaxyURLString + "\": not a proper URL", e);
			} catch (ConstraintViolationException e) {
				logger.error("Could not validate parameters to Galaxy", e);
			} catch (UploadException e) {
				logger.error("Could not connect to Galaxy on url="
						+ galaxyURLString, e);
			}
		} else {
			logger.warn("Galaxy connection not propertly setup, defaulting to no Galaxy connection");
		}

		return galaxyUploader;
	}

	/**
	 * Validates the constraints on a GalaxyAccountEmail for the property from the configuration file.
	 * @param accountEmail  The GalaxyAccountEmail that was loaded from the configuration file.
	 * @throws ConstraintViolationException  If one of the constraints was violated.
	 */
	private void validateGalaxyAccountEmail(GalaxyAccountEmail accountEmail)
			throws ConstraintViolationException {
		Set<ConstraintViolation<GalaxyAccountEmail>> violations = validator
				.validate(accountEmail);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}
}
