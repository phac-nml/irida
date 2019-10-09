package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * {@link SequenceFilePairConcatenator} for {@link SingleEndSequenceFile}s
 */
public class SingleEndSequenceFileConcatenator extends SequencingObjectConcatenator<SingleEndSequenceFile> {

	public SingleEndSequenceFileConcatenator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleEndSequenceFile concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException {
		Path tempFile;

		String extension = getFileExtension(toConcatenate);

		// create the filename with extension
		filename = filename + "." + extension;
		try {
			// create a temp directory and temp file
			Path tempDirectory = Files.createTempDirectory(null);

			tempFile = tempDirectory.resolve(filename);

			tempFile = Files.createFile(tempFile);

		} catch (IOException e) {
			throw new ConcatenateException("Could not create temporary files", e);
		}

		// for each file concatenate the file
		for (SequencingObject f : toConcatenate) {
			SingleEndSequenceFile single = (SingleEndSequenceFile) f;

			SequenceFile forwardSequenceFile = single.getSequenceFile();

			appendToFile(tempFile, forwardSequenceFile);
		}

		// create the new sequencefile and object
		SequenceFile forward = new SequenceFile(tempFile);

		SingleEndSequenceFile seqObject = new SingleEndSequenceFile(forward);

		return seqObject;
	}

}
