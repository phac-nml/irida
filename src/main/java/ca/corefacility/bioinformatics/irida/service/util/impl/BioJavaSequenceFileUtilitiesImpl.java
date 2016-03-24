package ca.corefacility.bioinformatics.irida.service.util.impl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava3.core.exceptions.CompoundNotFoundError;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
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
	public Long countSequenceFileLengthInBases(final Path file) throws UnsupportedReferenceFileContentError {
		Long totalLength = 0L;

		logger.trace("Calculating length for file: " + file);
		try (final InputStream stream = Files.newInputStream(file)){
			final LinkedHashMap<String, DNASequence> readFastaDNASequence = FastaReaderHelper.readFastaDNASequence(stream);
			for (Entry<String, DNASequence> entry : readFastaDNASequence.entrySet()) {
				logger.trace("Calculating for sequence " + entry.getValue().getAccession());
				int length = entry.getValue().getLength();
				totalLength += length;
			}
		} catch (final CompoundNotFoundError e) {
			logger.error("Cannot handle non-DNA files, or files with ambiguous bases.", e);
			throw new UnsupportedReferenceFileContentError("Cannot handle reference files with non-DNA or ambiguous characters.", e);
		} catch (Throwable e) {
			logger.error("Cannot calculate reference file length " + file);
			throw new IllegalArgumentException("Cannot parse reference file " + file, e);
		}
		return totalLength;
	}
}
