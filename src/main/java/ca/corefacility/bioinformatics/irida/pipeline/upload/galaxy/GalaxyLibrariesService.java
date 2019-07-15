package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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

import ca.corefacility.bioinformatics.irida.exceptions.UploadErrorException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadTimeoutException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.DeleteGalaxyObjectFailedException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.DataStorage;
import ca.corefacility.bioinformatics.irida.util.FileUtils;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.GalaxyObject;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;

/**
 * A service class for dealing with Galaxy libraries.
 * 
 *
 */
public class GalaxyLibrariesService {

	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibrariesService.class);

	private LibrariesClient librariesClient;
	
	private final ExecutorService executor;

	private final int libraryPollingTime;
	private final int libraryUploadTimeout;

	/**
	 * State a library dataset should be in on proper upload.
	 */
	private static final String LIBRARY_OK_STATE = "ok";
	
	/**
	 * Failure states for a library dataset.  Derived from
	 * https://github.com/galaxyproject/galaxy/blob/release_16.10/lib/galaxy/model/__init__.py#L1645
	 */
	private static List<String> LIBRARY_FAIL_STATES = Lists.newArrayList("paused", "error", "failed_metadata", "discarded");

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
	 * @param threadPoolSize
	 *            The thread pool size for parallel polling of Galaxy to check if uploads are finished.
	 */
	public GalaxyLibrariesService(LibrariesClient librariesClient, final int libraryPollingTime,
			final int libraryUploadTimeout, final int threadPoolSize) {
		checkNotNull(librariesClient, "librariesClient is null");
		checkArgument(libraryPollingTime > 0, "libraryPollingTime=" + libraryPollingTime + " must be positive");
		checkArgument(libraryUploadTimeout > 0, "libraryUploadTimeout=" + libraryUploadTimeout + " must be positive");
		checkArgument(libraryUploadTimeout > libraryPollingTime, "libraryUploadTimeout=" + libraryUploadTimeout
				+ " must be greater then libraryPollingTime=" + libraryPollingTime);
		checkArgument(threadPoolSize > 0, "threadPoolSize=" + threadPoolSize + " must be positive");
		
		logger.debug("Setting libraryPollingTime=" + libraryPollingTime + ", libraryUploadTimeout=" + libraryUploadTimeout 
				+ ", threadPoolSize=" + threadPoolSize);

		this.librariesClient = librariesClient;
		this.libraryPollingTime = libraryPollingTime;
		this.libraryUploadTimeout = libraryUploadTimeout;
		
		executor = Executors.newFixedThreadPool(threadPoolSize);
	}
	
	/**
	 * Builds a new empty library with the given name.
	 * 
	 * @param libraryName
	 *            The name of the new library.
	 * @return A Library object for the newly created library.
	 * @throws CreateLibraryException
	 *             If no library could be created.
	 */
	public Library buildEmptyLibrary(GalaxyProjectName libraryName)
			throws CreateLibraryException {
		checkNotNull(libraryName, "libraryName is null");

		Library persistedLibrary;

		Library library = new Library(libraryName.getName());
		persistedLibrary = librariesClient.createLibrary(library);

		if (persistedLibrary != null) {
			logger.debug("Created library=" + library.getName() + " libraryId="
					+ persistedLibrary.getId());

			return persistedLibrary;
		} else {
			throw new CreateLibraryException("Could not create library named "
					+ libraryName);
		}
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
			Library library, DataStorage dataStorage)
			throws UploadException {
		checkNotNull(paths, "paths is null");
		final int pollingTimeMillis = libraryPollingTime*1000;

		Map<Path, String> datasetLibraryIdsMap = new HashMap<>();

		try {
			// upload all files to library first
			for (Path path : paths) {
				InputFileType fileType = getFileType(path);
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

							if (LIBRARY_FAIL_STATES.contains(libraryDataset.getState())) {
								throw new UploadErrorException("Error: upload to Galaxy library id=" + library.getId()
										+ " name=" + library.getName() + " for dataset id=" + datasetLibraryId
										+ " name=" + libraryDataset.getName() + " failed with state="
										+ libraryDataset.getState());
							}
						}
					}
					
					return null;
				}
			});
			
			waitForLibraries.get(libraryUploadTimeout, TimeUnit.SECONDS);
		} catch (RuntimeException | IOException e) {
			throw new UploadException(e);
		} catch (TimeoutException e) {
			throw new UploadTimeoutException("Timeout while uploading, time limit = " + libraryUploadTimeout + " seconds", e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof UploadErrorException) {
				throw (UploadErrorException)e.getCause();
			} else {
				throw new UploadException(e);
			}
		} catch (InterruptedException e) {
			throw new UploadException(e);
		}

		return datasetLibraryIdsMap;
	}
	
	/**
	 * Given a {@link Path}, gets the {@link InputFileType} for the data type to upload to Galaxy.
	 * @param path The path to upload.
	 * @return The {@link InputFileType} for the data to upload to Galaxy.
	 * @throws IOException If there was an error reading the file to determine the file type.
	 */
	private InputFileType getFileType(Path path) throws IOException {
		checkArgument(path.toFile().exists(), "path=[" + path + "] does not exist");
		
		return (FileUtils.isGzipped(path) ? InputFileType.FASTQ_SANGER_GZ : InputFileType.FASTQ_SANGER); 
	}

	/**
	 * Deletes the Galaxy library with the given id.
	 * 
	 * @param libraryId
	 *            The id of the library to delete.
	 * @throws DeleteGalaxyObjectFailedException
	 *             If there was a failure to delete the library.
	 */
	public void deleteLibrary(String libraryId) throws DeleteGalaxyObjectFailedException {
		try {
			ClientResponse response = librariesClient.deleteLibraryRequest(libraryId);
			if (!ClientResponse.Status.OK.equals(response.getClientResponseStatus())) {
				throw new DeleteGalaxyObjectFailedException("Could not delete library with id " + libraryId
						+ ", status=" + response.getClientResponseStatus() + ", content="
						+ response.getEntity(String.class));
			}
		} catch (RuntimeException e) {
			throw new DeleteGalaxyObjectFailedException("Error while deleting library with id " + libraryId, e);
		}
	}
}
