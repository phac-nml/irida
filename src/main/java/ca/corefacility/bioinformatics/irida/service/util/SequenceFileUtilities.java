package ca.corefacility.bioinformatics.irida.service.util;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;

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
	 * @throws UnsupportedReferenceFileContentError
	 *             When the file content is *not* DNA (ambiguous IUPAC bases,
	 *             protein, etc.)
	 */
	public Long countSequenceFileLengthInBases(Path file) throws UnsupportedReferenceFileContentError;
}
