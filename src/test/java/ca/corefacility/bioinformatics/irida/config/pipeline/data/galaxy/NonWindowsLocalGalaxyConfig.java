package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * Builds a local running instance of Galaxy (requires mercurial and python) for
 * integration testing.
 *
 */
@TestConfiguration
@Conditional(NonWindowsPlatformCondition.class)
public class NonWindowsLocalGalaxyConfig implements LocalGalaxyConfig {

	private static final Logger logger = LoggerFactory.getLogger(NonWindowsLocalGalaxyConfig.class);

	/**
	 * URL for Galaxy used for test classes.
	 */
	@Value("${test.galaxy.url}")
	private URL galaxyURL;

	/**
	 * Invalid URL for Galaxy used for test classes.
	 */
	@Value("${test.galaxy.invalid.url}")
	private URL galaxyInvalidURL;

	/**
	 * Another invalid URL for Galaxy used for test classes.
	 */
	@Value("${test.galaxy.invalid.url2}")
	private URL galaxyInvalidURL2;

	/**
	 * Builds a new LocalGalaxy allowing for connecting with a running Galaxy
	 * instance.
	 * 
	 * @return A LocalGalaxy with information about the running Galaxy instance.
	 * @throws Exception
	 */
	@Lazy
	@Bean
	public LocalGalaxy localGalaxy() throws Exception {
		LocalGalaxy localGalaxy = new LocalGalaxy();

		logger.debug("Setting URL for test Galaxy: " + galaxyURL);
		logger.debug("Setting invalid URL for test Galaxy: " + galaxyInvalidURL);
		logger.debug("Setting invalid URL2 for test Galaxy: " + galaxyInvalidURL2);

		localGalaxy.setGalaxyURL(galaxyURL);
		localGalaxy.setInvalidGalaxyURL(galaxyInvalidURL);
		localGalaxy.setTestGalaxyURL(galaxyInvalidURL2);

		localGalaxy.setAdminName(new GalaxyAccountEmail("admin@galaxy.org"));
		localGalaxy.setAdminPassword("password");
		localGalaxy.setAdminAPIKey("fakekey");

		logger.debug("Creating Admin Blend4j Galaxy Instance using api key: " + localGalaxy.getAdminAPIKey());
		GalaxyInstance adminInstance = GalaxyInstanceFactory.get(localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getAdminAPIKey());
		localGalaxy.setGalaxyInstanceAdmin(adminInstance);

		localGalaxy.setupWorkflows();

		return localGalaxy;
	}
}
