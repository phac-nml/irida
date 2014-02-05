package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Class containing methods used to build new Galaxy libraries and change
 * permissions on a library.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxyLibraryBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibraryBuilder.class);

	private GalaxyInstance galaxyInstance;
	private GalaxySearch galaxySearch;

	/**
	 * Creates a new GalaxyLibrary object for working with Galaxy libraries.
	 * 
	 * @param galaxyInstance
	 *            The GalaxyInstance object to work with.
	 * @param galaxySearch
	 *            The GalaxySearch object to use for searching.
	 */
	public GalaxyLibraryBuilder(GalaxyInstance galaxyInstance,
			GalaxySearch galaxySearch) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(galaxySearch, "galaxySearch is null");

		this.galaxyInstance = galaxyInstance;
		this.galaxySearch = galaxySearch;
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
	public Library buildEmptyLibrary(GalaxyObjectName libraryName)
			throws CreateLibraryException {
		checkNotNull(libraryName, "libraryName is null");

		Library persistedLibrary;

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		Library library = new Library(libraryName.getName());
		persistedLibrary = librariesClient.createLibrary(library);

		if (persistedLibrary != null) {
			logger.debug("Created library=" + library.getName() + " libraryId="
					+ persistedLibrary.getId() + " in Galaxy url="
					+ galaxyInstance.getGalaxyUrl());

			return persistedLibrary;
		} else {
			throw new CreateLibraryException("Could not create library named "
					+ libraryName);
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
			GalaxyObjectName folderName) throws CreateLibraryException {
		checkNotNull(library, "library is null");
		checkNotNull(folderName, "folderName is null");

		LibraryFolder folder = null;
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();

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
					+ folderName + " within library " + library.getName());
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
			LibraryFolder libraryFolder, UploadObjectName folderName)
			throws CreateLibraryException {
		checkNotNull(library, "library is null");
		checkNotNull(libraryFolder, "libraryFolder is null");
		checkNotNull(folderName, "folderName is null");

		LibraryFolder folder = null;

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();

		if (librariesClient != null) {
			LibraryFolder newFolder = new LibraryFolder();
			newFolder.setName(folderName.getName());
			newFolder.setFolderId(libraryFolder.getId());

			folder = librariesClient.createFolder(library.getId(), newFolder);
		}

		if (folder == null) {
			throw new CreateLibraryException("Could not create library folder="
					+ folderName + " within folder " + libraryFolder.getName()
					+ " in library " + library.getName());
		} else {
			return folder;
		}
	}

	/**
	 * Changes the owner of the library to the given user emails within Galaxy.
	 * 
	 * @param library
	 *            The Library to change the owner of.
	 * @param userEmail
	 *            The regular user email address to own the library.
	 * @param adminEmail
	 *            The admin email address to own the library, used so
	 *            administrator can upload files to this library.
	 * @return The Library we changed the owner of.
	 * @throws ChangeLibraryPermissionsException
	 *             If an error occurred changing the library permissions.
	 * @throws GalaxyUserNoRoleException
	 *             If no corresponding roles for the Galaxy users could be
	 *             found.
	 */
	public Library changeLibraryOwner(Library library,
			GalaxyAccountEmail userEmail, GalaxyAccountEmail adminEmail)
			throws ChangeLibraryPermissionsException, GalaxyUserNoRoleException {
		checkNotNull(library, "library is null");
		checkNotNull(library.getId(), "library.getId() is null");
		checkNotNull(userEmail, "userEmail is null");

		Library changedLibrary = null;
		Role userRole = galaxySearch.findUserRoleWithEmail(userEmail);
		if (userRole == null) {
			throw new GalaxyUserNoRoleException(
					"Could not find a role for user with email=" + userEmail);
		}

		Role adminRole = galaxySearch.findUserRoleWithEmail(adminEmail);
		if (adminRole == null) {
			throw new GalaxyUserNoRoleException(
					"Could not find a role for admin user with email="
							+ adminEmail);
		}

		LibraryPermissions permissions = new LibraryPermissions();
		permissions.getAccessInRoles().add(userRole.getId());
		permissions.getAccessInRoles().add(adminRole.getId());
		permissions.getAddInRoles().add(userRole.getId());
		permissions.getAddInRoles().add(adminRole.getId());
		permissions.getManageInRoles().add(userRole.getId());
		permissions.getManageInRoles().add(adminRole.getId());
		permissions.getModifyInRoles().add(userRole.getId());
		permissions.getModifyInRoles().add(adminRole.getId());

		ClientResponse response = galaxyInstance.getLibrariesClient()
				.setLibraryPermissions(library.getId(), permissions);

		if (ClientResponse.Status.OK.equals(response.getClientResponseStatus())) {
			logger.debug("Changed owner of library=" + library.getName()
					+ " libraryId=" + library.getId() + " to roles:"
					+ userRole.getName() + "," + adminRole.getName()
					+ " in Galaxy url=" + galaxyInstance.getGalaxyUrl());

			changedLibrary = library;

			return changedLibrary;
		} else {
			throw new ChangeLibraryPermissionsException(
					"Could not change the owner for library="
							+ library.getName());
		}
	}
}
