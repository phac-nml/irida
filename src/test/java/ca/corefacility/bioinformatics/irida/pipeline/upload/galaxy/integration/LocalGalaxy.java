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

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

/**
 * A class containing information about the running instance of Galaxy for integration testing.
 *
 */
public class LocalGalaxy {

	private URL galaxyURL;
	private URL invalidGalaxyURL;
	private URL testGalaxyURL;

	private GalaxyAccountEmail adminName;
	private String adminPassword;
	private String adminAPIKey;

	private GalaxyInstance galaxyInstanceAdmin;
	
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
	private String constructTestWorkflow(Path workflowFile) throws IOException, RuntimeException {
		checkNotNull(workflowFile, "workflowFile is null");
				
		String content = readFile(workflowFile);
		
		WorkflowsClient workflowsClient = galaxyInstanceAdmin.getWorkflowsClient();
		Workflow workflow = workflowsClient.importWorkflow(content, false);
		
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
}
