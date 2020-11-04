package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 * {@link SequenceFilePairConcatenator} for {@link SingleEndSequenceFile}s
 */
public class SingleEndSequenceFileConcatenator extends SequencingObjectConcatenator<SingleEndSequenceFile> {

	private IridaFileStorageUtility iridaFileStorageUtility;

	public SingleEndSequenceFileConcatenator(IridaFileStorageUtility iridaFileStorageUtility) {
		this.iridaFileStorageUtility = iridaFileStorageUtility;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleEndSequenceFile concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException {
		Path tempFile;
		String extension = null;

		try {
			extension = IridaFiles.getFileExtension(toConcatenate);
		} catch (IOException e) {
			throw new ConcatenateException("Could not get file extension", e);
		}
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

			try {
				iridaFileStorageUtility.appendToFile(tempFile, forwardSequenceFile);
			} catch (IOException e) {
				throw new ConcatenateException("Could not append file", e);
			}
		}

		// create the new sequencefile and object
		SequenceFile forward = new SequenceFile(tempFile);

		SingleEndSequenceFile seqObject = new SingleEndSequenceFile(forward);

		return seqObject;
	}

}
