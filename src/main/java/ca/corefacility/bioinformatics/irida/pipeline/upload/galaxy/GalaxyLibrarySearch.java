package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.DataSourceSearch;

import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;

/**
 * Class containing methods used to search for library information in Galaxy
 * 
 * 
 */
public class GalaxyLibrarySearch implements DataSourceSearch<Library, String, GalaxyProjectName>{
	
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(String id) {
		try {
			return findById(id) != null;
		} catch (ExecutionManagerObjectNotFoundException e) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Library> findByName(GalaxyProjectName name)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(name, "name is null");

		List<Library> allLibraries = librariesClient.getLibraries();

		if (allLibraries != null) {
			List<Library> libraries = allLibraries.stream()
					.filter((lib) -> lib.getName().equals(name.getName())).collect(Collectors.toList());

			if (libraries.size() > 0) {
				return libraries;
			}
		}

		throw new NoLibraryFoundException("No library could be found with name " + name + " in Galaxy "
				+ galaxyURL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean existsByName(GalaxyProjectName name) {
		try {
			return findByName(name) != null;
		} catch (ExecutionManagerObjectNotFoundException e) {
			return false;
		}
	}
}
