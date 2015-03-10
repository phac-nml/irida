package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;

/**
 * A class containing information about the running instance of Galaxy for integration testing.
 *
 */
public class LocalGalaxy {
	private static final Logger logger = LoggerFactory
			.getLogger(LocalGalaxy.class);

	private BootStrapper bootStrapper;
	private GalaxyDaemon galaxyDaemon;
	private GalaxyProperties galaxyProperties;

	private URL galaxyURL;
	private URL invalidGalaxyURL;
	private URL testGalaxyURL;

	private GalaxyAccountEmail adminName;
	private String adminPassword;
	private String adminAPIKey;

	private GalaxyAccountEmail user1Name;
	private String user1Password;
	private String user1APIKey;

	private GalaxyAccountEmail user2Name;
	private String user2Password;
	private String user2APIKey;
	
	private GalaxyAccountEmail workflowUserName;
	private String workflowUserPassword;
	private String workflowUserAPIKey;
	
	private GalaxyAccountEmail nonExistentGalaxyAdminName;
	private GalaxyAccountEmail nonExistentGalaxyUserName;

	private UploaderAccountName invalidGalaxyUserName;

	private GalaxyInstance galaxyInstanceAdmin;
	private GalaxyInstance galaxyInstanceUser1;
	private GalaxyInstance galaxyInstanceUser2;
	private GalaxyInstance galaxyInstanceWorkflowUser;
	
	private String singleInputWorkflowId;
	private String singleInputWorkflowLabel;
	
	private String workflowFilterId;
	private String workflowFilterLabel;
	
	private String workflowSleepId;
	private String workflowSleepLabel;
	
	private String worklowCollectionListId;
	private String workflowCollectionListLabel;
	
	private String workflowCorePipelineTestId;
	private String workflowCorePipelineTestSequenceFilesLabel;
	private String workflowCorePipelineTestReferenceLabel;
	private String workflowCorePipelineTestTreeName;
	private String workflowCorePipelineTestMatrixName;
	private String workflowCorePipelineTestTabelName;
	private Path workflowCorePipelineTestMatrix;
	private Path workflowCorePipelineTestTree;
	private Path workflowCorePipelineTestSnpTable;
	
	private String invalidWorkflowId;
	private String invalidWorkflowLabel = "invalid";

	/**
	 * Method to cleanup the running instance of Galaxy when finished with tests.
	 */
	@PreDestroy
	public void shutdownGalaxy() {
		logger.info("Shutting down Galaxy on url=" + galaxyURL);
		galaxyDaemon.stop();
		galaxyDaemon.waitForDown();
		deleteGalaxy();
	}
	
	/**
	 * Delete Galaxy directory
	 */
	public void deleteGalaxy() {
		logger.debug("Deleting Galaxy directory: " + bootStrapper.getPath());
		bootStrapper.deleteGalaxyRoot();
	}
	
	/**
	 * Gets the root directory where the local Galaxy is running.
	 * @return  The root directory for the local Galaxy.
	 */
	public Path getGalaxyPath() {
		checkNotNull(bootStrapper);
		
		return Paths.get(bootStrapper.getPath());
	}

	/**
	 * @return The URL to the running Galaxy instance.
	 */
	public URL getGalaxyURL() {
		return galaxyURL;
	}

	/**
	 * Sets the Galaxy URL.
	 * @param galaxyURL  The Galaxy URL.
	 */
	public void setGalaxyURL(URL galaxyURL) {
		this.galaxyURL = galaxyURL;
	}

	/**
	 * @return  The Admin name for the local Galaxy.
	 */
	public GalaxyAccountEmail getAdminName() {
		return adminName;
	}

	/**
	 * Sets the admin name for Galaxy.
	 * @param adminName  The admin name for the local Galaxy.
	 */
	public void setAdminName(GalaxyAccountEmail adminName) {
		this.adminName = adminName;
	}

	/**
	 * @return The admin password for Galaxy.
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * Sets the admin password for Galaxy.
	 * @param adminPassword  The admin password for Galaxy.
	 */
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	/**
	 * @return The admin API key for Galaxy.
	 */
	public String getAdminAPIKey() {
		return adminAPIKey;
	}

	/**
	 * Sets the admin API key for Galaxy.
	 * @param adminAPIKey The admin API key for Galaxy.
	 */
	public void setAdminAPIKey(String adminAPIKey) {
		this.adminAPIKey = adminAPIKey;
	}

	/**
	 * @return The name of the 1st regular user in Galaxy.
	 */
	public GalaxyAccountEmail getUser1Name() {
		return user1Name;
	}

