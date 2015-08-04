package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;

/**
 * Class containing methods used to build new Galaxy libraries and change
 * permissions on a library.
 * 
 * 
 */
public class GalaxyLibraryBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibraryBuilder.class);

	private LibrariesClient librariesClient;
	private URL galaxyURL;

	/**
	 * Creates a new GalaxyLibrary object for working with Galaxy libraries.
	 * 
	 * @param librariesClient
	 *            The LibrariesClient to start working with libraries.
	 *  @param galaxyRoleSearch
	 *             The GalaxyRoleSearch used to search for Galaxy Roles.
	 *  @param galaxyURL
	 *              The URL to the Galaxy instance.
	 */
	public GalaxyLibraryBuilder(LibrariesClient librariesClient,
				GalaxyRoleSearch galaxyRoleSearch, URL galaxyURL) {
		checkNotNull(librariesClient, "librariesClient is null");
		checkNotNull(galaxyRoleSearch, "galaxyRoleSearch is null");
		checkNotNull(galaxyURL, "galaxyURL is null");

		this.librariesClient = librariesClient;
		this.galaxyURL = galaxyURL;
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
					+ persistedLibrary.getId() + " in Galaxy url="
					+ galaxyURL);

			return persistedLibrary;
		} else {
			throw new CreateLibraryException("Could not create library named "
					+ libraryName + " in Galaxy " + galaxyURL);
		}
	}

	/**
	 * Creates a new folder within the given library under the "root" directory.
	 * 
	 * @param library
	 *            The library to create the folder within.
	 * @param folderName
	 *            The name of the folder to create.
	 * @return A LibraryFolder object representing this folder.
	 * @throws CreateLibraryException
	 *             If no library folder could be created.
	 */
	public LibraryFolder createLibraryFolder(Library library,
			UploadFolderName folderName) throws CreateLibraryException {
		checkNotNull(library, "library is null");
		checkNotNull(folderName, "folderName is null");

		LibraryFolder folder = null;

		if (librariesClient != null) {
			LibraryContent rootContent = librariesClient.getRootFolder(library
					.getId());

			if (rootContent != null) {
				LibraryFolder newFolder = new LibraryFolder();
				newFolder.setName(folderName.getName());
				newFolder.setFolderId(rootContent.getId());

				folder = librariesClient.createFolder(library.getId(),
						newFolder);
			}
		}

		if (folder == null) {
			throw new CreateLibraryException("Could not create library folder="
					+ folderName + " within library " + library.getName() +
					" in Galaxy " + galaxyURL);
		} else {
			return folder;
		}
	}

	/**
	 * Creates a new folder within the given library under the given
	 * LibraryFolder.
	 * 
	 * @param library
	 *            The library to create the folder within.
	 * @param libraryFolder
	 *            The folder to create the new folder within.
	 * @param folderName
	 *            The name of the folder to create.
	 * @return A LibraryFolder object representing this folder.
	 * @throws CreateLibraryException
	 *             If no library folder could be created.
	 */
	public LibraryFolder createLibraryFolder(Library library,
			LibraryFolder libraryFolder, UploadFolderName folderName)
			throws CreateLibraryException {
		checkNotNull(library, "library is null");
		checkNotNull(libraryFolder, "libraryFolder is null");
		checkNotNull(folderName, "folderName is null");

		LibraryFolder folder = null;

		if (librariesClient != null) {
			LibraryFolder newFolder = new LibraryFolder();
			newFolder.setName(folderName.getName());
			newFolder.setFolderId(libraryFolder.getId());

			folder = librariesClient.createFolder(library.getId(), newFolder);
		}

		if (folder == null) {
			throw new CreateLibraryException("Could not create library folder="
					+ folderName + " within folder " + libraryFolder.getName()
					+ " in library " + library.getName() +
					" in Galaxy " + galaxyURL);
		} else {
			return folder;
		}
	}
}
