package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

public class SequenceFilePairConcatenator implements SequencingObjectConcatenator<SequenceFilePair> {

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

	private void appendToFile(Path target, SequenceFile file) throws ConcatenateException {

		try (FileWriter fw = new FileWriter(target.toFile(), true);
				BufferedWriter writer = new BufferedWriter(fw);
				FileReader reader = new FileReader(file.getFile().toFile())) {

			IOUtils.copy(reader, writer);

		} catch (IOException e) {
			throw new ConcatenateException("Could not open target file for writing", e);
		}

	}

}