	/**
	 * Sets the name of the 1st regular user in Galaxy.
	 * @param user1Name The name for the 1st regular user.
	 */
	public void setUser1Name(GalaxyAccountEmail user1Name) {
		this.user1Name = user1Name;
	}

	/**
	 * @return The password of the 1st regular user in Galaxy.
	 */
	public String getUser1Password() {
		return user1Password;
	}

	/**
	 * Sets the password for the 1st regular user in Galaxy.
	 * @param user1Password  The password for the 1st regular user in Galaxy.
	 */
	public void setUser1Password(String user1Password) {
		this.user1Password = user1Password;
	}

	/**
	 * @return The api key for the 1st regular user in Galaxy.
	 */
	public String getUser1APIKey() {
		return user1APIKey;
	}

	/**
	 * Sets the api key for the 1st regular user in Galaxy.
	 * @param user1apiKey  The api key for the 1st regular user in Galaxy.
	 */
	public void setUser1APIKey(String user1apiKey) {
		user1APIKey = user1apiKey;
	}

	/**
	 * @return The name of a 2nd regular user in Galaxy.
	 */
	public GalaxyAccountEmail getUser2Name() {
		return user2Name;
	}

	/**
	 * Sets the name of a 2nd regular user in Galaxy.
	 * @param user2Name The name of a 2nd regular user in Galaxy.
	 */
	public void setUser2Name(GalaxyAccountEmail user2Name) {
		this.user2Name = user2Name;
	}

	/**
	 * @return The password of a 2nd regular user in Galaxy.
	 */
	public String getUser2Password() {
		return user2Password;
	}

	/**
	 * Sets the password of a 2nd regular user in Galaxy.
	 * @param user2Password  The password of a 2nd regular user in Galaxy.
	 */
	public void setUser2Password(String user2Password) {
		this.user2Password = user2Password;
	}

	/**
	 * @return The API key for the 2nd regular user in Galaxy.
	 */
	public String getUser2APIKey() {
		return user2APIKey;
	}

	/**
	 * Sets the api key for the 2nd regular user in Galaxy.
	 * @param user2apiKey
	 */
	public void setUser2APIKey(String user2apiKey) {
		user2APIKey = user2apiKey;
	}

	/**
	 * @return The name of a non existent admin user in Galaxy.
	 */
	public GalaxyAccountEmail getNonExistentGalaxyAdminName() {
		return nonExistentGalaxyAdminName;
	}

	/**
	 * Sets the name of a non existent admin user in Galaxy.
	 * @param nonExistentGalaxyAdminName The name of a non existent admin user in Galaxy. 
	 */
	public void setNonExistentGalaxyAdminName(
			GalaxyAccountEmail nonExistentGalaxyAdminName) {
		this.nonExistentGalaxyAdminName = nonExistentGalaxyAdminName;
	}

	/**
	 * @return The name of a non existent regular user in Galaxy.
	 */
	public GalaxyAccountEmail getNonExistentGalaxyUserName() {
		return nonExistentGalaxyUserName;
	}

	/**
	 * Sets the name of a non existent regular user in Galaxy.
	 * @param nonExistentGalaxyUserName The name of a non existent regular user in Galaxy.
	 */
	public void setNonExistentGalaxyUserName(
			GalaxyAccountEmail nonExistentGalaxyUserName) {
		this.nonExistentGalaxyUserName = nonExistentGalaxyUserName;
	}

	/**
	 * @return The GalaxyInstance for the admin user.
	 */
	public GalaxyInstance getGalaxyInstanceAdmin() {
		return galaxyInstanceAdmin;
	}

	/**
	 * Sets the GalaxyInstance for the admin user.
	 * @param galaxyInstanceAdmin The GalaxyInstance for the admin user.
	 */
	public void setGalaxyInstanceAdmin(GalaxyInstance galaxyInstanceAdmin) {
		this.galaxyInstanceAdmin = galaxyInstanceAdmin;
	}

	/**
	 * @return The GalaxyInstance for the 1st regular user.
	 */
	public GalaxyInstance getGalaxyInstanceUser1() {
		return galaxyInstanceUser1;
	}

	/**
	 * Sets the GalaxyInstance for the 1st regular user.
	 * @param galaxyInstanceUser1 The GalaxyInstance for the 1st regular user.
	 */
	public void setGalaxyInstanceUser1(GalaxyInstance galaxyInstanceUser1) {
		this.galaxyInstanceUser1 = galaxyInstanceUser1;
	}

	/**
	 * @return The GalaxyInstance for the 2nd regular user.
	 */
	public GalaxyInstance getGalaxyInstanceUser2() {
		return galaxyInstanceUser2;
	}

