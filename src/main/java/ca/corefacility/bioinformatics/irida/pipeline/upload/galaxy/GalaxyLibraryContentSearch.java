package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.sun.jersey.api.client.UniformInterfaceException;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;

public class GalaxyLibraryContentSearch extends GalaxySearch<List<LibraryContent>, String> {

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
	public GalaxyLibraryContentSearch(LibrariesClient librariesClient, URL galaxyURL) {
		checkNotNull(librariesClient, "librariesClient is null");
		checkNotNull(galaxyURL, "galaxyURL is null");

		this.librariesClient = librariesClient;
		this.galaxyURL = galaxyURL;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LibraryContent> findById(String id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");

		try {
			List<LibraryContent> libraryContents = librariesClient.getLibraryContents(id);
	
			if (libraryContents == null) {
				throw new NoGalaxyContentFoundException("Could not find library content for id " + id + " in Galaxy "
						+ galaxyURL);
			} else {
				return libraryContents;
			}
		} catch (UniformInterfaceException e) {
			throw new NoGalaxyContentFoundException("Could not find library content for id " + id + " in Galaxy "
					+ galaxyURL, e);
		}
	}
	

	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the
	 * LibraryContent object.
	 * 
	 * @param libraryId
	 *            The library to get all contents from.
	 * @return A Map mapping the path of the library content to the
	 *         LibraryContent object.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	public Map<String, LibraryContent> libraryContentAsMap(String libraryId) throws ExecutionManagerObjectNotFoundException {
		checkNotNull(libraryId, "libraryId is null");

		List<LibraryContent> libraryContents = findById(libraryId);

		return libraryContents.stream().collect(Collectors.toMap(LibraryContent::getName, Function.identity()));
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
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	public LibraryContent findLibraryContentWithId(String libraryId, GalaxyFolderPath folderPath)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(folderPath, "folderPath is null");

		List<LibraryContent> libraryContents = findById(libraryId);

		Optional<LibraryContent> content = libraryContents.stream()
				.filter(c -> c.getType().equals("folder") && c.getName().equals(folderPath.getName())).findFirst();
		if (content.isPresent()) {
			return content.get();
		}

		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
				+ galaxyURL);
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
		} catch (ExecutionManagerObjectNotFoundException e) {
			return false;
		}
	}
}