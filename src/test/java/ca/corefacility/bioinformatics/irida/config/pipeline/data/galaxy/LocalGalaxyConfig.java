package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.DownloadProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData.User;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * Builds a local running instance of Galaxy (requires mercurial and python) for integration testing.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
public class LocalGalaxyConfig {
	
	/**
	 * Boolean to determine of Galaxy was successfully built the very first time.
	 */
	private boolean galaxyFailedToBuild = false;
	
	/**
	 * Exception on failure to build Galaxy for the first time.
	 */
	private Exception galaxyBuildException = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(LocalGalaxyConfig.class);

	private static final int largestPort = 65535;
	
	private static final String LATEST_REVISION_STRING = "latest";

	/**
	 * Builds a GalaxyUploader to connect to a running instance of Galaxy.
	 * @return  An Uploader connected to a running instance of Galaxy.
	 * @throws MalformedURLException  If there was an issue when contructing a URL.
	 * @throws GalaxyConnectException If there was an issue connecting to the running instance of Galaxy.
	 */
	@Lazy
	@Bean
	public Uploader galaxyUploader() throws MalformedURLException, GalaxyConnectException {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(localGalaxy().getGalaxyURL(),
				localGalaxy().getAdminName(), localGalaxy().getAdminAPIKey());

		return galaxyUploader;
	}

	/**
	 * Builds a GalaxyAPI object to connect to a running instance of Galaxy.
	 * @return  A GalaxyAPI object connected to a running instance of Galaxy.
	 * @throws MalformedURLException  If there was an issue building some of the URLs.
	 * @throws GalaxyConnectException If there was an issue connecting to the Galaxy instance.
	 */
	@Lazy
	@Bean
	public GalaxyAPI galaxyAPI() throws MalformedURLException, GalaxyConnectException {
		return new GalaxyAPI(localGalaxy().getGalaxyURL(), localGalaxy()
				.getAdminName(), localGalaxy().getAdminAPIKey());
	}

	/**
	 * Builds a new LocalGalaxy allowing for connecting with a running Galaxy instance.
	 * @return  A LocalGalaxy with information about the running Galaxy instance.
	 * @throws MalformedURLException  If there was an issue constructing the URLs.
	 */
	@Lazy
	@Bean
	public LocalGalaxy localGalaxy() throws MalformedURLException {
		
		if (galaxyFailedToBuild) {
			throw new RuntimeException("Galaxy could not be built the first time, don't attempt to try again", galaxyBuildException);
		} else {
			LocalGalaxy localGalaxy = null;
			
			try {
				localGalaxy = new LocalGalaxy();
				
				String revisionHash = getGalaxyRevision("test.galaxy.revision");
		
				String randomPassword = UUID.randomUUID().toString();
		
				localGalaxy.setAdminName(new GalaxyAccountEmail("admin@localhost"));
				localGalaxy.setAdminPassword(randomPassword);
				localGalaxy.setUser1Name(new GalaxyAccountEmail("user1@localhost"));
				localGalaxy.setUser1Password(randomPassword);
				localGalaxy.setUser2Name(new GalaxyAccountEmail("user2@localhost"));
				localGalaxy.setUser2Password(randomPassword);
				localGalaxy.setNonExistentGalaxyAdminName(new GalaxyAccountEmail(
						"admin_no_exist@localhost"));
				localGalaxy.setNonExistentGalaxyUserName(new GalaxyAccountEmail(
						"no_exist@localhost"));
		
				localGalaxy.setInvalidGalaxyUserName(new GalaxyAccountEmail(
						"<a href='localhost'>invalid user</a>"));
		
				GalaxyData galaxyData = new GalaxyData();
		
				BootStrapper bootStrapper = downloadGalaxy(localGalaxy,revisionHash);
				localGalaxy.setBootStrapper(bootStrapper);
		
				GalaxyProperties galaxyProperties = setupGalaxyProperties(localGalaxy,revisionHash);
				localGalaxy.setGalaxyProperties(galaxyProperties);
		
				buildGalaxyUsers(galaxyData, localGalaxy);
		
				GalaxyDaemon galaxyDaemon = runGalaxy(galaxyData, localGalaxy);
				localGalaxy.setGalaxyDaemon(galaxyDaemon);
		
				localGalaxy.setGalaxyInstanceAdmin(GalaxyInstanceFactory.get(
						localGalaxy.getGalaxyURL().toString(),
						localGalaxy.getAdminAPIKey()));
				localGalaxy.setGalaxyInstanceUser1(GalaxyInstanceFactory.get(
						localGalaxy.getGalaxyURL().toString(),
						localGalaxy.getUser1APIKey()));
				localGalaxy.setGalaxyInstanceUser2(GalaxyInstanceFactory.get(
						localGalaxy.getGalaxyURL().toString(),
						localGalaxy.getUser2APIKey()));
				
				return localGalaxy;
			} catch (Exception e) {
				// If Galaxy failed to build, we don't want it to re-build on every test
				// which will waste time and resources.  Instead, we save the failure exception
				// and throw it on every further attempt to build a local instance of Galaxy.
				
				galaxyFailedToBuild = true;
				galaxyBuildException = e;
				
				// cleanup files if Galaxy was downloaded but couldn't be run
				if (localGalaxy != null) {
					localGalaxy.deleteGalaxy();
				}
				
				throw e;
			}
		}
	}
	
