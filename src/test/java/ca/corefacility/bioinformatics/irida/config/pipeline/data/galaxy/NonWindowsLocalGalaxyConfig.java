package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
import com.google.common.base.Optional;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryContentSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;
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
	 * The system property name to set the URL to the Galaxy mercurial repository. 
	 */
	private final static String GALAXY_URL_PROPERTY = "test.galaxy.repository.url";
	
	/**
	 * The system property name to set the branch of the Galaxy repository to use.
	 */
	private final static String GALAXY_BRANCH_PROPERTY = "test.galaxy.branch";
	
	/**
	 * The system property name to set the revision of the Galaxy repository to use.
	 */
	private final static String GALAXY_REVISION_PROPERTY = "test.galaxy.revision";
	
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
	 * Boolean to determine of Galaxy was successfully built the very first time.
	 */
	private boolean galaxyFailedToBuild = false;
	
	/**
	 * Exception on failure to build Galaxy for the first time.
	 */
	private Exception galaxyBuildException = null;

	/**
	 * URL to a local database file.
	 */
	private static final URL LOCAL_DATABASE_URL = NonWindowsLocalGalaxyConfig.class
			.getResource("db_gx_rev_0124.sqlite");
	
	private static final Logger logger = LoggerFactory
			.getLogger(NonWindowsLocalGalaxyConfig.class);

	private static final int largestPort = 65535;
	
	private static final String LATEST_REVISION_STRING = "latest";
	private static final String DEFAULT_REPSITORY_URL = "https://bitbucket.org/galaxy/galaxy-dist";
	private static final String DEFAULT_BRANCH = "default";

	/**
	 * Builds a GalaxyUploader to connect to a running instance of Galaxy.
	 * @return  An Uploader connected to a running instance of Galaxy.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader() throws Exception {
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.connectToGalaxy(galaxyConnector());

		return galaxyUploader;
	}
	
	/**
	 * Creates a new GalaxyConnector to connect to the local Galaxy instance.
	 * @return  A GalaxyConnector to connect to the local Galaxy instance.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public GalaxyConnector galaxyConnector() throws Exception {
		GalaxyConnector galaxyConnector = new GalaxyConnector(localGalaxy().getGalaxyURL(),
				localGalaxy().getAdminName(), localGalaxy().getAdminAPIKey());
		galaxyConnector.setDataStorage(DataStorage.REMOTE);
		
		return galaxyConnector;
	}

	/**
	 * Builds a GalaxyAPI object to connect to a running instance of Galaxy.
	 * @return  A GalaxyAPI object connected to a running instance of Galaxy.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public GalaxyUploaderAPI galaxyAPI() throws Exception {
		return new GalaxyUploaderAPI(localGalaxy().getGalaxyURL(), localGalaxy()
				.getAdminName(), localGalaxy().getAdminAPIKey());
	}
	
	/**
	 * Builds a {@link GalaxyLibraryContentSearch} object for testing against a running Galaxy.
	 * @return  A {@link GalaxyLibraryContentSearch} object connected to a running instance of Galaxy.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public GalaxyLibraryContentSearch galaxyLibraryContentSearch() throws Exception {
		return new GalaxyLibraryContentSearch(localGalaxy().getGalaxyInstanceAdmin().getLibrariesClient(),
				localGalaxy().getGalaxyURL());
	}

	/**
	 * Builds a new LocalGalaxy allowing for connecting with a running Galaxy instance.
	 * @return  A LocalGalaxy with information about the running Galaxy instance.
	 * @throws Exception 
	 */
	@Lazy
	@Bean
	public LocalGalaxy localGalaxy() throws Exception {
		
		if (galaxyFailedToBuild) {
			throw new RuntimeException("Galaxy could not be built the first time, don't attempt to try again", galaxyBuildException);
		} else {
			LocalGalaxy localGalaxy = null;
			
			try {
				localGalaxy = new LocalGalaxy();
				
				URL repositoryURL = getGalaxyRepositoryURL(GALAXY_URL_PROPERTY);
				String branchName = getGalaxyRepositoryBranch(GALAXY_BRANCH_PROPERTY);
				String revisionHash = getGalaxyRevision(GALAXY_REVISION_PROPERTY);
				Optional<URL> databaseURL = getGalaxyDatabaseURL(GALAXY_DATABASE_PROPERTY);
				Optional<String> databaseConnectionString = getGalaxyDatabaseConnectionString(GALAXY_DATABASE_CONNECTION_PROPERTY);
		
				String randomPassword = UUID.randomUUID().toString();
		
				localGalaxy.setAdminName(new GalaxyAccountEmail("admin@localhost"));
				localGalaxy.setAdminPassword(randomPassword);
				localGalaxy.setUser1Name(new GalaxyAccountEmail("user1@localhost"));
				localGalaxy.setUser1Password(randomPassword);
				localGalaxy.setUser2Name(new GalaxyAccountEmail("user2@localhost"));
				localGalaxy.setUser2Password(randomPassword);
				localGalaxy.setWorkflowUserName(new GalaxyAccountEmail("workflow@localhost"));
				localGalaxy.setWorkflowUserPassword(randomPassword);
				localGalaxy.setNonExistentGalaxyAdminName(new GalaxyAccountEmail(
						"admin_no_exist@localhost"));
				localGalaxy.setNonExistentGalaxyUserName(new GalaxyAccountEmail(
						"no_exist@localhost"));
		
				localGalaxy.setInvalidGalaxyUserName(new GalaxyAccountEmail(
						"<a href='localhost'>invalid user</a>"));
		
				GalaxyData galaxyData = new GalaxyData();
		
				BootStrapper bootStrapper = downloadGalaxy(localGalaxy, repositoryURL, branchName, revisionHash);
				localGalaxy.setBootStrapper(bootStrapper);
		
				GalaxyProperties galaxyProperties = setupGalaxyProperties(localGalaxy,revisionHash,databaseURL, databaseConnectionString);
				localGalaxy.setGalaxyProperties(galaxyProperties);
		
				buildGalaxyUsers(galaxyData, localGalaxy);
				
				setupTestToolSheds(localGalaxy.getGalaxyPath(), galaxyProperties);
				buildTestTools(localGalaxy.getGalaxyPath(), galaxyProperties, localGalaxy);
		
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
				localGalaxy.setGalaxyInstanceWorkflowUser(GalaxyInstanceFactory.get(
						localGalaxy.getGalaxyURL().toString(),
						localGalaxy.getWorkflowUserAPIKey()));
				
				localGalaxy.setupWorkflows();
				
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
	 * Sets up tool sheds for this test Galaxy instance.
	 * 
	 * @param galaxyPath
	 *            The path to the Galaxy root directory.
	 * @param galaxyProperties
	 *            The GalaxyProperties object.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private void setupTestToolSheds(Path galaxyPath, GalaxyProperties galaxyProperties) throws URISyntaxException,
			IOException {
		Path sourceToolShedsConf = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource("tool_sheds_conf.xml")
				.toURI());
		Path destinationToolShedsConf = galaxyPath.resolve("tool_sheds_conf.xml");
		Files.copy(sourceToolShedsConf, destinationToolShedsConf);

		Path toolDependencyDir = galaxyPath.resolve("tool_dependencies");
		Files.createDirectory(toolDependencyDir);

		galaxyProperties.setAppProperty("tool_dependency_dir", toolDependencyDir.toString());
	}

	/**
	 * Gets a database connection string for Galaxy.
	 * @param galaxyDatabaseConnectionProperty  The system property for the database connection string.
	 * @return  A database connection string for Galaxy.
	 */
	private Optional<String> getGalaxyDatabaseConnectionString(String galaxyDatabaseConnectionProperty) {
		String databaseConnectionString = System.getProperty(galaxyDatabaseConnectionProperty);
		if (databaseConnectionString == null) {
			return Optional.absent();
		} else {
			return Optional.of(databaseConnectionString);
		}
	}

	/**
	 * Builds configuration files for extra tools in Galaxy to test.
	 * @param galaxyRoot  The root directory to Galaxy.
	 * @param galaxyProperties  The properties object for Galaxy.
	 * @param localGalaxy  The LocalGalaxy instance.
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	private void buildTestTools(Path galaxyRoot, GalaxyProperties galaxyProperties, LocalGalaxy localGalaxy) throws URISyntaxException, IOException {
		Path sleepToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"sleep.xml").toURI());
		Path collectionExampleToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"collection_list_paired.xml").toURI());
		Path corePipelineOutputsSingleToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"core_pipeline_outputs.xml").toURI());
		Path corePipelineOutputsPairedToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"core_pipeline_outputs_paired.xml").toURI());
		Path corePipelineOutputsPairedWithParametersToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"core_pipeline_outputs_paired_with_parameters.xml").toURI());
		Path corePipelineOutputsPairedWithMultiLeveledParametersToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"core_pipeline_outputs_paired_with_multi_level_parameters.xml").toURI());
		Path corePipelineOutputsSinglePairedToolSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"core_pipeline_outputs_single_paired.xml").toURI());
		Path assemblyAnnotationPipelineOutputsSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"assembly_annotation_pipeline_outputs.xml").toURI());
		Path iridaToolConfigSource = Paths.get(NonWindowsLocalGalaxyConfig.class.getResource(
				"tool_conf_irida.xml").toURI());
		
		Path configDirectory = galaxyRoot.resolve("config");
		
		// copies conf/tool_conf.xml.sample to conf/tool_conf.xml
		// I'm suprised Galaxy doesn't do this by default
		Path toolConfigSample = configDirectory.resolve("tool_conf.xml.sample");
		Path toolConfig = configDirectory.resolve("tool_conf.xml");
		Files.copy(toolConfigSample, toolConfig);

		// copy over necessary files for testing custom tools
		Path exampleToolDirectory = galaxyRoot.resolve("tools").resolve("irida");
		Files.createDirectories(exampleToolDirectory);
		Path sleepToolDestination = 
				exampleToolDirectory.resolve("sleep.xml");
		Path collectionExampleToolDestination = 
				exampleToolDirectory.resolve("collection_list_paired.xml");
		Path corePipelineExampleToolDestination = 
				exampleToolDirectory.resolve("core_pipeline_outputs.xml");
		Path corePipelinePairedExampleToolDestination = 
				exampleToolDirectory.resolve("core_pipeline_outputs_paired.xml");
		Path corePipelinePairedWithParametersExampleToolDestination = 
				exampleToolDirectory.resolve("core_pipeline_outputs_paired_with_parameters.xml");
		Path corePipelinePairedWithMultiLeveledParametersExampleToolDestination = 
				exampleToolDirectory.resolve("core_pipeline_outputs_paired_with_multi_level_parameters.xml");
		Path corePipelineSinglePairedExampleToolDestination = 
				exampleToolDirectory.resolve("core_pipeline_outputs_single_paired.xml");
		Path assemblyAnnotationPipelinOutputsDestination = 
				exampleToolDirectory.resolve("assembly_annotation_pipeline_outputs.xml");
		Files.copy(sleepToolSource, sleepToolDestination);
		Files.copy(collectionExampleToolSource, collectionExampleToolDestination);
		Files.copy(corePipelineOutputsSingleToolSource, corePipelineExampleToolDestination);
		Files.copy(corePipelineOutputsPairedToolSource, corePipelinePairedExampleToolDestination);
		Files.copy(corePipelineOutputsPairedWithParametersToolSource, corePipelinePairedWithParametersExampleToolDestination);
		Files.copy(corePipelineOutputsPairedWithMultiLeveledParametersToolSource, corePipelinePairedWithMultiLeveledParametersExampleToolDestination);
		Files.copy(corePipelineOutputsSinglePairedToolSource, corePipelineSinglePairedExampleToolDestination);
		Files.copy(assemblyAnnotationPipelineOutputsSource, assemblyAnnotationPipelinOutputsDestination);
		
		Path iridaToolConfigDestination = configDirectory.resolve("tool_conf_irida.xml");
		Files.copy(iridaToolConfigSource, iridaToolConfigDestination);
		
		// set configuration file in Galaxy for custom tools
		galaxyProperties.setAppProperty("tool_config_file",
				"config/tool_conf.xml,config/shed_tool_conf.xml,config/tool_conf_irida.xml");
	}
	
	/**
	 * Gets the URL to a Galaxy repository to download and test against.
	 * @param systemProperty  The system property storing the URL.
	 * @return  A URL to a Galaxy repository to download and test against.
	 * @throws MalformedURLException 
	 */
	private URL getGalaxyRepositoryURL(String systemProperty) throws MalformedURLException {
		String repsitoryURLString = System.getProperty(systemProperty);
		URL repositoryURL = new URL(DEFAULT_REPSITORY_URL);
		
		if (repsitoryURLString != null && !"".equals(repsitoryURLString)) {
			repositoryURL = new URL(repsitoryURLString);
		}
		
		return repositoryURL;
	}
	
	/**
	 * Gets the URL to a Galaxy database to pre-populate the database.
	 * @param systemProperty  The system property storing the URL.
	 * @return  A URL to a Galaxy pre-populated database file.
	 * @throws MalformedURLException 
	 */
	private Optional<URL> getGalaxyDatabaseURL(String systemProperty) throws MalformedURLException {
		
		String databaseURLString = System.getProperty(systemProperty);
		Optional<URL> databaseURL = Optional.absent();
		
		if (databaseURLString != null && !"".equals(databaseURLString)) {
			if (GALAXY_USE_LOCAL_DATABASE.equals(databaseURLString)) {
				databaseURL = Optional.of(LOCAL_DATABASE_URL);
			} else {
				databaseURL = Optional.of(new URL(databaseURLString));
			}
		}
		
		return databaseURL;
	}
	
	/**
	 * Gets the branch within a Galaxy repository to download and test against.
	 * @param systemProperty  The system property storing the branch name.
	 * @return A branch name within a Galaxy repository.
	 */
	private String getGalaxyRepositoryBranch(String systemProperty) {
		String repsitoryBranchString = System.getProperty(systemProperty);
		
		if (repsitoryBranchString != null && !"".equals(repsitoryBranchString)) {
			return repsitoryBranchString;
		} else {
			return DEFAULT_BRANCH;
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
	 * @param repositoryURL The URL of the repository storing the Galaxy code.
	 * @param branchName  The branch name within the repository of Galaxy to download.
	 * @param revisionHash  The mercurial revisionHash of Galaxy to download. 
	 * @return  A BootStrapper object describing the downloaded Galaxy.
	 */
	@SuppressWarnings("deprecation")
	private BootStrapper downloadGalaxy(LocalGalaxy localGalaxy, URL repositoryURL,
			String branchName, String revisionHash) {
		final File DEFAULT_DESTINATION = null;
		
		DownloadProperties downloadProperties
			= new DownloadProperties(repositoryURL.toString(), branchName, revisionHash, DEFAULT_DESTINATION);
		BootStrapper bootStrapper = new BootStrapper(downloadProperties);

		bootStrapper.setupGalaxy();

		return bootStrapper;
	}

	/**
	 * Does some custom configuration for Galaxy to work with the tests.
	 * @param localGalaxy  The object describing the local running instance of Galaxy.
	 * @param revisionHash  The mercurial revision hash of the Galaxy version to download.
	 * @param databaseURL An (optional) URL to a pre-populated database for Galaxy.
	 * @param databaseConnectionString An {@Optional} value giving the location to use for a Galaxy database.
	 * @return  A GalaxyProperties object defining properties of the running instance of Galaxy.
	 * @throws MalformedURLException  If there was an issue constructing the Galaxy URL.
	 */
	private GalaxyProperties setupGalaxyProperties(LocalGalaxy localGalaxy, String revisionHash, Optional<URL> databaseURL, Optional<String> databaseConnectionString)
			throws MalformedURLException {
		GalaxyProperties galaxyProperties = new GalaxyProperties()
				.assignFreePort().configureNestedShedTools();
		
		if (databaseConnectionString.isPresent()) {
			logger.info("Setting Galaxy database to system property [" + GALAXY_DATABASE_CONNECTION_PROPERTY + "="
					+ databaseConnectionString.get() + "]");
			logger.info("Ignoring any value set in [" + GALAXY_DATABASE_PROPERTY
					+ "], and proceeding through all database migration steps");
			galaxyProperties.setAppProperty("database_connection", databaseConnectionString.get());
		} else {
			logger.info("No Galaxy database connection defined in [" + GALAXY_DATABASE_CONNECTION_PROPERTY
					+ "], using SQLite database");

			if (DownloadProperties.LATEST_REVISION.equals(revisionHash)) {
				galaxyProperties.prepopulateSqliteDatabase();
				logger.debug("Using latest revision of Galaxy.  Pre-populating with database found within Galaxy bootstrap");
			} else if (databaseURL.isPresent()) {
				galaxyProperties.prepopulateSqliteDatabase(databaseURL.get());
				logger.debug("Database located at " + databaseURL.get() + " has been set to use for Galaxy");
			} else {
				logger.debug("No pre-populated Galaxy database set, will proceed through all database migration steps");
			}
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
		
		// setup another port for running tests on
		int wrongPort2 = (galaxyPort + 2);
		if (wrongPort2 > largestPort) {
			wrongPort2 = galaxyPort - 2;
		}
		URL wrongGalaxyURL2 = new URL("http://localhost:" + wrongPort2 + "/");
		localGalaxy.setTestGalaxyURL(wrongGalaxyURL2);

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
		
		User workflowUser = new User(localGalaxy.getWorkflowUserName().getName());
		workflowUser.setPassword(localGalaxy.getWorkflowUserPassword());
		localGalaxy.setWorkflowUserAPIKey(workflowUser.getApiKey());

		galaxyData.getUsers().add(adminUser);
		galaxyData.getUsers().add(user1);
		galaxyData.getUsers().add(user2);
		galaxyData.getUsers().add(workflowUser);

		galaxyProperties.setAdminUsers(Arrays.asList(adminUser.getUsername(), workflowUser.getUsername()));
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

		galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);

		if (!galaxyDaemon.waitForUp()) {
			System.err.println("Could not launch Galaxy on "
					+ localGalaxy.getGalaxyURL());
			System.exit(1);
		}

		return galaxyDaemon;
	}
}
