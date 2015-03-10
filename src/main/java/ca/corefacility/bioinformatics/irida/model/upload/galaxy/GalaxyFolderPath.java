package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A name for a Galaxy folder path (eg. /illumina_reads/sample_name) used for
 * checking the validity of the path.  This is different from the GalaxyFolderName only in that
 * a folder path can contain '/' characters separating folders or files.
 * 
 * 
 */
public class GalaxyFolderPath {
	
	public static final char[] BLACKLIST = { '?', '(', ')', '[', ']', '\\',
		'=', '+', '<', '>', ':', ';', '"', ',', '*', '^', '|', '&', '\'', '.', ' ', '\t' };
	
	private static final Pattern invalidPathName 
		= Pattern.compile("[\\?\\(\\)\\[\\]\\\\\\=\\+\\<\\>" +
				"\\:\\;\\\"\\,\\*\\^\\|\\&\\'\\.\\s]");
	
	private String pathName;

	/**
	 * Builds a new Galaxy folder path with the given name.
	 * @param pathName  The name of the folder path.
	 */
	public GalaxyFolderPath(String pathName) {
		checkNotNull(pathName, "pathName is null");
		checkArgument(pathName.length() >= 2, "pathName is less than 2 characters");
		checkArgument(!invalidPathName.matcher(pathName).find(), "pathName=" + pathName + " is invalid");
		
		this.pathName = pathName;
	}

	/**
	 * Gets the name of this GalaxyFolderPath
	 * @return  The name of this folder path.
	 */
	public String getName() {
		return pathName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return pathName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(pathName);
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
		GalaxyFolderPath other = (GalaxyFolderPath) obj;
		
		return Objects.equals(this.pathName, other.pathName);
	}
}
