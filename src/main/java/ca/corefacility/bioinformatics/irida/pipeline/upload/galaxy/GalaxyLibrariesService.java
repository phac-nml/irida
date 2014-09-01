package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.GalaxyObject;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;

/**
 * A service class for dealing with Galaxy libraries.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyLibrariesService {

	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibrariesService.class);

	private LibrariesClient librariesClient;

	/**
	 * Polling time in milliseconds to poll a Galaxy library to check if
	 * datasets have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5 * 1000;

	/**
	 * Timeout in milliseconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5 * 60 * 1000;

	/**
	 * State a library dataset should be in on proper upload.
	 */
	private static final String LIBRARY_OK_STATE = "ok";

	/**
	 * Builds a new GalaxyLibrariesService with the given LibrariesClient.
	 * 
	 * @param librariesClient
	 *            The LibrariesClient used to interact with Galaxy libraries.
	 */
	public GalaxyLibrariesService(LibrariesClient librariesClient) {
		this.librariesClient = librariesClient;
	}

	/**
	 * Uploads the given file to a library with the given information.
	 * 
	 * @param path
	 *            The path of the file to upload.
	 * @param fileType
	 *            The type of the file to upload.
	 * @param library
	 *            The library to upload the file into.
	 * @param dataStorage
	 *            The {@link DataStorage} method to apply to this dataset.
	 * @return A dataset id for the dataset in this library.
	 * @throws UploadException
	 *             If there was an issue uploading the file to the library.
	 */
	public String fileToLibrary(Path path, InputFileType fileType,
			Library library, DataStorage dataStorage) throws UploadException {
		checkNotNull(path, "path is null");
		checkNotNull(fileType, "fileType is null");
		checkNotNull(library, "library is null");
		checkNotNull(library.getId(), "library id is null");
		checkState(path.toFile().exists(), "path " + path + " does not exist");

		File file = path.toFile();

		try {
			LibraryContent rootContent = librariesClient.getRootFolder(library
					.getId());
			FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload();
			upload.setFolderId(rootContent.getId());

			upload.setContent(file.getAbsolutePath());
			upload.setName(file.getName());
			upload.setLinkData(DataStorage.LOCAL.equals(dataStorage));
			upload.setFileType(fileType.toString());

			GalaxyObject uploadObject = librariesClient.uploadFilesystemPaths(
					library.getId(), upload);

			return uploadObject.getId();
		} catch (RuntimeException e) {
			throw new UploadException(e);
		}
	}

	/**
	 * Uploads a set of files to a given library, waiting until all uploads are
	 * complete.
	 * 
	 * @param paths
	 *            The set of paths to upload.
	 * @param fileType
	 *            The file type of the file to upload.
	 * @param library
	 *            The library to initially upload the file into.
	 * @param dataStorage
	 *            The type of DataStorage strategy to use.
	 * @return An @{link Map} of paths and ids for each dataset object in this
	 *         library.
	 * @throws UploadException
	 *             If there was an issue uploading the file to Galaxy.
	 */
	public Map<Path, String> filesToLibraryWait(Set<Path> paths,
			InputFileType fileType, Library library, DataStorage dataStorage)
			throws UploadException {
		checkNotNull(paths, "paths is null");

		Map<Path, String> datasetLibraryIdsMap = new HashMap<>();

		try {
			// upload all files to library first
			for (Path path : paths) {
				String datasetLibraryId = fileToLibrary(path, fileType,
						library, dataStorage);
				datasetLibraryIdsMap.put(path, datasetLibraryId);
			}

			// wait for uploads to finish
			for (Path path : paths) {
				String datasetLibraryId = datasetLibraryIdsMap.get(path);

				LibraryDataset libraryDataset = librariesClient.showDataset(
						library.getId(), datasetLibraryId);
				long startTime = System.currentTimeMillis();
				while (!LIBRARY_OK_STATE.equals(libraryDataset.getState())) {
					long timeDifference = System.currentTimeMillis()
							- startTime;
					if (timeDifference > LIBRARY_TIMEOUT) {
						throw new UploadException("Error: timeout ("
								+ timeDifference + "ms > " + LIBRARY_TIMEOUT
								+ ") when polling Galaxy data library "
								+ library.getId() + " for dataset "
								+ libraryDataset.getId());
					} else {
						logger.debug("Waiting for library dataset "
								+ libraryDataset.getId()
								+ " to be finished processing, in state "
								+ libraryDataset.getState());
						Thread.sleep(LIBRARY_POLLING_TIME);

						libraryDataset = librariesClient.showDataset(
								library.getId(), datasetLibraryId);
					}
				}
			}
		} catch (RuntimeException e) {
			throw new UploadException(e);
		} catch (InterruptedException e) {
			throw new UploadException(e);
		}

		return datasetLibraryIdsMap;
	}
}
