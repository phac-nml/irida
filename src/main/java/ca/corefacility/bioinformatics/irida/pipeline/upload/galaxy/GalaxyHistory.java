package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

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
	
	private GalaxyInstance galaxyInstance;
	private GalaxySearch galaxySearch;
	
	/**
	 * Builds a new GalaxyHistory with the given Galaxy instance and GalaxySearch objects.
	 * @param galaxyInstance  The Galaxy Instance to use to connect to Galaxy.
	 * @param galaxySearch The GalaxySearch object to use.
	 */
	public GalaxyHistory(GalaxyInstance galaxyInstance, GalaxySearch galaxySearch) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		
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
	
	public Dataset fileToHistory(Path path, History history) throws UploadException, GalaxyDatasetNotFoundException {
		checkNotNull(path, "path is null");
		checkNotNull(history, "history is null");
		checkNotNull(history.getId(), "history id is null");
		checkState(path.toFile().exists(), "path " + path + " does not exist");
		
		File file = path.toFile();
		
		ToolsClient toolsClient = galaxyInstance.getToolsClient();
		
		//Map<String, String> extraParameters;
		FileUploadRequest uploadRequest = new FileUploadRequest(history.getId(), file);
		uploadRequest.setFileType("fastqsanger");
		//extraParameters = uploadRequest.getExtraParameters();
		//extraParameters.put("link_data_only", "link_to_files");
		
		ClientResponse clientResponse = 
				toolsClient.uploadRequest(uploadRequest);
		
		if (clientResponse != null &&
			ClientResponse.Status.OK.equals(clientResponse.getClientResponseStatus())) {
			
			return galaxySearch.getDatasetForFileInHistory(file.getName(), history);
		} else {
			throw new UploadException("Could not upload " + file + " to history " + history.getId());
		}
	}
}
