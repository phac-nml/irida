package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;

import com.github.jmchilton.blend4j.galaxy.beans.Library;

/**
 * An object containing information about constructed data libraries in Galaxy.
 *
 */
public class GalaxyUploadResult implements UploadResult {
	
	public static final String LIBRARY_API_BASE = "api/libraries";
	
	private String libraryId;
	private GalaxyProjectName libraryName;
	private GalaxyAccountEmail ownerName;
	private URL libraryAPIURL;
	private URL sharedDataURL;

	/**
	 * Constructs a new GalaxyUploadResult with the given information.
	 * @param library  The Library the data was uploaded to.
	 * @param libraryName  The name of the library the data was uploaded to.
	 * @param ownerName  The owner of the library, null if no permissions were changed (existing library).
	 * @param galaxyURL  The URL of the Galaxy we uploaded to.
	 * @throws MalformedURLException  If the galaxyURL is invalid.
	 */
	public GalaxyUploadResult(Library library, GalaxyProjectName libraryName,
			GalaxyAccountEmail ownerName, String galaxyURL)
			throws MalformedURLException {
		checkNotNull(library, "library is null");
		checkNotNull(libraryName, "libraryName is null");
		checkNotNull(galaxyURL, "galaxyURL is null");

		String actualLibraryName = library.getName();

		if (!libraryName.getName().equals(actualLibraryName)) {
			throw new RuntimeException("Library names are out of sync");
		}

		this.libraryId = library.getId();
		this.libraryName = libraryName;
		this.ownerName = ownerName;

		this.libraryAPIURL = libraryToAPIURL(library, galaxyURL);
		this.sharedDataURL = galaxyURLToLibraryURL(galaxyURL);
	}

	/**
	 * Converts the returned Library information to a URL describing the location of the library in
	 * 	the Galaxy API.
	 * @param library  The library where data was uploaded.
	 * @param galaxyURL  The base galaxy URL.
	 * @return  A URL describing the location of the library in the galaxy API.
	 * @throws MalformedURLException  If there was an issue constructing the URL.
	 */
	private URL libraryToAPIURL(Library library, String galaxyURL)
			throws MalformedURLException {
		String urlPath = LIBRARY_API_BASE + library.getId();
		String domainPath = galaxyURL;

		if (domainPath.endsWith("/")) {
			domainPath = domainPath.substring(0, domainPath.length() - 1);
		}

		return new URL(domainPath + "/" + urlPath);
	}

	/**
	 * Given a galaxy URL builds a URL describing the location of all data libraries.
	 * @param galaxyURL  The base URL for Galaxy.
	 * @return  A URL describing the location of all data libraries.
	 * @throws MalformedURLException  If there was an issue when constructing the URL.
	 */
	private URL galaxyURLToLibraryURL(String galaxyURL)
			throws MalformedURLException {
		String domainPath = galaxyURL;

		if (domainPath.endsWith("/")) {
			domainPath = domainPath.substring(0, domainPath.length() - 1);
		}

		return new URL(domainPath + "/library");
	}

	/**
	 * Gets the API url for the data library the files were uploaded to.
	 * @return the Galaxy API url for the data library.
	 */
	public URL getLibraryAPIURL() {
		return libraryAPIURL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getDataLocation() {
		return sharedDataURL;
	}

	/**
	 * Gets the id of the library in Galaxy.
	 * @return  The id of the library in Galaxy.
	 */
	public String getLibraryId() {
		return libraryId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadProjectName getLocationName() {
		return libraryName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploaderAccountName ownerOfNewLocation() {
		return ownerName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean newLocationCreated() {
		return ownerName != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(libraryId, libraryName, ownerName, libraryAPIURL, sharedDataURL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxyUploadResult other = (GalaxyUploadResult) obj;
		
		return Objects.equals(this.libraryId, other.libraryId) &&
				Objects.equals(this.libraryName, other.libraryName) &&
				Objects.equals(this.ownerName, other.ownerName) &&
				Objects.equals(this.libraryAPIURL, other.libraryAPIURL) &&
				Objects.equals(this.sharedDataURL, other.sharedDataURL);
	}
}
