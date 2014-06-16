package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
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
public class GalaxyHistoriesService extends GalaxySearch<History, String> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyHistoriesService.class);

	private HistoriesClient historiesClient;
	private ToolsClient toolsClient;
	
	/**
	 * Builds a new GalaxyHistory object for working with Galaxy Histories.
	 * @param historiesClient  The HistoriesClient for interacting with Galaxy histories.
	 * @param toolsClient  The ToolsClient for interacting with tools in Galaxy.
	 */
	public GalaxyHistoriesService(HistoriesClient historiesClient,
			ToolsClient toolsClient) {
		checkNotNull(historiesClient, "historiesClient is null");
		checkNotNull(toolsClient, "toolsClient is null");
		
		this.historiesClient = historiesClient;
		this.toolsClient = toolsClient;
	}
	
	/**
	 * Creates a new History for running a workflow.
	 * @return  A new History for running a workflow.
	 */
	public History newHistoryForWorkflow() {
		History history = new History();
		history.setName(UUID.randomUUID().toString());
		return historiesClient.create(history);
	}
	
	/**
	 * Transfers a dataset from a Galaxy library into a history for a workflow.
	 * @param libraryFileId The id of a file within a Galaxy library.
	 * @param history The history to transfer this library dataset into.
	 * @return A HistoryDetails object describing the details of the created history dataset.
	 */
	public HistoryDetails libraryDatasetToHistory(String libraryFileId, History history) {
		checkNotNull(libraryFileId, "libraryFileId is null");
		checkNotNull(history, "history is null");
				
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
			return getDatasetForFileInHistory(file.getName(), history);
		}
	}
	
	/**
	 * Gets a Dataset object for a file with the given name in the given history.
	 * @param filename  The name of the file to get a Dataset object for.
	 * @param history  The history to look for the dataset.
	 * @return The corresponding dataset for the given file name.
	 * @throws GalaxyDatasetNotFoundException  If the dataset could not be found.
	 */
	public Dataset getDatasetForFileInHistory(String filename, History history) throws GalaxyDatasetNotFoundException {
		checkNotNull(filename, "filename is null");
		checkNotNull(history, "history is null");
				
		List<HistoryContents> historyContentsList =
				historiesClient.showHistoryContents(history.getId());

		Optional<HistoryContents> h = historyContentsList.stream().
				filter((historyContents) -> filename.equals(historyContents.getName())).findFirst();
		if (h.isPresent()) {
			String dataId = h.get().getId();
			if (dataId != null) {
				Dataset dataset = historiesClient.showDataset(history.getId(), dataId);	
				if (dataset != null) {
					return dataset;
				}
			}
		}

		throw new GalaxyDatasetNotFoundException("dataset for file " + filename +
				" not found in Galaxy history " + history.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public History findById(String id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");
		
		List<History> galaxyHistories = historiesClient.getHistories();
		
		if (galaxyHistories != null) {
			Optional<History> h = galaxyHistories.stream().
					filter((history) -> id.equals(history.getId())).findFirst();
			if (h.isPresent()) {
				return h.get();
			}
		}
		
		throw new NoGalaxyHistoryException("No history for id " + id);
	}
}
