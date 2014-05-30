package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Handles submission of workflows to Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowManager {
	
	private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowManager.class);
	
	private static final String OK_STATE = "ok";
	
	private GalaxyHistory galaxyHistory;
	private GalaxyInstance galaxyInstance;
	
	/**
	 * Constructs a new GalaxyWorkflowSubmitter with the given information.
	 * @param galaxyInstance  A Galaxyinstance defining the Galaxy to submit to.
	 * @param galaxyHistory  A GalaxyHistory for methods on operating with Galaxy histories.
	 */
	public GalaxyWorkflowManager(GalaxyInstance galaxyInstance, GalaxyHistory galaxyHistory) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(galaxyHistory, "galaxyHistory is null");
		
		this.galaxyHistory = galaxyHistory;
		this.galaxyInstance = galaxyInstance;
	}
	
	private void checkWorkflowIdValid(String workflowId) {
		checkNotNull(workflowId, "workflow id is null");
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		if (workflowsClient.showWorkflow(workflowId) == null) {
			throw new RuntimeException("workflow with id " + workflowId + " does not exist in Galaxy instance");
		}
	}
	
	/**
	 * Starts the execution of a workflow with the given list of files and a given workflow ID.
	 * @param fastqFile  A fastq file to start the fastqc workflow. 
	 * @param workflowId  The id of the workflow to start.
	 * @throws GalaxyDatasetNotFoundException 
	 * @throws UploadException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void runFastQCWorkflow(File fastqFile, String workflowId) throws UploadException, GalaxyDatasetNotFoundException, InterruptedException, IOException {
		checkNotNull(fastqFile, "files are null");
		checkArgument(fastqFile.exists(), "fastqFile " + fastqFile + " does not exist");
		checkWorkflowIdValid(workflowId);
		
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		
		History workflowHistory = galaxyHistory.newHistoryForWorkflow();
		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(workflowId);
		
		// upload dataset to history
		Dataset inputDataset = galaxyHistory.fileToHistory(fastqFile.toPath(), workflowHistory);
		
		// setup workflow inputs
		String workflowInput1Id = null;
		for(final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
			final String label = inputEntry.getValue().getLabel();
			if(label.equals("fastq")) {
				workflowInput1Id = inputEntry.getKey();
			}
		}

		WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(workflowHistory.getId()));
		inputs.setWorkflowId(workflowDetails.getId());
		inputs.setInput(workflowInput1Id, new WorkflowInputs.WorkflowInput(inputDataset.getId(), WorkflowInputs.InputSourceType.HDA));
		
		// execute workflow
		WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		logger.debug("Running workflow in history " + output.getHistoryId());
		for(String outputId : output.getOutputIds()) {
			logger.debug("Workflow writing to output id " + outputId);
		}
		
		// poll history until workflow completed
		HistoryDetails details;
		do {
			Thread.sleep(5000);
			details = historiesClient.showHistory(output.getHistoryId());
			logger.debug("Details for history " + details.getId() + ": state=" + details.getState());
		} while (!OK_STATE.equals(details.getState()));
		
		// workflow complete
		for(String outputId : output.getOutputIds()) {
			Dataset dataset = historiesClient.showDataset(output.getHistoryId(), outputId);
			HistoryContentsProvenance provenance = historiesClient.showProvenance(output.getHistoryId(), outputId);

			logger.debug("output " + dataset.getName() + " generated from " +
					provenance.getToolId() + " with parameters " + provenance.getParameters());
			
			URL downloadURL = new URL(dataset.getFullDownloadUrl());
			logger.debug("download URL=" + downloadURL);
			File outputFile = downloadURLToTempFile(downloadURL);
			
			logger.debug("output file " + dataset.getName() + " written to " + outputFile);
		}
	}
	
	private File downloadURLToTempFile(URL url) throws IOException {
		Path path = Files.createTempFile("galaxy_output", null);
		InputStream in = url.openStream();
		FileOutputStream fos = new FileOutputStream(path.toFile());

		int length = -1;
		byte[] buffer = new byte[4096];// buffer for portion of data from
		                                // connection
		while ((length = in.read(buffer)) > -1) {
		    fos.write(buffer, 0, length);
		}
		fos.close();
		in.close();
		
		return path.toFile();
	}
	
	public static void main(String[] args) throws UploadException, GalaxyDatasetNotFoundException, InterruptedException, IOException {
		File fastqFile = new File("/home/aaron/workspace/irida-api/cholera-files-subsample/fastq/2010EL-1749.fastq");
		
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get("http://galaxy-staging.corefacility.ca", "558a75474e8e3987104bdb430ffddaf2");
		GalaxySearch galaxySearch = new GalaxySearch(galaxyInstance);
		GalaxyHistory galaxyHistory = new GalaxyHistory(galaxyInstance, galaxySearch);
		GalaxyWorkflowManager workflowSubmitter = new GalaxyWorkflowManager(galaxyInstance, galaxyHistory);
		
		workflowSubmitter.runFastQCWorkflow(fastqFile, "3f5830403180d620");
	}
}
