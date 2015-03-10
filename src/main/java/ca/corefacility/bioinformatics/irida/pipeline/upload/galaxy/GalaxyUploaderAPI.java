package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.LibraryContentId;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadEventListener;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * A class defining an API for uploading samples to a remote Galaxy instance.
 * 
 *
 */
public class GalaxyUploaderAPI {

	/**
	 * Sets default filetype for fastq files uploaded to Galaxy libraries.
	 */
	private static final InputFileType DEFAULT_FILE_TYPE = InputFileType.FASTQ_SANGER;

	private static final Logger logger = LoggerFactory.getLogger(GalaxyUploaderAPI.class);

	private static final GalaxyFolderName ILLUMINA_FOLDER_NAME = new GalaxyFolderName("illumina_reads");
	private static final GalaxyFolderName REFERENCES_FOLDER_NAME = new GalaxyFolderName("references");
	private static final GalaxyFolderPath ILLUMINA_FOLDER_PATH = new GalaxyFolderPath("/illumina_reads");
	private static final GalaxyFolderPath REFERENCES_FOLDER_PATH = new GalaxyFolderPath("/references");

	private GalaxyInstance galaxyInstance;
	private GalaxyAccountEmail adminEmail;
	private GalaxyLibrarySearch galaxyLibrarySearchAdmin;
	private GalaxyLibraryContentSearch galaxyLibraryContentSearchAdmin;
	private GalaxyRoleSearch galaxyRoleSearchAdmin;
	private GalaxyUserSearch galaxyUserSearchAdmin;
	private GalaxyLibraryBuilder galaxyLibrary;
	private Uploader.DataStorage dataStorage = Uploader.DataStorage.REMOTE;

	private List<UploadEventListener> eventListeners = new LinkedList<UploadEventListener>();

	/**
	 * Builds a new GalaxyAPI instance with the given information.
	 * 
	 * @param galaxyURL
	 *            The URL to the Galaxy instance.
	 * @param adminEmail
	 *            An administrators email address for the Galaxy instance.
	 * @param adminAPIKey
	 *            A corresponding administrators API key for the Galaxy
	 *            instance.
	 * @throws ConstraintViolationException
	 *             If the adminEmail is invalid.
	 * @throws GalaxyConnectException
	 *             If an error occred when connecting to Galaxy.
	 */
	public GalaxyUploaderAPI(URL galaxyURL, @Valid GalaxyAccountEmail adminEmail, String adminAPIKey)
			throws ConstraintViolationException, GalaxyConnectException {
		checkNotNull(galaxyURL, "galaxyURL is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(adminAPIKey, "apiKey is null");

		galaxyInstance = GalaxyInstanceFactory.get(galaxyURL.toString(), adminAPIKey);
		this.adminEmail = adminEmail;

		if (galaxyInstance == null) {
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + galaxyURL + ", adminEmail="
					+ adminEmail);
		}
		
		galaxyLibrarySearchAdmin = new GalaxyLibrarySearch(galaxyInstance.getLibrariesClient(), galaxyURL);
		galaxyLibraryContentSearchAdmin = new GalaxyLibraryContentSearch(galaxyInstance.getLibrariesClient(), galaxyURL);
		galaxyRoleSearchAdmin = new GalaxyRoleSearch(galaxyInstance.getRolesClient(),
				galaxyURL);
		galaxyUserSearchAdmin = new GalaxyUserSearch(galaxyInstance.getUsersClient(), galaxyURL);
		galaxyLibrary = new GalaxyLibraryBuilder(galaxyInstance.getLibrariesClient(),
				galaxyRoleSearchAdmin, galaxyURL);

		if (!isConnected()) {
			throw new GalaxyConnectException("Could not create GalaxyInstance with URL=" + galaxyURL + ", adminEmail="
					+ adminEmail);
		}
	}

