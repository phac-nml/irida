package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

public class SingleEndSequenceFileConcatenator extends SequencingObjectConcatenator<SingleEndSequenceFile> {

	@Override
	public SingleEndSequenceFile concatenateFiles(Set<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException {
		Path tempFile;
		
		filename = filename + ".fastq";
		try {
			Path tempDirectory = Files.createTempDirectory(null);

			tempFile = tempDirectory.resolve(filename);

			tempFile = Files.createFile(tempFile);

		} catch (IOException e) {
			throw new ConcatenateException("Could not create temporary files", e);
		}

		for (SequencingObject f : toConcatenate) {
			SingleEndSequenceFile single = (SingleEndSequenceFile) f;

			SequenceFile forwardSequenceFile = single.getSequenceFile();

			appendToFile(tempFile, forwardSequenceFile);
		}

		SequenceFile forward = new SequenceFile(tempFile);

		SingleEndSequenceFile seqObject = new SingleEndSequenceFile(forward);

		return seqObject;
	}

}
