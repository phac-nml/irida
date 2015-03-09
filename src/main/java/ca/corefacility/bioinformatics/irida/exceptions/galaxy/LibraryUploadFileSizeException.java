package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import java.io.File;

import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;

/**
 * Exception thrown when there is a mismatch of file sizes within a Galaxy data
 * library.
 * 
 *
 */
public class LibraryUploadFileSizeException extends LibraryUploadException {
	private static final long serialVersionUID = 50534628995103073L;

	private File localFile;
	private Library galaxyLibrary;
	private LibraryDataset galaxyLibraryDataset;
	private String galaxyURL;

	/**
	 * Constructs a new {@link LibraryUploadFileSizeException} with the given
	 * information.
	 * 
	 * @param localFile
	 *            The local file that is being uploaded.
	 * @param galaxyLibrary
	 *            The library that we are attempting to upload into.
	 * @param galaxyLibraryDataset
	 *            The library dataset for the file that already exists.
	 * @param galaxyURL
	 *            The URL to the Galaxy instance we are uploading into.
	 */
	public LibraryUploadFileSizeException(File localFile, Library galaxyLibrary, LibraryDataset galaxyLibraryDataset,
			String galaxyURL) {
		super("File from local path=" + localFile.getAbsolutePath() + ", size=" + localFile.length()
				+ " already exists in Galaxy name=" + galaxyLibraryDataset.getFileName() + ", size="
				+ galaxyLibraryDataset.getFileSize() + " in library name=" + galaxyLibrary.getName() + " id="
				+ galaxyLibrary.getId() + " in Galaxy url=" + galaxyURL + " but file sizes are different");
		this.localFile = localFile;
		this.galaxyLibrary = galaxyLibrary;
		this.galaxyLibraryDataset = galaxyLibraryDataset;
		this.galaxyURL = galaxyURL;
	}

	/**
	 * @return The local file that is attempting to be uploaded.
	 */
	public File getLocalFile() {
		return localFile;
	}

	/**
	 * @return The Galaxy {@link Library} we are attempting to upload into.
	 */
	public Library getGalaxyLibrary() {
		return galaxyLibrary;
	}

	/**
	 * @return The Galaxy {@link LibraryDataset} for the already existing file.
	 */
	public LibraryDataset getGalaxyLibraryDataset() {
		return galaxyLibraryDataset;
	}

	/**
	 * @return The URL for the Galaxy instance we are uploading to.
	 */
	public String getGalaxyURL() {
		return galaxyURL;
	}
}
