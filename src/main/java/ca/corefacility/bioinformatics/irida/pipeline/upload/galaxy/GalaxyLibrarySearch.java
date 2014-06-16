package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Class containing methods used to search for library information in Galaxy
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxyLibrarySearch extends GalaxySearch<Library, String> {
	
	private LibrariesClient librariesClient;
	private URL galaxyURL;

	/**
	 * Builds a new GalaxyLibrarySearch object around the passed LibrariesClient.
	 * 
	 * @param librariesClient
	 *            The LibrariesClient to use.
	 *  @param galaxyURL
	 *            The URL to the Galaxy instance.
	 */
	public GalaxyLibrarySearch(LibrariesClient librariesClient, URL galaxyURL) {
		checkNotNull(librariesClient, "librariesClient is null");
		checkNotNull(galaxyURL, "galaxyURL is null");

		this.librariesClient = librariesClient;
		this.galaxyURL = galaxyURL;
	}

	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the
	 * LibraryContent object.
	 * 
	 * @param libraryId
	 *            The library to get all contents from.
	 * @return A Map mapping the path of the library content to the
	 *         LibraryContent object.
	 * @throws NoGalaxyContentFoundException
	 *             If no library could be found.
	 */
	public Map<String, LibraryContent> libraryContentAsMap(String libraryId) throws NoGalaxyContentFoundException {
		checkNotNull(libraryId, "libraryId is null");

		try {
			List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
	
			if (libraryContents != null) {
				Map<String, LibraryContent> map = libraryContents.stream().collect(
						Collectors.toMap(LibraryContent::getName, Function.identity()));
				return map;
			}
		} catch (UniformInterfaceException e) {
			throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
					+ galaxyURL, e);
		}

		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
				+ galaxyURL);
	}

	/**
	 * Given a library name, searches for a list of matching Library objects.
	 * 
	 * @param libraryName
	 *            The name of the library to search for.
	 * @return A list of Library objects matching the given name.
	 * @throws NoLibraryFoundException
	 *             If no libraries could be found.
	 */
	public List<Library> findLibraryWithName(GalaxyProjectName libraryName) throws NoLibraryFoundException {
		checkNotNull(libraryName, "libraryName is null");

		List<Library> allLibraries = librariesClient.getLibraries();

		if (allLibraries != null) {
			List<Library> libraries = allLibraries.stream()
					.filter((lib) -> lib.getName().equals(libraryName.getName())).collect(Collectors.toList());

			if (libraries.size() > 0) {
				return libraries;
			}
		}

		throw new NoLibraryFoundException("No library could be found with name " + libraryName + " in Galaxy "
				+ galaxyURL);
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
	 * @throws NoGalaxyContentFoundException
	 *             If no Galaxy content was found.
	 */
	public LibraryContent findLibraryContentWithId(String libraryId, GalaxyFolderPath folderPath)
			throws NoGalaxyContentFoundException {
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(folderPath, "folderPath is null");

		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);

		if (libraryContents != null) {
			Optional<LibraryContent> content = libraryContents.stream()
					.filter(c -> c.getType().equals("folder") && c.getName().equals(folderPath.getName())).findFirst();
			if (content.isPresent()) {
				return content.get();
			}
		}

		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
				+ galaxyURL);
	}

	/**
	 * Determines if a library with the given name exists.
	 * 
	 * @param libraryName
	 *            The name of the library to check.
	 * @return True if a library with this name exists, false otherwise.
	 */
	public boolean libraryExists(GalaxyProjectName libraryName) {
		try {
			List<Library> libraries = findLibraryWithName(libraryName);
			return libraries != null && libraries.size() > 0;
		} catch (NoLibraryFoundException e) {
			return false;
		}
	}

	/**
	 * Determine if the given folderPath exists within a library with the given
	 * id.
	 * 
	 * @param libraryId
	 *            The id of the library to check.
	 * @param folderPath
	 *            A path within this library to check.
	 * @return True if this path exists within this library, false otherwise.
	 */
	public boolean libraryContentExists(String libraryId, GalaxyFolderPath folderPath) {
		try {
			LibraryContent content = findLibraryContentWithId(libraryId, folderPath);
			return content != null;
		} catch (NoGalaxyContentFoundException e) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Library findById(String id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");

		List<Library> libraries = librariesClient.getLibraries();
		Optional<Library> library = libraries.stream()
				.filter((lib) -> lib.getId().equals(id)).findFirst();
		if (library.isPresent()) {
			return library.get();
		}

		throw new NoLibraryFoundException("No library found for id " + id + " in Galaxy "
				+ galaxyURL);
	}
}
