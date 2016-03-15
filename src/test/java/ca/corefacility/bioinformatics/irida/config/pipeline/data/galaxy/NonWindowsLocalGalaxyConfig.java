package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;
import java.net.URL;

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
	
	/**
	 * The system property name to set a URL to a pre-populated database SQLite file.
	 */
	private final static String GALAXY_DATABASE_PROPERTY = "test.galaxy.database";
	
	/**
	 * The system property value if we want to use a local pre-configured database for Galaxy.
	 */
	private final static String GALAXY_USE_LOCAL_DATABASE = "local";
	
	/**
	 * A property which stores information about a Galaxy database to connect to for testing purposes.
	 */
	private final static String GALAXY_DATABASE_CONNECTION_PROPERTY = "test.galaxy.database.connection";

	/**
	 * URL to a local database file.
	 */
	private static final URL LOCAL_DATABASE_URL = NonWindowsLocalGalaxyConfig.class
			.getResource("db_gx_rev_0124.sqlite");
	
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
		localGalaxy.setUser1Password("galaxy");
		localGalaxy.setUser2Name(new GalaxyAccountEmail("user2@irida.corefacility.ca"));
		localGalaxy.setUser2Password("galaxy");
		localGalaxy.setWorkflowUserName(new GalaxyAccountEmail("workflow@irida.corefacility.ca"));
		localGalaxy.setWorkflowUserPassword("galaxy");
		localGalaxy.setNonExistentGalaxyAdminName(new GalaxyAccountEmail(
				"admin_no_exist@localhost.ca"));
		localGalaxy.setNonExistentGalaxyUserName(new GalaxyAccountEmail(
				"no_exist@localhost.ca"));

		localGalaxy.setInvalidGalaxyUserName(new GalaxyAccountEmail(
				"<a href='localhost'>invalid user</a>"));

		GalaxyInstance adminInstance = GalaxyInstanceFactory.get(
				localGalaxy.getGalaxyURL().toString(),
				localGalaxy.getAdminAPIKey());
		localGalaxy.setGalaxyInstanceAdmin(adminInstance);

		setupUserApiKeys(localGalaxy, adminInstance);

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

	//todo: delete? we no longer need to add toolsheds because we don't install anything for SNVPhyl anymore, it's built in
	//removed:	private void setupTestToolSheds(Path galaxyPath, GalaxyProperties galaxyProperties) throws URISyntaxException,
	//			IOException


	//todo: add database connection string to galaxy container
	//	private Optional<String> getGalaxyDatabaseConnectionString(String galaxyDatabaseConnectionProperty) {


	//TODO: delete, these tools are already in the container
	//removed	private void buildTestTools(Path galaxyRoot, GalaxyProperties galaxyProperties, LocalGalaxy localGalaxy) throws URISyntaxException, IOException


	//todo: delete, unneeded, no more downloading
	//removed:	private URL getGalaxyRepositoryURL(String systemProperty) throws MalformedURLException

	
	//todo: add database url to galax container
	// private Optional<URL> getGalaxyDatabaseURL(String systemProperty) throws MalformedURLException {

	//TODO: no longer needed, as we don't download Galaxy anymore
	//removed: private String getGalaxyRepositoryBranch(String systemProperty)

	//TODO: might not need this anymore? Since we're not downloading anything anymore
	//removed: private String getGalaxyRevision(String systemProperty)


	//TODO: delete this, no longer want to download galaxy
	//removed:	private BootStrapper downloadGalaxy(LocalGalaxy localGalaxy, URL repositoryURL,
	//			String branchName, String revisionHash)

	//TODO: switch this up so that it only sets the ports for docker-galaxy, and also the wrong ports. no longer need galaxy properties
	//removed:	private GalaxyProperties setupGalaxyProperties(LocalGalaxy localGalaxy, String revisionHash, Optional<URL> databaseURL, Optional<String> databaseConnectionString)
	//			throws MalformedURLException

	/**
	 * Sets up connection to an instance of Galaxy running in a docker container on host
	 * @param localGalaxy An object containing information about the local running Galaxy
	 * @throws MalformedURLException
     */
	private void setupGalaxyConnection(LocalGalaxy localGalaxy) throws MalformedURLException {

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

	//TODO: no longer need to build users, but still need to get api keys, possibly using the blend4j api

	/**
	 * Configures the users for the Galaxy for integration testing.
	 * @param localGalaxy  An object containing information about the local running Galaxy.
	 */
	private void setupUserApiKeys(LocalGalaxy localGalaxy, GalaxyInstance instance) {

		UsersClient usersClient = instance.getUsersClient();

		User user1 = usersClient.showUser("user1@irida.corefacility.ca");
		String user1apiKey = usersClient.createApiKey(user1.getId());
		localGalaxy.setUser1APIKey(user1apiKey);

		User user2 = usersClient.showUser("user2@irida.corefacility.ca");
		String user2apiKey = usersClient.createApiKey(user2.getId());
		localGalaxy.setUser2APIKey(user2apiKey);

		User workflowUser = usersClient.showUser("workflowuser@irida.corefacility.ca");
		String workflowApiKey = usersClient.createApiKey(workflowUser.getId());
		localGalaxy.setWorkflowUserAPIKey(workflowApiKey);
	}

	//TODO: delete this, no longer needed because we can just directly get the galaxyinstance objects
	//removed:	private GalaxyDaemon runGalaxy(GalaxyData galaxyData,
	//			LocalGalaxy localGalaxy) {

}
