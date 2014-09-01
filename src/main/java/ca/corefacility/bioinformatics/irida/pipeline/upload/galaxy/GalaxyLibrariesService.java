package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.GalaxyObject;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;

/**
 * A service class for dealing with Galaxy libraries.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@SuppressWarnings("unused")
public class GalaxyLibrariesService {
	
	private LibrariesClient librariesClient;
	
	/**
	 * Polling time in milliseconds to poll a Galaxy library 
	 * to check if datasets have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5*1000;
	
	/**
	 * Timeout in milliseconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5*60*1000;
	
	/**
	 * State a library dataset should be in on proper upload.
	 */
	private static final String LIBRARY_OK_STATE = "ok";
	
	/**
	 * Builds a new GalaxyLibrariesService with the given LibrariesClient.
	 * @param librariesClient  The LibrariesClient used to interact with Galaxy libraries.
	 */
	public GalaxyLibrariesService(LibrariesClient librariesClient) {
		this.librariesClient = librariesClient;
	}
	
	/**
	 * Uploads the given file to a library with the given information.
	 * @param path
	 * @param fileType
	 * @param library
	 * @param dataStorage
	 * @return
	 * @throws UploadException
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
	
			GalaxyObject uploadObject = 
					librariesClient.uploadFilesystemPaths(library.getId(), upload);
			
			return uploadObject.getId();
		} catch (RuntimeException e) {
			throw new UploadException(e);
		} 
	}
}
