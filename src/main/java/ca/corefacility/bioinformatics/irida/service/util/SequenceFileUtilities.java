package ca.corefacility.bioinformatics.irida.service.util;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for executing common functions on sequence files
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Component
public class SequenceFileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileUtilities.class);

	public SequenceFileUtilities() {

	}

	/**
	 * Calculate the length of a reference file in bases
	 * 
	 * @param entity
	 *            The reference file object to calculate
	 * @return The number of bases in the file
	 */
	public Long getSequenceFileLength(Path file) {
		Long totalLength = 0l;

		logger.trace("Calculating length for file: " + file);
		try {
			LinkedHashMap<String, DNASequence> readFastaDNASequence = FastaReaderHelper.readFastaDNASequence(file
					.toFile());
			for (Entry<String, DNASequence> entry : readFastaDNASequence.entrySet()) {
				logger.trace("Calculating for sequence " + entry.getValue().getAccession());
				int length = entry.getValue().getLength();
				totalLength += length;
			}
		} catch (Throwable e) {
			logger.error("Cannot calculate reference file length " + file);
			throw new IllegalArgumentException("Cannot parse reference file " + file, e);
		}
		return totalLength;
	}
}
