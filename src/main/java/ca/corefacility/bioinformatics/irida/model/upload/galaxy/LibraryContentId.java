package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

/**
 * An ID to uniquely identify a LibraryContent item.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class LibraryContentId {

	private String libraryId;
	private GalaxyFolderPath folderPath;
	
	/**
	 * Builds a new LibraryContentId.
	 * @param libraryId  The ID of the library to use.
	 * @param galaxyFolderPath  The folder path of the LibraryContent item.
	 */
	public LibraryContentId(String libraryId, GalaxyFolderPath galaxyFolderPath) {
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(galaxyFolderPath, "galaxyFolderPath is null");
		
		this.libraryId = libraryId;
		this.folderPath = galaxyFolderPath;
	}

	/**
	 * Gets the library id.
	 * @return  The library id.
	 */
	public String getLibraryId() {
		return libraryId;
	}

	/**
	 * Gets the folder path.
	 * @return  The folder path.
	 */
	public GalaxyFolderPath getFolderPath() {
		return folderPath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "libraryId=" + libraryId + ",folderPath=" + folderPath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(libraryId, folderPath);
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
		if (!(obj instanceof LibraryContentId))
			return false;
		LibraryContentId other = (LibraryContentId) obj;
		
		return Objects.equals(this.libraryId, other.libraryId) &&
				Objects.equals(this.folderPath, other.folderPath);
	}
}