	/**
	 * Sets the GalaxyInstance for the 2nd regular user.
	 * @param galaxyInstanceUser2  The GalaxyInstance for the 2nd regular user.
	 */
	public void setGalaxyInstanceUser2(GalaxyInstance galaxyInstanceUser2) {
		this.galaxyInstanceUser2 = galaxyInstanceUser2;
	}

	/**
	 * @return The BootStrapper for Galaxy.
	 */
	public BootStrapper getBootStrapper() {
		return bootStrapper;
	}

	/**
	 * Sets the BootStrapper for Galaxy.
	 * @param bootStrapper The BootStrapper for Galaxy.
	 */
	public void setBootStrapper(BootStrapper bootStrapper) {
		this.bootStrapper = bootStrapper;
	}

	/**
	 * @return The daemon object for the Galaxy process.
	 */
	public GalaxyDaemon getGalaxyDaemon() {
		return galaxyDaemon;
	}

	/**
	 * Sets the daemon object for the Galaxy process.
	 * @param galaxyDaemon The daemon object for the Galaxy process.
	 */
	public void setGalaxyDaemon(GalaxyDaemon galaxyDaemon) {
		this.galaxyDaemon = galaxyDaemon;
	}

	/**
	 * @return The GalaxyProperties object.
	 */
	public GalaxyProperties getGalaxyProperties() {
		return galaxyProperties;
	}

	/**
	 * Sets the GalaxyProperties object.
	 * @param galaxyProperties  The GalaxyProperties object.
	 */
	public void setGalaxyProperties(GalaxyProperties galaxyProperties) {
		this.galaxyProperties = galaxyProperties;
	}

	/**
	 * @return An invalid Galaxy URL.
	 */
	public URL getInvalidGalaxyURL() {
		return invalidGalaxyURL;
	}

	/**
	 * Sets an invalid GalaxyURL.
	 * @param invalidGalaxyURL An invalid Galaxy URl.
	 */
	public void setInvalidGalaxyURL(URL invalidGalaxyURL) {
		this.invalidGalaxyURL = invalidGalaxyURL;
	}

	/**
	 * @return An invalid Galaxy user name.
	 */
	public UploaderAccountName getInvalidGalaxyUserName() {
		return invalidGalaxyUserName;
	}

	/**
	 * Sets an invalid Galaxy user name.
	 * @param invalidGalaxyUserName An invalid Galaxy user name.
	 */
	public void setInvalidGalaxyUserName(
			UploaderAccountName invalidGalaxyUserName) {
		this.invalidGalaxyUserName = invalidGalaxyUserName;
	}
	
	/**
	 * Reads the given file into a string.
	 * @param file  The file to read.
	 * @return  A string of the file contents.
	 * @throws IOException
	 */
	private String readFile(Path file) throws IOException {
		String fileContents = "";
		List<String> lines = Files.readAllLines(file, Charset.defaultCharset());

		for (String line : lines) {
			fileContents += line + "\n";
		}
		
		return fileContents;
	}
	
	/**
	 * Constructs a workflow in the test Galaxy with the given workflow file.
	 * @param workflowFile  The file to construct a workflow from.
	 * @return  The id of the workflow constructed.
	 * @throws IOException If there was an error reading the workflow file.
	 */
	private String constructTestWorkflow(Path workflowFile) throws IOException,RuntimeException {
		checkNotNull(workflowFile, "workflowFile is null");
				
		String content = readFile(workflowFile);
		
		WorkflowsClient workflowsClient = galaxyInstanceWorkflowUser.getWorkflowsClient();
		Workflow workflow = workflowsClient.importWorkflow(content);
		
		if (workflow != null && workflow.getId() != null) {	
			return workflow.getId();
		} else {
			throw new RuntimeException("Error building workflow from file " + workflowFile + " in Galaxy " + 
					galaxyURL);
		}
	}
	
