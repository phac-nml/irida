package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset.Source;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.sun.jersey.api.client.ClientResponse;

import static com.google.common.base.Preconditions.*;

/**
 * Class for working with Galaxy Histories.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyHistory {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyHistory.class);
	
	private GalaxyInstance galaxyInstance;
	private GalaxySearch galaxySearch;
	
	/**
	 * Builds a new GalaxyHistory with the given Galaxy instance and GalaxySearch objects.
	 * @param galaxyInstance  The Galaxy Instance to use to connect to Galaxy.
	 * @param galaxySearch The GalaxySearch object to use.
	 */
	public GalaxyHistory(GalaxyInstance galaxyInstance, GalaxySearch galaxySearch) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(galaxySearch, "galaxySearch is null");
		
		this.galaxyInstance = galaxyInstance;
		this.galaxySearch = galaxySearch;
	}
	
	/**
	 * Creates a new History for running a workflow.
	 * @return  A new History for running a workflow.
	 */
	public History newHistoryForWorkflow() {
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();

		History history = new History();
		history.setName(UUID.randomUUID().toString());
		return historiesClient.create(history);
	}
	
	/**
	 * Transfers a dataset from a Galaxy library into a history for a workflow.
	 * @param libraryFileId
	 * @param history
	 * @return
	 */
	public HistoryDetails libraryDatasetToHistory(String libraryFileId, History history) {
		checkNotNull(libraryFileId, "libraryFileId is null");
		checkNotNull(history, "history is null");
		
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		
		HistoryDataset historyDataset = new HistoryDataset();
		historyDataset.setSource(Source.LIBRARY);
		historyDataset.setContent(libraryFileId);
		
		return historiesClient.createHistoryDataset(
				history.getId(), historyDataset);
	}
	
	/**
	 * Uploads a file to a given history.
	 * @param path  The path to the file to upload.
	 * @param fileType The file type of the file to upload.
	 * @param history  The history to upload the file into.
	 * @return A Dataset object for the uploaded file.
	 * @throws UploadException  If there was an issue uploading the file to Galaxy.
	 * @throws GalaxyDatasetNotFoundException If a Dataset could not be found for the uploaded file to Galaxy.
	 */
	public Dataset fileToHistory(Path path, String fileType, History history) throws UploadException, GalaxyDatasetNotFoundException {
		checkNotNull(path, "path is null");
		checkNotNull(fileType, "fileType is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history id is null");
		checkState(path.toFile().exists(), "path " + path + " does not exist");
		
		File file = path.toFile();
		
		ToolsClient toolsClient = galaxyInstance.getToolsClient();
		
		FileUploadRequest uploadRequest = new FileUploadRequest(history.getId(), file);
		uploadRequest.setFileType(fileType);
		
		ClientResponse clientResponse = 
				toolsClient.uploadRequest(uploadRequest);
		
		if (clientResponse == null) {
			throw new UploadException("Could not upload " + file + " to history " + history.getId() +
					" ClientResponse is null");
		} else if (!ClientResponse.Status.OK.equals(clientResponse.getClientResponseStatus())) {
			String message = "Could not upload " + file + " to history " + history.getId() +
					" ClientResponse: " + clientResponse.getClientResponseStatus() + " " +
					clientResponse.getEntity(String.class);
			
			logger.error(message);
			
			throw new UploadException(message);
		} else {
			return galaxySearch.getDatasetForFileInHistory(file.getName(), history);
		}
	}
}
