package ca.corefacility.bioinformatics.irida.model.irida;

import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Describes fields that must be made available for a Sequence File in IRIDA
 * 
 *
 */
public interface IridaSequenceFile {

	/**
	 * Get the location on the file system where the sequence file is stored
	 * 
	 * @return the physical location of the file.
	 */
	public Path getFile();
	
	/**
	 * Get the size of the file.
	 *
	 * @return The String representation of the file size
	 */
	@JsonIgnore
	public String getFileSize();
	
	/**
	 * From
	 * (http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-
	 * into-human-readable-format-in-java)
	 *
	 * @param bytes
	 *            The {@link Long} size of the file in bytes.
	 * @param si
	 *            {@link Boolean} true to use si units
	 *
	 * @return A human readable {@link String} representation of the file size.
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
