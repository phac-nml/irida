package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Class to concatenate multiple {@link SequencingObject}s and return a single
 * new {@link SequencingObject}. This class should be extended by
 * implementations for specific {@link SequencingObject}s
 * 
 * @param <Type>
 *            the {@link SequencingObject} class to concatenate
 */
public abstract class SequencingObjectConcatenator<Type extends SequencingObject> {

	/**
	 * Concatenate a set of {@link SequencingObject}s of a given type
	 * 
	 * @param toConcatenate
	 *            the set of {@link SequencingObject}s to concatenate
	 * @param filename
	 *            base name of the new file to create
	 * @return the newly created {@link SequencingObject} class
	 * @throws ConcatenateException
	 *             if there is an error during concatenation
	 */
	public abstract Type concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException;

	/**
	 * Append a {@link SequenceFile} to a {@link Path} on the filesystem
	 * 
	 * @param target
	 *            the {@link Path} to append to
	 * @param file
	 *            the {@link SequenceFile} to append to the path
	 * @throws ConcatenateException
	 *             if there is an error appending the file
	 */
	protected void appendToFile(Path target, SequenceFile file) throws ConcatenateException {

		try (FileWriter fw = new FileWriter(target.toFile(), true);
				BufferedWriter writer = new BufferedWriter(fw);
				FileReader reader = new FileReader(file.getFile().toFile())) {

			IOUtils.copy(reader, writer);

		} catch (IOException e) {
			throw new ConcatenateException("Could not open target file for writing", e);
		}

	}
}
