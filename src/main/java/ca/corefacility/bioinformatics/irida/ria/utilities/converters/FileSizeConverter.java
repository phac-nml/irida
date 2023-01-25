package ca.corefacility.bioinformatics.irida.ria.utilities.converters;

import org.springframework.core.convert.converter.Converter;

/**
 * Converts a files size (originally in bytes) to Kilobytes
 */
public class FileSizeConverter implements Converter<Long, String> {

	public static final int BYTES_PER_KB = 1024;
	public static final int BYTES_PER_MB = BYTES_PER_KB * BYTES_PER_KB;
	public static final int BYTES_PER_GB = BYTES_PER_MB * BYTES_PER_KB;

	/**
	 * Converts a file length property (bytes) to kilobytes.
	 *
	 * @param size Length property for a file
	 * @return String formatted size of file in kilobytes.
	 */
	@Override
	public String convert(Long size) {
		if (size > BYTES_PER_GB) {
			return String.format("%.2f GB", ((float) size / BYTES_PER_GB));
		} else if (size > BYTES_PER_MB) {
			return String.format("%d MB", (int) Math.ceil(size / BYTES_PER_MB));
		}
		return String.format("%d KB", (int) Math.ceil(size / BYTES_PER_KB));
	}
}
