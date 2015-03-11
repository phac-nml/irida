package ca.corefacility.bioinformatics.irida.service.util;

import java.nio.file.Path;

/**
 * Utility class for executing common functions on sequence files
 * 
 *
 */
public interface SequenceFileUtilities {

	/**
	 * Calculate the length of a sequence file in bases
	 * 
	 * @param file
	 *            The reference file object to calculate
	 * @return The number of bases in the file
	 */
	public Long countSequenceFileLengthInBases(Path file);
}
