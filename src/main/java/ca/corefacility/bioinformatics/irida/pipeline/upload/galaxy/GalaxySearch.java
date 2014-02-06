package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Class containing methods used to search for information within a Galaxy
 * instance.
 * 
 * @author aaron
 * 
 */
public class GalaxySearch {
	private GalaxyInstance galaxyInstance;

	/**
	 * Builds a new Galaxy search object around the passed GalaxyInstance.
	 * 
	 * @param galaxyInstance
	 *            The GalaxyInstance object used to connect to Galaxy.
	 */
	public GalaxySearch(GalaxyInstance galaxyInstance) {
		checkNotNull(galaxyInstance, "galaxyInstance is null");

		this.galaxyInstance = galaxyInstance;
	}

	/**
	 * Given an email, finds a corresponding users private Role object in Galaxy
	 * with that email.
	 * 
	 * @param email
	 *            The email of the user to search.
	 * @return A private Role object of the user with the corresponding email.
	 * @throws GalaxyUserNoRoleException If no role for the user could be found.
	 */
	public Role findUserRoleWithEmail(GalaxyAccountEmail email) throws GalaxyUserNoRoleException {
		checkNotNull(email, "email is null");

		RolesClient rolesClient = galaxyInstance.getRolesClient();
		if (rolesClient != null) {
			for (Role curr : rolesClient.getRoles()) {
				if (email.getName().equals(curr.getName())) {
					return curr;
				}
			}
		}

		throw new GalaxyUserNoRoleException("No role found for " + email);
	}

	/**
	 * Given an email, finds a corresponding User object in Galaxy with that
	 * email.
	 * 
	 * @param email
	 *            The email of the user to search.
	 * @return A User object of the user with the corresponding email.
	 * @throws GalaxyUserNotFoundException If the user could not be found.
	 */
	public User findUserWithEmail(GalaxyAccountEmail email) throws GalaxyUserNotFoundException {
		checkNotNull(email, "email is null");

		UsersClient usersClient = galaxyInstance.getUsersClient();
		if (usersClient != null) {
			for (User curr : usersClient.getUsers()) {
				if (email.getName().equals(curr.getEmail())) {
					return curr;
				}
			}
		}

		throw new GalaxyUserNotFoundException("No user found with email " + email);
	}

	/**
	 * Given a library ID, searches for the corresponding Library object.
	 * 
	 * @param libraryId
	 *            The libraryId to search for.
	 * @return A Library object for this Galaxy library.
	 * @throws NoLibraryFoundException If a library could not be found.
	 */
	public Library findLibraryWithId(String libraryId) throws NoLibraryFoundException {
		checkNotNull(libraryId, "libraryId is null");

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> libraries = librariesClient.getLibraries();
		for (Library curr : libraries) {
			if (libraryId.equals(curr.getId())) {
				return curr;
			}
		}

		throw new NoLibraryFoundException("No library found for id " + libraryId);
	}

	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the
	 * LibraryContent object.
	 * 
	 * @param libraryId
	 *            The library to get all contents from.
	 * @return A Map mapping the path of the library content to the
	 *         LibraryContent object.
	 * @throws NoGalaxyContentFoundException If no library could be found.
	 */
	public Map<String, LibraryContent> libraryContentAsMap(String libraryId) throws NoGalaxyContentFoundException {
		checkNotNull(libraryId, "libraryId is null");

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient
				.getLibraryContents(libraryId);

		if (libraryContents != null) {
			Map<String, LibraryContent> map = new HashMap<String, LibraryContent>();

			for (LibraryContent content : libraryContents) {
				map.put(content.getName(), content);
			}
			
			return map;
		}

		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId);
	}

	/**
	 * Given a library name, searches for a list of matching Library objects.
	 * 
	 * @param libraryName
	 *            The name of the library to search for.
	 * @return A list of Library objects matching the given name.
	 * @throws NoLibraryFoundException If no libraries could be found.
	 */
	public List<Library> findLibraryWithName(GalaxyObjectName libraryName) throws NoLibraryFoundException {
		checkNotNull(libraryName, "libraryName is null");

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> allLibraries = librariesClient.getLibraries();

		if (allLibraries != null) {
			List<Library> libraries = new LinkedList<Library>();

			for (Library curr : allLibraries) {
				if (libraryName.getName().equals(curr.getName())) {
					libraries.add(curr);
				}
			}
			
			if (libraries.size() > 0) {
				return libraries;
			}
		}

		throw new NoLibraryFoundException("No library could be found with name " + libraryName);
	}

	/**
	 * Given a libraryId and a folder name, search for the corresponding
	 * LibraryContent object within this library.
	 * 
	 * @param libraryId
	 *            The ID of the library to search for.
	 * @param folderName
	 *            The name of the folder to search for (only finds first
	 *            instance of this folder name).
	 * @return A LibraryContent within the given library with the given name, or
	 *         null if no such folder exists.
	 * @throws NoGalaxyContentFoundException If no Galaxy content was found.
	 */
	public LibraryContent findLibraryContentWithId(String libraryId,
			GalaxyFolderPath folderPath) throws NoGalaxyContentFoundException {
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(folderPath, "folderPath is null");

		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient
				.getLibraryContents(libraryId);

		if (libraryContents != null) {
			for (LibraryContent content : libraryContents) {
				if ("folder".equals(content.getType())) {
					if (folderPath.getName().equals(content.getName())) {
						return content;
					}
				}
			}
		}

		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId);
	}

	/**
	 * Determines if the passed Galaxy user exists within the Galaxy instance.
	 * 
	 * @param galaxyUserEmail
	 *            The user email address to check.
	 * @return True if this user exists, false otherwise.
	 */
	public boolean galaxyUserExists(GalaxyAccountEmail galaxyUserEmail) {
		try {
			return findUserWithEmail(galaxyUserEmail) != null;			
		} catch (GalaxyUserNotFoundException e) {
			return false;
		}
	}
}
