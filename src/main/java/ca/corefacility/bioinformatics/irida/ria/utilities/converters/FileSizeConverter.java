package ca.corefacility.bioinformatics.irida.ria.utilities.converters;

import org.springframework.core.convert.converter.Converter;

/**
 * Converts a files size (originally in bytes) to Kilobytes
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class FileSizeConverter implements Converter<Long, String> {

	public static final int BYTES_PER_KB = 1024;

	/**
	 * Converts a file length property (bytes) to kilobytes.
	 *
	 * @param size Length property for a file
	 * @return String formatted size of file in kilobytes.
	 */
	@Override public String convert(Long size) {
		return String.format("%.2f KB", (float) (size / BYTES_PER_KB));
	}
}
