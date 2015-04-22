package ca.corefacility.bioinformatics.irida.service.util.impl;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;

/**
 * Sequence file utilities class using BioJava for calculation
 * 
 *
 */
@Component
public class BioJavaSequenceFileUtilitiesImpl implements SequenceFileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileUtilities.class);

	public BioJavaSequenceFileUtilitiesImpl() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countSequenceFileLengthInBases(Path file) {
		Long totalLength = 0L;

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
