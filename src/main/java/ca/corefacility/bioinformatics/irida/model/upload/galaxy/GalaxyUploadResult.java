package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.net.MalformedURLException;
import java.net.URL;

import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;

import com.github.jmchilton.blend4j.galaxy.beans.Library;

/**
 * An object containing information about constructed data libraries in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploadResult implements UploadResult {
	private String libraryId;
	private GalaxyObjectName libraryName;
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
	public GalaxyUploadResult(Library library, GalaxyObjectName libraryName,
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
		String urlPath = library.getUrl();
		String domainPath = galaxyURL;

		if (domainPath.endsWith("/")) {
			domainPath = domainPath.substring(0, domainPath.length() - 1);
		}

		if (urlPath.startsWith("/")) {
			urlPath = urlPath.substring(1);
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
	 * @return
	 */
	public URL getLibraryAPIURL() {
		return libraryAPIURL;
	}

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

	@Override
	public UploadObjectName getLocationName() {
		return libraryName;
	}

	@Override
	public UploaderAccountName ownerOfNewLocation() {
		return ownerName;
	}

	@Override
	public boolean newLocationCreated() {
		return ownerName != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((libraryAPIURL == null) ? 0 : libraryAPIURL.hashCode());
		result = prime * result
				+ ((libraryId == null) ? 0 : libraryId.hashCode());
		result = prime * result
				+ ((libraryName == null) ? 0 : libraryName.hashCode());
		result = prime * result
				+ ((ownerName == null) ? 0 : ownerName.hashCode());
		result = prime * result
				+ ((sharedDataURL == null) ? 0 : sharedDataURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxyUploadResult other = (GalaxyUploadResult) obj;
		if (libraryAPIURL == null) {
			if (other.libraryAPIURL != null)
				return false;
		} else if (!libraryAPIURL.equals(other.libraryAPIURL))
			return false;
		if (libraryId == null) {
			if (other.libraryId != null)
				return false;
		} else if (!libraryId.equals(other.libraryId))
			return false;
		if (libraryName == null) {
			if (other.libraryName != null)
				return false;
		} else if (!libraryName.equals(other.libraryName))
			return false;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		if (sharedDataURL == null) {
			if (other.sharedDataURL != null)
				return false;
		} else if (!sharedDataURL.equals(other.sharedDataURL))
			return false;
		return true;
	}
}
