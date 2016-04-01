package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.UserCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * Builds a local running instance of Galaxy (requires mercurial and python) for integration testing.
 *
 */
@Configuration
@Profile("test")
@Conditional(NonWindowsPlatformCondition.class)
public class NonWindowsLocalGalaxyConfig implements LocalGalaxyConfig {
	
	private static final Logger logger = LoggerFactory
			.getLogger(NonWindowsLocalGalaxyConfig.class);

	private static final int largestPort = 65535;

	private static final int dockerGalaxyPort = 48888;
	/**
	 * Builds a new LocalGalaxy allowing for connecting with a running Galaxy instance.
	 * @return  A LocalGalaxy with information about the running Galaxy instance.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public LocalGalaxy localGalaxy() throws Exception {
		LocalGalaxy localGalaxy = new LocalGalaxy();

		setupGalaxyConnection(localGalaxy);

		localGalaxy.setAdminName(new GalaxyAccountEmail("admin@galaxy.org"));
		localGalaxy.setAdminPassword("admin");
		localGalaxy.setAdminAPIKey("admin");

		logger.debug("Creating Admin Blend4j Galaxy Instance using api key: " + localGalaxy.getAdminAPIKey());
		GalaxyInstance adminInstance = GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getAdminAPIKey());
		localGalaxy.setGalaxyInstanceAdmin(adminInstance);

		localGalaxy.setupWorkflows();

		return localGalaxy;
		}

	/**
	 * Sets up connection to an instance of Galaxy running in a docker container on host
	 * @param localGalaxy An object containing information about the local running Galaxy
	 * @throws MalformedURLException
     */
	private void setupGalaxyConnection(LocalGalaxy localGalaxy) throws MalformedURLException {

		logger.debug("Setting Docker Galaxy ports");
		int galaxyPort = dockerGalaxyPort;
		URL galaxyURL = new URL("http://localhost:" + galaxyPort + "/");
		localGalaxy.setGalaxyURL(galaxyURL);

		// set wrong port to something Galaxy is not running on
		int wrongPort = (galaxyPort + 1);
		if (wrongPort > largestPort) {
			wrongPort = galaxyPort - 1;
		}
		URL wrongGalaxyURL = new URL("http://localhost:" + wrongPort + "/");
		localGalaxy.setInvalidGalaxyURL(wrongGalaxyURL);

		// setup another port for running tests on
		int wrongPort2 = (galaxyPort + 2);
		if (wrongPort2 > largestPort) {
			wrongPort2 = galaxyPort - 2;
		}
		URL wrongGalaxyURL2 = new URL("http://localhost:" + wrongPort2 + "/");
		localGalaxy.setTestGalaxyURL(wrongGalaxyURL2);
	}
}
