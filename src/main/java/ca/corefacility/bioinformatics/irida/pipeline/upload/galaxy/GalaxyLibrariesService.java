package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
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
 *
 */
public class GalaxyLibrariesService {

	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibrariesService.class);

	private LibrariesClient librariesClient;
	
	private final ExecutorService executor = Executors.newFixedThreadPool(1);

	private final int libraryPollingTime;
	private final int libraryUploadTimeout;

	/**
	 * State a library dataset should be in on proper upload.
	 */
	private static final String LIBRARY_OK_STATE = "ok";

	/**
	 * Builds a new GalaxyLibrariesService with the given LibrariesClient.
	 * 
	 * @param librariesClient
	 *            The LibrariesClient used to interact with Galaxy libraries.
	 * @param libraryPollingTime
	 *            The time (in seconds) for polling a Galaxy library.
	 * @param libraryUploadTimeout
	 *            The timeout (in seconds) for waiting for files to be uploaded
	 *            to a library.
	 */
	public GalaxyLibrariesService(LibrariesClient librariesClient, final int libraryPollingTime,
			final int libraryUploadTimeout) {
		checkNotNull(librariesClient, "librariesClient is null");
		checkArgument(libraryPollingTime > 0, "libraryPollingTime=" + libraryPollingTime + " must be positive");
		checkArgument(libraryUploadTimeout > 0, "libraryUploadTimeout=" + libraryUploadTimeout + " must be positive");
		checkArgument(libraryUploadTimeout > libraryPollingTime, "libraryUploadTimeout=" + libraryUploadTimeout
				+ " must be greater then libraryPollingTime=" + libraryPollingTime);
		
		logger.debug("Setting libraryPollingTime=" + libraryPollingTime + ", libraryUploadTimeout=" + libraryUploadTimeout);

		this.librariesClient = librariesClient;
		this.libraryPollingTime = libraryPollingTime;
		this.libraryUploadTimeout = libraryUploadTimeout;
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
		final int pollingTimeMillis = libraryPollingTime*1000;

		Map<Path, String> datasetLibraryIdsMap = new HashMap<>();

		try {
			// upload all files to library first
			for (Path path : paths) {
				String datasetLibraryId = fileToLibrary(path, fileType,
						library, dataStorage);
				datasetLibraryIdsMap.put(path, datasetLibraryId);
			}

			Future<Void> waitForLibraries = executor.submit(new Callable<Void>(){
				@Override
				public Void call() throws Exception {
					// wait for uploads to finish
					for (Path path : paths) {
						String datasetLibraryId = datasetLibraryIdsMap.get(path);
						LibraryDataset libraryDataset = librariesClient.showDataset(
								library.getId(), datasetLibraryId);
						while (!LIBRARY_OK_STATE.equals(libraryDataset.getState())) {
							logger.trace("Waiting for library dataset "
									+ libraryDataset.getId()
									+ " to be finished processing, in state "
									+ libraryDataset.getState());
							Thread.sleep(pollingTimeMillis);
	
							libraryDataset = librariesClient.showDataset(
									library.getId(), datasetLibraryId);
						}
					}
					
					return null;
				}
			});
			
			waitForLibraries.get(libraryUploadTimeout, TimeUnit.SECONDS);
		} catch (RuntimeException e) {
			throw new UploadException(e);
		} catch (ExecutionException | InterruptedException | TimeoutException e) {
			throw new UploadException(e);
		}

		return datasetLibraryIdsMap;
	}
}