	/**
	 * Sets up the single input workflow.
	 */
	private void setupWorkflowSingleInput() {
		try {
			Path workflowFile = Paths.get(LocalGalaxy.class.getResource(
					"GalaxyWorkflowSingleInput.ga").toURI());
			
			// build workflow
			singleInputWorkflowId = constructTestWorkflow(workflowFile);
			singleInputWorkflowLabel = "fastq";
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets up a sleep workflow.
	 */
	private void setupWorkflowSleep() {
		try {
			Path workflowFile = Paths.get(LocalGalaxy.class.getResource(
					"Galaxy-Workflow-Sleep.ga").toURI());
			
			// build workflow
			workflowSleepId = constructTestWorkflow(workflowFile);
			workflowSleepLabel = "Input Dataset";
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets up a filter workflow.
	 */
	private void setupWorkflowFilter() {
		try {
			Path workflowFile = Paths.get(LocalGalaxy.class.getResource(
					"GalaxyWorkflowFilter.ga").toURI());
			
			// build workflow
			workflowFilterId = constructTestWorkflow(workflowFile);
			workflowFilterLabel = "Input Dataset";
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets up a collection list workflow.
	 */
	private void setupWorkflowCollectionListPaired() {
		try {
			Path workflowFile = Paths.get(LocalGalaxy.class.getResource(
					"workflow_collection_list_paired.ga").toURI());
			
			// build workflow
			worklowCollectionListId = constructTestWorkflow(workflowFile);
			workflowCollectionListLabel = "input_list";
			
			// find a workflow id that's invalid
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets up a test of the core pipeline workflow.
	 */
	private void setupWorkflowCorePipelineTest() {
		try {
			Path workflowFile = Paths.get(LocalGalaxy.class.getResource(
					"Workflow-Core_Pipeline_Test.ga").toURI());
			
			this.workflowCorePipelineTestTree = Paths.get(LocalGalaxy.class.getResource(
					"phylogeneticTree.txt").toURI());
			this.workflowCorePipelineTestMatrix = Paths.get(LocalGalaxy.class.getResource(
					"snpMatrix.tsv").toURI());
			this.workflowCorePipelineTestSnpTable = Paths.get(LocalGalaxy.class.getResource(
					"snpTable.tsv").toURI());
			
			// build workflow
			this.workflowCorePipelineTestId = constructTestWorkflow(workflowFile);
			this.workflowCorePipelineTestSequenceFilesLabel = "sequence_reads";
			this.workflowCorePipelineTestReferenceLabel = "reference";
			this.workflowCorePipelineTestTreeName = "phylogeneticTree.txt";
			this.workflowCorePipelineTestTabelName = "snpTable.tsv";
			this.workflowCorePipelineTestMatrixName = "snpMatrix.tsv";
			
			// find a workflow id that's invalid
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the workflow id for a single input workflow.
	 * @return  The id of the workflow.
	 */
	public String getSingleInputWorkflowId() {
		return singleInputWorkflowId;
	}

	/**
	 * Gets the input label for the single input workflow.
	 * @return  The input label for the single input workflow.
	 */
	public String getSingleInputWorkflowLabel() {
		return singleInputWorkflowLabel;
	}
	
	/**
	 * Gets a workflow ID for a collection list workflow.
	 * @return  The id of a collection list workflow.
	 */
	public String getWorklowCollectionListId() {
		return worklowCollectionListId;
	}

	/**
	 * Gets the workflow input label of a collection list workflow.
	 * @return  The workflow input label of a collection list workflow.
	 */
	public String getWorkflowCollectionListLabel() {
		return workflowCollectionListLabel;
	}
	
	/**
	 * Gets a test core pipeline workflow id.
	 * @return  A test core pipeline workflow id.
	 */
	public String getWorkflowCorePipelineTestId() {
		return workflowCorePipelineTestId;
	}

	/**
	 * Gets a test core pipeline label for sequence files.
	 * @return  A test core pipeline label for sequence files.
	 */
	public String getWorkflowCorePipelineTestSequenceFilesLabel() {
		return workflowCorePipelineTestSequenceFilesLabel;
	}

	/**
	 * Gets a test core pipeline label for reference files.
	 * @return  A test core pipeline label for reference files.
	 */
	public String getWorkflowCorePipelineTestReferenceLabel() {
		return workflowCorePipelineTestReferenceLabel;
	}

	/**
	 * Gets the name for the tree output from the test workflow.
	 * @return  The name for the tree output from the test workflow. 
	 */
	public String getWorkflowCorePipelineTestTreeName() {
		return workflowCorePipelineTestTreeName;
	}

	/**
	 * Gets the name for the matrix output from the test workflow.
	 * @return The name for the matrix output from the test workflow.
	 */
	public String getWorkflowCorePipelineTestMatrixName() {
		return workflowCorePipelineTestMatrixName;
	}

	/**
	 * Gets the name for the snp table label from the test workflow.
	 * @return The name for the snp table label from the test workflow.
	 */
	public String getWorkflowCorePipelineTestTabelName() {
		return workflowCorePipelineTestTabelName;
	}

	/**
	 * Sets up all workflows for this local galaxy.
	 */
	public void setupWorkflows() {
		setupWorkflowSingleInput();
		setupWorkflowCollectionListPaired();
		setupWorkflowCorePipelineTest();
		setupWorkflowFilter();
		setupWorkflowSleep();
		
		invalidWorkflowId = "invalid";
	}
	
	/**
	 * Gets an id for a test sleep workflow.
	 * @return  The id for a test sleep workflow.
	 */
	public String getWorkflowSleepId() {
		return workflowSleepId;
	}

	/**
	 * Gets an input label for a test sleep workflow.
	 * @return  The input label for a test sleep workflow.
	 */
	public String getWorkflowSleepLabel() {
		return workflowSleepLabel;
	}
	
	/**
	 * Gets an id for a test filter workflow.
	 * @return  The id for a test filter workflow.
	 */
	public String getWorkflowFilterId() {
		return workflowFilterId;
	}

	/**
	 * Gets an input label for a test filter workflow.
	 * @return  The input label for a test filter workflow.
	 */
	public String getWorkflowFilterLabel() {
		return workflowFilterLabel;
	}

	/**
	 * Gets a workflow id that is invalid.
	 * @return  An invalid workflow id.
	 */
	public String getInvalidWorkflowId() {
		return invalidWorkflowId;
	}

	/**
	 * Gets an invalid workflow label.
	 * @return  An invalid workflow label.
	 */
	public String getInvalidWorkflowLabel() {
		return invalidWorkflowLabel;
	}

	/**
	 * Sets a url used for testing purposes.
	 * @param testGalaxyURL  A url used for testing purposes.
	 */
	public void setTestGalaxyURL(URL testGalaxyURL) {
		this.testGalaxyURL = testGalaxyURL;
	}
	
	/**
	 * Gets a URL for testing purposes.
	 * @return  A URL for testing purposes.
	 */
	public URL getTestGalaxyURL() {
		return testGalaxyURL;
	}
	
	/**
	 * Gets an example output snp matrix.
	 * @return  An example output snp matrix.
	 */
	public Path getWorkflowCorePipelineTestMatrix() {
		return workflowCorePipelineTestMatrix;
	}

	/**
	 * Gets an exampe output phylogenetic tree.
	 * @return  An example output phylogenetic tree.
	 */
	public Path getWorkflowCorePipelineTestTree() {
		return workflowCorePipelineTestTree;
	}

	/**
	 * Gets an example output snp table.
	 * @return  An example output snp table.
	 */
	public Path getWorkflowCorePipelineTestSnpTable() {
		return workflowCorePipelineTestSnpTable;
	}

	/**
	 * Gets a user name for running workflows.
	 * @return A user name for running workflows.
	 */
	public GalaxyAccountEmail getWorkflowUserName() {
		return workflowUserName;
	}

	/**
	 * Sets a user name for running workflows.
	 * @param workflowUserName  A user name for running workflows.
	 */
	public void setWorkflowUserName(GalaxyAccountEmail workflowUserName) {
		this.workflowUserName = workflowUserName;
	}

	/**
	 * Gets a password for a user for running workflows.
	 * @return  A password for a user for running workflows.
	 */
	public String getWorkflowUserPassword() {
		return workflowUserPassword;
	}

	/**
	 * Sets a password for a user running workflows.
	 * @param workflowUserPassword  A password for a user running workflows.
	 */
	public void setWorkflowUserPassword(String workflowUserPassword) {
		this.workflowUserPassword = workflowUserPassword;
	}

	/**
	 * Gets an API key for a user running workflows.
	 * @return An API key for a user running workflows.
	 */
	public String getWorkflowUserAPIKey() {
		return workflowUserAPIKey;
	}

	/**
	 * Sets an API key for a user running workflows.
	 * @param workflowUserAPIKey  An API key for a user running workflows.
	 */
	public void setWorkflowUserAPIKey(String workflowUserAPIKey) {
		this.workflowUserAPIKey = workflowUserAPIKey;
	}

	/**
	 * Gets a GalaxyInstace for a workflow user.
	 * @return  A GalaxyInstance for a workflow user.
	 */
	public GalaxyInstance getGalaxyInstanceWorkflowUser() {
		return galaxyInstanceWorkflowUser;
	}

	/**
	 * Sets a GalaxyInstance for a workflow user.
	 * @param galaxyInstanceWorkflowUser  A GalaxyInstance for a workflow user.
	 */
	public void setGalaxyInstanceWorkflowUser(
			GalaxyInstance galaxyInstanceWorkflowUser) {
		this.galaxyInstanceWorkflowUser = galaxyInstanceWorkflowUser;
	}
}