	/**
	 * Given a system property string gets the revision hash for the version of Galaxy
	 * 	from this property.  Corresponds to commit in https://bitbucket.org/galaxy/galaxy-dist.
	 * @param systemProperty  The system property storing the revision hash.
	 * @return  The revision hash code to download Galaxy at, DownloadProperties.LATEST_REVISION
	 * 	if no hash is defined.
	 */
	private String getGalaxyRevision(String systemProperty) {
		String revisionHash = System.getProperty(systemProperty);
		if (revisionHash != null) {
			
			// must be LATEST_REVISION_STRING or a hex number
			if (LATEST_REVISION_STRING.equalsIgnoreCase(revisionHash)) {
				revisionHash = DownloadProperties.LATEST_REVISION;
				logger.debug("Galaxy revision from " + systemProperty + "=" + LATEST_REVISION_STRING);
			}
			else if (!revisionHash.matches("^[a-fA-F0-9]+$")) {
				throw new IllegalArgumentException(systemProperty + "=" + revisionHash + " is invalid");
			} else {
				logger.debug("Galaxy revision from " + systemProperty + "=" + revisionHash);
			}
		} else {
			revisionHash = DownloadProperties.LATEST_REVISION;
			logger.debug("No Galaxy revision set in " + systemProperty + " defaulting to latest revision");
		}
		
		return revisionHash;
	}

	/**
	 * Downloads the latest stable release of Galaxy.
	 * @param localGalaxy  The LocalGalaxy object used to fill in information about Galaxy.
	 * @param revisionHash  The mercurial revisionHash of Galaxy to download. 
	 * @return  A BootStrapper object describing the downloaded Galaxy.
	 */
	private BootStrapper downloadGalaxy(LocalGalaxy localGalaxy, String revisionHash) {
		final File DEFAULT_DESTINATION = null;
		
		DownloadProperties downloadProperties
			= DownloadProperties.forGalaxyDist(DEFAULT_DESTINATION, revisionHash);
		BootStrapper bootStrapper = new BootStrapper(downloadProperties);

		logger.info("About to download Galaxy");
		logger.info(downloadProperties.toString());
		bootStrapper.setupGalaxy();
		logger.info("Finished downloading Galaxy");

		return bootStrapper;
	}

	/**
	 * Does some custom configuration for Galaxy to work with the tests.
	 * @param localGalaxy  The object describing the local running instance of Galaxy.
	 * @param revisionHash  The mercurial revision hash of the Galaxy version to download. 
	 * @return  A GalaxyProperties object defining properties of the running instance of Galaxy.
	 * @throws MalformedURLException  If there was an issue constructing the Galaxy URL.
	 */
	private GalaxyProperties setupGalaxyProperties(LocalGalaxy localGalaxy, String revisionHash)
			throws MalformedURLException {
		GalaxyProperties galaxyProperties = new GalaxyProperties()
				.assignFreePort().configureNestedShedTools();
		
		// only pre-populate if latest Galaxy
		// speeds up database construction, but database wouldn't be valid for previous versions of Galaxy
		if (DownloadProperties.LATEST_REVISION.equals(revisionHash)) {
			galaxyProperties.prepopulateSqliteDatabase();
		}
		
		galaxyProperties.setAppProperty("allow_library_path_paste", "true");

		int galaxyPort = galaxyProperties.getPort();
		URL galaxyURL = new URL("http://localhost:" + galaxyPort + "/");
		localGalaxy.setGalaxyURL(galaxyURL);

		// set wrong port to something Galaxy is not running on
		int wrongPort = (galaxyPort + 1);
		if (wrongPort > largestPort) {
			wrongPort = galaxyPort - 1;
		}
		URL wrongGalaxyURL = new URL("http://localhost:" + wrongPort + "/");
		localGalaxy.setInvalidGalaxyURL(wrongGalaxyURL);

		return galaxyProperties;
	}

