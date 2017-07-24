package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

public class SequenceFilePairConcatenator extends SequencingObjectConcatenator<SequenceFilePair> {

	public SequenceFilePairConcatenator(){
	}
	
	@Override
	public SequenceFilePair concatenateFiles(Set<? extends SequencingObject> toConcatenate)
			throws ConcatenateException {
		SequenceFilePair firstFile = (SequenceFilePair) toConcatenate.iterator().next();
		SequenceFile originalForward = firstFile.getForwardSequenceFile();
		SequenceFile originalReverse = firstFile.getReverseSequenceFile();

		String forwardName = originalForward.getFileName();
		String reverseName = originalReverse.getFileName();

		Path forwardFile;
		Path reverseFile;
		try {
			Path tempDirectory = Files.createTempDirectory(null);

			forwardFile = tempDirectory.resolve(forwardName);
			reverseFile = tempDirectory.resolve(reverseName);

			forwardFile = Files.createFile(forwardFile);
			reverseFile = Files.createFile(reverseFile);

		} catch (IOException e) {
			throw new ConcatenateException("Could not create temporary files", e);
		}

		for (SequencingObject f : toConcatenate) {
			SequenceFilePair pair = (SequenceFilePair) f;

			SequenceFile forwardSequenceFile = pair.getForwardSequenceFile();
			SequenceFile reverseSequenceFile = pair.getReverseSequenceFile();

			appendToFile(forwardFile, forwardSequenceFile);
			appendToFile(reverseFile, reverseSequenceFile);
		}

		SequenceFile forward = new SequenceFile(forwardFile);
		SequenceFile reverse = new SequenceFile(reverseFile);

		SequenceFilePair sequenceFilePair = new SequenceFilePair(forward, reverse);

		return sequenceFilePair;
	}
}
