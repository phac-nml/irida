package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.LibraryContentId;

import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.sun.jersey.api.client.UniformInterfaceException;

public class GalaxyLibraryContentSearch extends GalaxySearch<LibraryContent, LibraryContentId> {

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
	 * Gets a list of library contents given a library id.
	 * @param libraryId  The library id to search for a list of library contents.
	 * @return  A list of library contents.
	 * @throws NoGalaxyContentFoundException  If no library contents could be found.
	 */
	private List<LibraryContent> getLibraryContents(String libraryId)
			throws NoGalaxyContentFoundException {
		
		try {
			List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
			
			if (libraryContents != null) {
				return libraryContents;
			}
			
		} catch (GalaxyResponseException | UniformInterfaceException e) {
			throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
					+ galaxyURL, e);
		}
		
		throw new NoGalaxyContentFoundException("Could not find library content for id " + libraryId + " in Galaxy "
				+ galaxyURL);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LibraryContent findById(LibraryContentId id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");

		List<LibraryContent> libraryContents = getLibraryContents(id.getLibraryId());

		Optional<LibraryContent> content = libraryContents.stream()
				.filter(c -> c.getType().equals("folder") && 
						c.getName().equals(id.getFolderPath().getName())).findFirst();
		if (content.isPresent()) {
			return content.get();
		}
		
		throw new NoGalaxyContentFoundException("Could not find library content for id " + id + " in Galaxy "
				+ galaxyURL);
	}
	

	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the
	 * LibraryContent object.
	 * 
	 * @param libraryId
	 *            The library to get all contents from.
	 * @return A Map mapping the path of the library content to a list of
	 *         {@link LibraryContent} objects.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	public Map<String, List<LibraryContent>> libraryContentAsMap(String libraryId)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(libraryId, "libraryId is null");

		List<LibraryContent> libraryContents = getLibraryContents(libraryId);
		return libraryContents.stream().collect(Collectors.groupingBy(LibraryContent::getName));
	}
}