	/**
	 * Configures the users for the Galaxy for integration testing.
	 * @param galaxyData  A GalaxyData object used to setup users.
	 * @param localGalaxy  An object containing information about the local running Galaxy.
	 */
	private void buildGalaxyUsers(GalaxyData galaxyData, LocalGalaxy localGalaxy) {
		GalaxyProperties galaxyProperties = localGalaxy.getGalaxyProperties();

		User adminUser = new User(localGalaxy.getAdminName().getName());
		adminUser.setPassword(localGalaxy.getAdminPassword());
		localGalaxy.setAdminAPIKey(adminUser.getApiKey());

		User user1 = new User(localGalaxy.getUser1Name().getName());
		user1.setPassword(localGalaxy.getUser1Password());
		localGalaxy.setUser1APIKey(user1.getApiKey());

		User user2 = new User(localGalaxy.getUser2Name().getName());
		user2.setPassword(localGalaxy.getUser2Password());
		localGalaxy.setUser2APIKey(user2.getApiKey());

		galaxyData.getUsers().add(adminUser);
		galaxyData.getUsers().add(user1);
		galaxyData.getUsers().add(user2);

		galaxyProperties.setAdminUser(adminUser.getUsername());
	}

	/**
	 * Constructs a string containing the Galaxy user information for logging.
	 * @param usertype  The type of user constructed (admin, etc).
	 * @param name  The name of the user.
	 * @param password  The password of the user.
	 * @param apiKey  The api key of the user.
	 * @return  A String with the proper logging message.
	 */
	private String generateUserString(String usertype, String name,
			String password, String apiKey) {
		return "Galaxy " + usertype + " user: " + name + ", password: "
				+ password + ", apiKey: " + apiKey;
	}

	/**
	 * Runs the downloaded and configured instance of Galaxy.
	 * @param galaxyData  The data used to run Galaxy.
	 * @param localGalaxy  The object containing information about the local Galaxy instance.
	 * @return  A GalaxyDaemon object containing information about the running Galaxy process.
	 */
	private GalaxyDaemon runGalaxy(GalaxyData galaxyData,
			LocalGalaxy localGalaxy) {
		GalaxyDaemon galaxyDaemon;

		GalaxyProperties galaxyProperties = localGalaxy.getGalaxyProperties();
		BootStrapper bootStrapper = localGalaxy.getBootStrapper();

		File galaxyLogFile = new File(bootStrapper.getPath() + File.separator
				+ "paster.log");

		logger.info("Setting up Galaxy");
		logger.debug(generateUserString("admin", localGalaxy.getAdminName()
				.getName(), localGalaxy.getAdminPassword(), localGalaxy
				.getAdminAPIKey()));
		logger.debug(generateUserString("user1", localGalaxy.getUser1Name()
				.getName(), localGalaxy.getUser1Password(), localGalaxy
				.getUser1APIKey()));
		logger.debug(generateUserString("user2", localGalaxy.getUser2Name()
				.getName(), localGalaxy.getUser2Password(), localGalaxy
				.getUser2APIKey()));
		logger.debug("Setup log files located within: " + bootStrapper.getBootstrapLogDir().getAbsolutePath());

		galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);

		logger.info("Waiting for Galaxy to come up on url: "
				+ localGalaxy.getGalaxyURL() + ", log: "
				+ galaxyLogFile.getAbsolutePath());

		if (!galaxyDaemon.waitForUp()) {
			System.err.println("Could not launch Galaxy on "
					+ localGalaxy.getGalaxyURL());
			System.exit(1);
		}
		logger.info("Galaxy running on url: " + localGalaxy.getGalaxyURL());

		return galaxyDaemon;
	}
}