	/**
	 * Builds a GalaxyAPI object with the given information.
	 * 
	 * @param galaxyInstance
	 *            A GalaxyInstance object pointing to the correct Galaxy
	 *            location.
	 * @param adminEmail
	 *            The administrators email address for the corresponding API key
	 *            within the GalaxyInstance.
	 * @throws ConstraintViolationException
	 *             If the adminEmail is invalid.
	 * @throws GalaxyConnectException
	 *             If an issue connecting to Galaxy occurred.
	 */
	public GalaxyUploaderAPI(GalaxyInstance galaxyInstance, @Valid GalaxyAccountEmail adminEmail)
			throws ConstraintViolationException, GalaxyConnectException {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(adminEmail, "adminEmail is null");

		this.galaxyInstance = galaxyInstance;
		this.adminEmail = adminEmail;

		URL galaxyURL;
		try {
			galaxyURL = new URL(galaxyInstance.getGalaxyUrl());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		galaxyLibrarySearchAdmin = new GalaxyLibrarySearch(galaxyInstance.getLibrariesClient(), galaxyURL);
		galaxyLibraryContentSearchAdmin = new GalaxyLibraryContentSearch(galaxyInstance.getLibrariesClient(), galaxyURL);
		galaxyRoleSearchAdmin = new GalaxyRoleSearch(galaxyInstance.getRolesClient(),
				galaxyURL);
		galaxyUserSearchAdmin = new GalaxyUserSearch(galaxyInstance.getUsersClient(), galaxyURL);
		galaxyLibrary = new GalaxyLibraryBuilder(galaxyInstance.getLibrariesClient(),
				galaxyRoleSearchAdmin, galaxyURL);

		if (!isConnected()) {
			throw new GalaxyConnectException("Could not create GalaxyInstance with URL="
					+ galaxyInstance.getGalaxyUrl() + ", adminEmail=" + adminEmail);
		}
	}

	/**
	 * Builds a GalaxyAPI object with the given information.
	 * 
	 * @param galaxyInstance
	 *            A GalaxyInstance object pointing to the correct Galaxy
	 *            location.
	 * @param adminEmail
	 *            The administrators email address for the corresponding API key
	 *            within the GalaxyInstance.
	 * @param dataStorage
	 *            If uploaded files will exist on the same or a separate
	 *            filesystem as the archive.
	 * @param galaxyLibrarySearch
	 *            A GalaxyLibrarySearch object.
	 * @param galaxyLibraryContentSearch
	 *            A GalaxyLibraryContentSearch object.
	 * @param galaxyRoleSearch
	 *            A GalaxyRoleSearch object.
	 * @param galaxyUserSearch
	 * 	          A GalaxyUserSearch object.
	 * @param galaxyLibrary
	 *            A GalaxyLibrary object.
	 * @throws ConstraintViolationException
	 *             If the adminEmail is invalid.
	 * @throws GalaxyConnectException
	 *             If an issue connecting to Galaxy occurred.
	 */
	public GalaxyUploaderAPI(GalaxyInstance galaxyInstance, @Valid GalaxyAccountEmail adminEmail, 
			GalaxyLibrarySearch galaxyLibrarySearch, GalaxyLibraryContentSearch galaxyLibraryContentSearch,
			GalaxyRoleSearch galaxyRoleSearch, GalaxyUserSearch galaxyUserSearch,
			GalaxyLibraryBuilder galaxyLibrary) throws ConstraintViolationException, GalaxyConnectException {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(galaxyLibrarySearch, "galaxyLibrarySearch is null");
		checkNotNull(galaxyLibraryContentSearch, "galaxyLibraryContentSearch is null");
		checkNotNull(galaxyRoleSearch, "galaxyRoleSearch is null");
		checkNotNull(galaxyUserSearch, "galaxyUserSearch is null");
		checkNotNull(galaxyLibrary, "galaxyLibrary is null");

		this.galaxyInstance = galaxyInstance;
		this.adminEmail = adminEmail;

		this.galaxyLibrary = galaxyLibrary;
		this.galaxyLibrarySearchAdmin = galaxyLibrarySearch;
		this.galaxyLibraryContentSearchAdmin = galaxyLibraryContentSearch;
		this.galaxyUserSearchAdmin = galaxyUserSearch;
		this.galaxyRoleSearchAdmin = galaxyRoleSearch;

		if (!isConnected()) {
			throw new GalaxyConnectException("Could not use GalaxyInstance with URL=" + galaxyInstance.getGalaxyUrl()
					+ ", adminEmail=" + adminEmail);
		}
	}

	/**
	 * Builds a data library in Galaxy with the name and owner.
	 * 
	 * @param libraryName
	 *            The name of the library to create.
	 * @param galaxyUserEmail
	 *            The name of the user who will own the galaxy library.
	 * @return A Library object for the library just created.
	 * @throws ConstraintViolationException
	 *             If the galaxyUserEmail or libraryName are invalid.
	 * @throws CreateLibraryException
	 *             If there was an error building a library (assuming Spring is
	 *             managing the API object).
	 * @throws ChangeLibraryPermissionsException
	 *             If an error occured while attempting to change the library
	 *             permissions.
	 * @throws ExecutionManagerObjectNotFoundException
	 *             If users or roles do not exist within the execution manager.
	 */
	public Library buildGalaxyLibrary(@Valid GalaxyProjectName libraryName, @Valid GalaxyAccountEmail galaxyUserEmail)
			throws CreateLibraryException, ConstraintViolationException, ChangeLibraryPermissionsException,
			ExecutionManagerObjectNotFoundException {
		checkNotNull(libraryName, "libraryName is null");
		checkNotNull(galaxyUserEmail, "galaxyUser is null");

		logger.debug("Attempt to create new library=" + libraryName + " owned by user=" + galaxyUserEmail
				+ " under Galaxy url=" + galaxyInstance.getGalaxyUrl());

		// make sure user exists and has a role before we create an empty
		// library
		if (!galaxyUserSearchAdmin.exists(galaxyUserEmail)) {
			throw new GalaxyUserNotFoundException(galaxyUserEmail, getGalaxyUrl());
		}

		if (!galaxyRoleSearchAdmin.exists(galaxyUserEmail)) {
			throw new GalaxyUserNoRoleException("Could not find role for Galaxy user with email=" + galaxyUserEmail);
		}

		Library library = galaxyLibrary.buildEmptyLibrary(libraryName);

		return galaxyLibrary.changeLibraryOwner(library, galaxyUserEmail, adminEmail);
	}

	/**
	 * Uploads the given file to the given library and LibraryFolder in Galaxy.
	 * 
	 * @param folder
	 *            The folder to place the file in Galaxy.
	 * @param file
	 *            The file to upload.
	 * @param librariesClient
	 *            The client used to connect to the Libraries Galaxy API.
	 * @param library
	 *            The library to upload the file to.
	 * @return A ClientResponse describing the status of the upload.
	 */
	private ClientResponse uploadFile(LibraryFolder folder, File file, LibrariesClient librariesClient, Library library) {
		FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload();
		upload.setFolderId(folder.getId());

		upload.setContent(file.getAbsolutePath());
		upload.setName(file.getName());
		upload.setLinkData(DataStorage.LOCAL.equals(dataStorage));
		upload.setFileType(DEFAULT_FILE_TYPE.toString());

		return librariesClient.uploadFilesystemPathsRequest(library.getId(), upload);
	}

	private boolean doUploadWithLogging(LibraryFolder persistedSampleFolder, File file,
			LibrariesClient librariesClient, Library library, String samplePath) {
		ClientResponse uploadResponse = uploadFile(persistedSampleFolder, file, librariesClient, library);

		boolean success = ClientResponse.Status.OK.equals(uploadResponse.getClientResponseStatus());

		if (success) {
			logger.debug(String
					.format("Uploaded file to Galaxy path=%s from local path=%s dataStorage=%s in library name=%s id=%s in Galaxy url=%s",
							samplePath, file.getAbsolutePath(), dataStorage, library.getName(), library.getId(),
							galaxyInstance.getGalaxyUrl()));
		} else {
			logger.debug(String
					.format("Failed to upload file to Galaxy, response=%s (%s), response message=\"%s\", path=%s from local path=%s dataStorage=%s in library name=%s id=%s in Galaxy url=%s",
							uploadResponse.getStatus(), uploadResponse.getClientResponseStatus(),
							uploadResponse.getEntity(String.class), samplePath, file.getAbsolutePath(), dataStorage,
							library.getName(), library.getId(), galaxyInstance.getGalaxyUrl()));
		}

		return success;
	}

	/**
	 * Constructs a String describing the path of the given sample within the
	 * given folder.
	 * 
	 * @param rootFolder
	 *            The folder the sample will be located within.
	 * @param sample
	 *            The sample to find the path for.
	 * @return A String describing the path of the sample.
	 */
	private String samplePath(LibraryFolder rootFolder, UploadSample sample) {
		String rootFolderName;
		if (rootFolder.getName().startsWith("/")) {
			rootFolderName = rootFolder.getName().substring(1);
		} else {
			rootFolderName = rootFolder.getName();
		}

		return String.format("/%s/%s", rootFolderName, sample.getSampleName());
	}

	/**
	 * Constructs a String describing the path of the given file for the given
	 * sample within the given folder.
	 * 
	 * @param rootFolder
	 *            The folder the sample will be located within.
	 * @param sample
	 *            The sample to find the path for.
	 * @param file
	 *            The file to find the path for.
	 * @return A String describing the path of the sample file.
	 */
	private String samplePath(LibraryFolder rootFolder, UploadSample sample, File file) {
		String rootFolderName;
		if (rootFolder.getName().startsWith("/")) {
			rootFolderName = rootFolder.getName().substring(1);
		} else {
			rootFolderName = rootFolder.getName();
		}

		return String.format("/%s/%s/%s", rootFolderName, sample.getSampleName(), file.getName());
	}
	
	private Optional<LibraryFolder> getSampleFolder(Map<String, List<LibraryContent>> libraryMap, UploadSample sample,
			LibraryFolder rootFolder) throws CreateLibraryException, LibraryUploadException {
		LibraryFolder persistedSampleFolder;
		String expectedSamplePath = samplePath(rootFolder, sample);

		// if Galaxy does not contain a folder for this sample, return empty
		if (!libraryMap.containsKey(expectedSamplePath)) {
			return Optional.absent();
		} else {
			List<LibraryContent> persistedSampleFolderAsContentList = libraryMap.get(expectedSamplePath);

			if (persistedSampleFolderAsContentList.size() == 0) {
				throw new LibraryUploadException("No LibraryContent for path " + expectedSamplePath);
			} else if (persistedSampleFolderAsContentList.size() > 1) {
				throw new LibraryUploadException("Duplicate sample folders ("
						+ persistedSampleFolderAsContentList.size() + ") for path " + expectedSamplePath);
			} else {
				LibraryContent persistedSampleFolderAsContent = persistedSampleFolderAsContentList.get(0);
				persistedSampleFolder = new LibraryFolder();
				persistedSampleFolder.setId(persistedSampleFolderAsContent.getId());
				persistedSampleFolder.setName(persistedSampleFolderAsContent.getName());
			}

			return Optional.of(persistedSampleFolder);
		}
	}
	
	private LibraryFolder buildSampleFolder(UploadSample sample,
			LibraryFolder rootFolder, Library library) throws CreateLibraryException {
		LibraryFolder persistedSampleFolder;
		String expectedSamplePath = samplePath(rootFolder, sample);

		persistedSampleFolder = galaxyLibrary.createLibraryFolder(library, rootFolder, sample.getSampleName());

		logger.debug(String.format(
				"Created Galaxy sample folder name=%s id=%s in library name=%s id=%s in Galaxy url=%s",
				expectedSamplePath, persistedSampleFolder.getId(), library.getName(), library.getId(),
				galaxyInstance.getGalaxyUrl()));

		return persistedSampleFolder;
	}

	private boolean skipUploadForFile(File sampleFile, String sampleFilePathGalaxy, LibrariesClient librariesClient,
			Library library, Map<String, List<LibraryContent>> libraryMap) throws LibraryUploadException {
		boolean shouldSkip = false;

		if (libraryMap.containsKey(sampleFilePathGalaxy)) {
			List<LibraryContent> sampleGalaxyFileContentList = libraryMap.get(sampleFilePathGalaxy);
			if (sampleGalaxyFileContentList.size() == 0) {
				throw new LibraryUploadException("sampleGalaxyFileContentList has size 0 for file "
						+ sampleFilePathGalaxy);
			} else {
				long localFileSize = sampleFile.length();

				// for every file with the same name, check the size
				for (LibraryContent fileWithSameNameAsSample : sampleGalaxyFileContentList) {
					LibraryDataset sampleFileDataset = librariesClient.showDataset(library.getId(),
							fileWithSameNameAsSample.getId());

					long galaxyFileSize = Long.parseLong(sampleFileDataset.getFileSize());

					if (galaxyFileSize == localFileSize) {
						logger.debug(String
								.format("File from local path=%s, size=%s already exists on Galaxy path=%s, size=%s in library name=%s id=%s in Galaxy url=%s skipping upload",
										sampleFile.getAbsolutePath(), localFileSize, sampleFilePathGalaxy,
										galaxyFileSize, library.getName(), library.getId(),
										galaxyInstance.getGalaxyUrl()));
						shouldSkip = true;
					} else if (galaxyFileSize == (localFileSize + 1)) {
						// It's possible for Galaxy to add an extra trailing
						// newline at the end of a file if there was no newline
						// before. This is due to Galaxy attempting to write out
						// datasets with Unix style newlines. The code for this
						// is in
						// https://bitbucket.org/galaxy/galaxy-dist/src/7e4d21621ce12e13ebbdf9fd3259df58c3ef124c/lib/galaxy/datatypes/data.py?at=stable#cl-673
						logger.debug(String
								.format("File from local path=%s, size=%s already exists on Galaxy path=%s, size=%s in library name=%s id=%s in Galaxy url=%s sizes off by 1 so assuming Galaxy added a trailing newline ... skipping upload",
										sampleFile.getAbsolutePath(), localFileSize, sampleFilePathGalaxy,
										galaxyFileSize, library.getName(), library.getId(),
										galaxyInstance.getGalaxyUrl()));
						shouldSkip = true;
					}
				}
			}
		}

		return shouldSkip;
	}

	/**
	 * Performs an upload of the sample files.
	 * 
	 * @param sample
	 *            The sample to upload.
	 * @param rootFolder
	 *            The folder on Galaxy to place the sample files.
	 * @param librariesClient
	 *            The connector to the Library Galaxy API.
	 * @param library
	 *            The library to upload the sample to.
	 * @param libraryMap
	 *            A map of existing content within this library (to make sure we
	 *            don't make duplicate sample folders).
	 * @return True if the upload was successful, false otherwise.
	 * @throws LibraryUploadException
	 *             If there was an issue uploading the file.
	 * @throws CreateLibraryException
	 *             If there was an issue creating a new library.
	 */
	private boolean uploadSample(UploadSample sample, LibraryFolder rootFolder, LibrariesClient librariesClient,
			Library library, Map<String, List<LibraryContent>> libraryMap) throws LibraryUploadException,
			CreateLibraryException {
		boolean success = true;

		LibraryFolder persistedSampleFolder;
		Optional<LibraryFolder> persistedSampleFolderOptional = getSampleFolder(libraryMap, sample, rootFolder);

		if (!persistedSampleFolderOptional.isPresent()) {
			persistedSampleFolder = buildSampleFolder(sample, rootFolder, library);
		} else {
			persistedSampleFolder = persistedSampleFolderOptional.get();
		}

		for (Path path : sample.getSampleFiles()) {
			File file = path.toFile();
			String sampleFilePathGalaxy = samplePath(rootFolder, sample, file);

			if (!skipUploadForFile(file, sampleFilePathGalaxy, librariesClient, library, libraryMap)) {
				success &= doUploadWithLogging(persistedSampleFolder, file, librariesClient, library,
						samplePath(rootFolder, sample, file));
			}
		}

		return success;
	}

	/**
	 * Uploads the given list of samples to the passed Galaxy library with the
	 * passed Galaxy user.
	 * 
	 * @param samples
	 *            The set of samples to upload.
	 * @param libraryName
	 *            The name of the library to upload to.
	 * @param galaxyUserEmail
	 *            The name of the Galaxy user who should own the files.
	 * @return A GalaxyUploadResult containing information about the location of
	 *         the uploaded files.
	 * @throws LibraryUploadException
	 *             If an error occurred.
	 * @throws CreateLibraryException
	 *             If there was an error creating the folder structure for the
	 *             library.
	 * @throws ConstraintViolationException
	 *             If the samples, libraryName or galaxyUserEmail are invalid
	 *             (assumes this object is managed by Spring).
	 * @throws ChangeLibraryPermissionsException
	 *             If an error occurred while attempting to change the library
	 *             permissions.
	 * @throws NoLibraryFoundException
	 *             If no library with the given name can be found.
	 * @throws NoGalaxyContentFoundException
	 *             If an error occured trying to find content for the library.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	public GalaxyUploadResult uploadSamples(@Valid List<UploadSample> samples, @Valid GalaxyProjectName libraryName,
			@Valid GalaxyAccountEmail galaxyUserEmail) throws LibraryUploadException, CreateLibraryException,
			ConstraintViolationException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		checkNotNull(libraryName, "libraryName is null");
		checkNotNull(samples, "samples is null");
		checkNotNull(galaxyUserEmail, "galaxyUserEmail is null");

		GalaxyUploadResult galaxyUploadResult = null;
		GalaxyAccountEmail returnedOwner = null;

		Library uploadLibrary;
		if (galaxyUserSearchAdmin.exists(galaxyUserEmail)) {

			if (galaxyLibrarySearchAdmin.existsByName(libraryName)) {
				List<Library> libraries = galaxyLibrarySearchAdmin.findByName(libraryName);
				uploadLibrary = libraries.get(0); // gets first library returned
			} else {
				uploadLibrary = buildGalaxyLibrary(libraryName, galaxyUserEmail);
				returnedOwner = galaxyUserEmail;
			}

			if (uploadFilesToLibrary(samples, uploadLibrary.getId())) {
				try {
					galaxyUploadResult = new GalaxyUploadResult(uploadLibrary, libraryName, returnedOwner,
							galaxyInstance.getGalaxyUrl());
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}

				return galaxyUploadResult;
			} else {
				throw new LibraryUploadException("Could upload files to library " + libraryName + "id="
						+ uploadLibrary.getId() + " in instance of galaxy with url=" + galaxyInstance.getGalaxyUrl());
			}
		} else {
			throw new GalaxyUserNotFoundException(galaxyUserEmail, getGalaxyUrl());
		}
	}

	/**
	 * Gets a copy of the event listeners list.
	 * 
	 * @return A copy of the event listeners list
	 */
	private synchronized List<UploadEventListener> getEventListenersCopy() {
		List<UploadEventListener> eventListenersList = new LinkedList<UploadEventListener>();
		for (UploadEventListener eventListener : eventListeners) {
			eventListenersList.add(eventListener);
		}

		return eventListenersList;
	}

	/**
	 * Updates all listeners about the progress of the upload.
	 * 
	 * @param totalSamples
	 * @param currentSample
	 * @param sampleName
	 */
	private void sampleProgressUpdate(int totalSamples, int currentSample, UploadFolderName sampleName) {
		getEventListenersCopy().forEach(
				listener -> listener.sampleProgressUpdate(totalSamples, currentSample, sampleName));
	}

	/**
	 * Uploads the passed set of files to a Galaxy library.
	 * 
	 * @param samples
	 *            The samples to upload to Galaxy.
	 * @param libraryID
	 *            A unique ID for the library, generated from
	 *            buildGalaxyLibrary(String)
	 * @return True if the files have been uploaded, false otherwise.
	 * @throws LibraryUploadException
	 *             If there was an error uploading files to the library.
	 * @throws ConstraintViolationException
	 *             If one of the GalaxySamples is invalid (assumes this object
	 *             is managed by Spring).
	 * @throws CreateLibraryException
	 *             If an error occurred while attempting to build the data
	 *             library.
	 * @throws NoGalaxyContentFoundException
	 *             If an error occurred when attempting to find content for the
	 *             library.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	public boolean uploadFilesToLibrary(@Valid List<UploadSample> samples, String libraryID)
			throws LibraryUploadException, ConstraintViolationException, CreateLibraryException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		checkNotNull(samples, "samples are null");
		checkNotNull(libraryID, "libraryID is null");

		boolean success = true;
		int numberOfSamples = samples.size();

		if (numberOfSamples > 0) {
			String errorSuffix = " in instance of galaxy with url=" + galaxyInstance.getGalaxyUrl();

			LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();

			Library library = galaxyLibrarySearchAdmin.findById(libraryID);

			Map<String, List<LibraryContent>> libraryContentMap = galaxyLibraryContentSearchAdmin.libraryContentAsMap(libraryID);

			LibraryFolder illuminaFolder;

			if (galaxyLibraryContentSearchAdmin.exists(new LibraryContentId(libraryID, ILLUMINA_FOLDER_PATH))) {
				LibraryContent illuminaContent = galaxyLibraryContentSearchAdmin.
						findById(new LibraryContentId(libraryID, ILLUMINA_FOLDER_PATH));

				illuminaFolder = new LibraryFolder();
				illuminaFolder.setId(illuminaContent.getId());
				illuminaFolder.setName(illuminaContent.getName());
			} else {
				illuminaFolder = galaxyLibrary.createLibraryFolder(library, ILLUMINA_FOLDER_NAME);
			}

			// create references folder if it doesn't exist, but we don't need
			// to put anything into it.
			if (!galaxyLibraryContentSearchAdmin.exists(new LibraryContentId(libraryID, REFERENCES_FOLDER_PATH))) {
				galaxyLibrary.createLibraryFolder(library, REFERENCES_FOLDER_NAME);
			}

			int currentSample = 0;
			for (UploadSample sample : samples) {
				if (sample != null) {
					// message about current sample being worked on
					sampleProgressUpdate(numberOfSamples, currentSample, sample.getSampleName());

					success &= uploadSample(sample, illuminaFolder, librariesClient, library, libraryContentMap);

					currentSample++;
				} else {
					throw new LibraryUploadException("Cannot upload a null sample" + errorSuffix);
				}
			}
		}

		return success;
	}

	/**
	 * The type of data storage the remote site has. Determines whether or not
	 * files should be linked within Galaxy or a duplicate should be uploaded.
	 * 
	 * @param dataStorage
	 *            DataStorage.REMOTE if there is no shared filesystem between
	 *            the archive and the remote site, or DataStorage.LOCAL if there
	 *            is a shared filesystem.
	 */
	public void setDataStorage(DataStorage dataStorage) {
		this.dataStorage = dataStorage;
	}

	/**
	 * The type of data storage the remote site has. Determines whether or not
	 * files should be linked within Galaxy or a duplicate should be uploaded.
	 * 
	 * @return DataStorage.REMOTE if there is no shared filesystem between the
	 *         archive and the remote site, or DataStorage.LOCAL if there is a
	 *         shared filesystem.
	 */
	public DataStorage getDataStorage() {
		return dataStorage;
	}

	/**
	 * Gets the URL of the Galaxy instance we are connected to.
	 * 
	 * @return A String of the URL of the Galaxy instance we are connected to.
	 */
	public URL getGalaxyUrl() {
		try {
			return new URL(galaxyInstance.getGalaxyUrl());
		} catch (MalformedURLException e) {
			// This should never really occur, don't force all calling methods
			// to catch exception
			throw new RuntimeException("Galaxy URL is malformed", e);
		}
	}

	/**
	 * Whether or not the API is properly connected to an instance of Galaxy.
	 * 
	 * @return True if there is a proper connection, false otherwise.
	 */
	public boolean isConnected() {
		try {
			return galaxyUserSearchAdmin.exists(adminEmail);
		} catch (ClientHandlerException | UniformInterfaceException e) {
			return false;
		}
	}

	/**
	 * Adds a new upload event listener.
	 * 
	 * @param eventListener
	 *            The event listener to add.
	 */
	public synchronized void addUploadEventListener(UploadEventListener eventListener) {
		checkNotNull(eventListener, "eventListener is null");
		eventListeners.add(eventListener);
	}
}
