package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * {@link SequencingObjectConcatenator} for {@link SequenceFilePair}s
 */
public class SequenceFilePairConcatenator extends SequencingObjectConcatenator<SequenceFilePair> {
	
	public SequenceFilePairConcatenator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFilePair concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException {

		String extension = getFileExtension(toConcatenate);

		// create the filenames with F/R for the forward and reverse files
		String forwardName = filename + "_R1." + extension;
		String reverseName = filename + "_R2." + extension;

		Path forwardFile;
		Path reverseFile;
		try {
			// create a temp directory for the new files
			Path tempDirectory = Files.createTempDirectory(null);

			forwardFile = tempDirectory.resolve(forwardName);
			reverseFile = tempDirectory.resolve(reverseName);

			// create temp files
			forwardFile = Files.createFile(forwardFile);
			reverseFile = Files.createFile(reverseFile);

		} catch (IOException e) {
			throw new ConcatenateException("Could not create temporary files", e);
		}

		// for each file concatenate the forward and reverse files
		for (SequencingObject f : toConcatenate) {
			SequenceFilePair pair = (SequenceFilePair) f;

			SequenceFile forwardSequenceFile = pair.getForwardSequenceFile();
			SequenceFile reverseSequenceFile = pair.getReverseSequenceFile();

			appendToFile(forwardFile, forwardSequenceFile);
			appendToFile(reverseFile, reverseSequenceFile);
		}

		// create new SequenceFiles
		SequenceFile forward = new SequenceFile(forwardFile);
		SequenceFile reverse = new SequenceFile(reverseFile);

		// create the new pair
		SequenceFilePair sequenceFilePair = new SequenceFilePair(forward, reverse);

		return sequenceFilePair;
	}
}
