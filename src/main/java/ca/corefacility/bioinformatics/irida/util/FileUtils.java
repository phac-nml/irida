package ca.corefacility.bioinformatics.irida.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;


/**
 * A class containing a number of utilities for dealing with files.
 */
public class FileUtils {

	/**
	 * Determines if a file is compressed. Adapted from stackoverflow answer:
	 * 
	 * @see <a href="http://stackoverflow.com/questions/4818468/how-to-check-if-inputstream-is-gzipped#answer-8620778">stackoverflow</a>
	 * 
	 * @param file A file to test.
	 * 
	 * @return true if the file is gzipped, false otherwise.
	 * 
	 * @throws java.io.IOException if the file array couldn't be read
	 */
	public static boolean isGzipped(Path file) throws IOException {
		try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ)) {
			byte[] bytes = new byte[2];
			is.read(bytes);
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
					&& (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}

	/**
	 * From (http://stackoverflow.com/questions/3758606/how-to-convert-byte-size- into-human-readable-format-in-java)
	 *
	 * @param bytes The {@link Long} size of the file in bytes.
	 * @param si    {@link Boolean} true to use si units
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
