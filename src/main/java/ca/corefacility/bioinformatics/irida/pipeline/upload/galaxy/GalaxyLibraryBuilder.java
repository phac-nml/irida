package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;

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
 * 
 */
public class GalaxyLibraryBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyLibraryBuilder.class);

	private LibrariesClient librariesClient;
	private GalaxyRoleSearch galaxyRoleSearch;
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
		this.galaxyRoleSearch = galaxyRoleSearch;
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
}
