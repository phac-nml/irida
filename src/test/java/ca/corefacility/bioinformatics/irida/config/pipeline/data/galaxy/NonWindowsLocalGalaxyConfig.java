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
		localGalaxy.setUser1Name(new GalaxyAccountEmail("user1@irida.corefacility.ca"));
		localGalaxy.setUser1Password("galaxyuser1");
		localGalaxy.setUser2Name(new GalaxyAccountEmail("user2@irida.corefacility.ca"));
		localGalaxy.setUser2Password("galaxyuser2");
		localGalaxy.setWorkflowUserName(new GalaxyAccountEmail("workflowUser@irida.corefacility.ca"));
		localGalaxy.setWorkflowUserPassword("galaxyuserwork");
		localGalaxy.setNonExistentGalaxyAdminName(new GalaxyAccountEmail(
				"admin_no_exist@localhost.ca"));
		localGalaxy.setNonExistentGalaxyUserName(new GalaxyAccountEmail(
				"no_exist@localhost.ca"));

		localGalaxy.setInvalidGalaxyUserName(new GalaxyAccountEmail(
				"<a href='localhost'>invalid user</a>"));

		logger.debug("Creating Admin Blend4j Galaxy Instance using api key: " + localGalaxy.getAdminAPIKey());
		GalaxyInstance adminInstance = GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getAdminAPIKey());
		localGalaxy.setGalaxyInstanceAdmin(adminInstance);

		setupUserApiKeys(localGalaxy, adminInstance);

		logger.debug("Creating GalaxyInstances for users.");
		localGalaxy.setGalaxyInstanceUser1(GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getUser1APIKey()));
		localGalaxy.setGalaxyInstanceUser2(GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getUser2APIKey()));
		localGalaxy.setGalaxyInstanceWorkflowUser(GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getWorkflowUserAPIKey()));

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

	/**
	 * Configures the users for the Galaxy for integration testing.
	 * @param localGalaxy  An object containing information about the local running Galaxy.
	 */
	private void setupUserApiKeys(LocalGalaxy localGalaxy, GalaxyInstance instance) {

		logger.debug("Getting users client from admin instance.");
		UsersClient usersClient = instance.getUsersClient();

		logger.debug("Creating user1.");
		UserCreate userCreate1 = new UserCreate();
		userCreate1.setEmail("user1@irida.corefacility.ca");
		userCreate1.setPassword("galaxyuser1");
		userCreate1.setUsername("user1");
		logger.debug("Generating new api-key for user1");
		final User user1 = usersClient.createUser(userCreate1);
		final String user1apiKey = usersClient.createApiKey(user1.getId());
		localGalaxy.setUser1APIKey(user1apiKey);

		logger.debug("Creating user2.");
		UserCreate userCreate2 = new UserCreate();
		userCreate2.setEmail("user2@irida.corefacility.ca");
		userCreate2.setPassword("galaxyuser2");
		userCreate2.setUsername("user2");
		logger.debug("Generating new api-key for user2");
		final User user2 = usersClient.createUser(userCreate2);
		final String user2apiKey = usersClient.createApiKey(user2.getId());
		localGalaxy.setUser2APIKey(user2apiKey);

		logger.debug("Creating workflowuser.");
		UserCreate userCreateWorkflow = new UserCreate();
		userCreateWorkflow.setEmail("workflowUser@irida.corefacility.ca");
		userCreateWorkflow.setPassword("galaxyuserwork");
		userCreateWorkflow.setUsername("workflowuser");
		logger.debug("Generating new api-key for workflow");
		final User workflowUser = usersClient.createUser(userCreateWorkflow);
		final String workflowUserApiKey = usersClient.createApiKey(workflowUser.getId());
		localGalaxy.setUser1APIKey(workflowUserApiKey);
	}
}
