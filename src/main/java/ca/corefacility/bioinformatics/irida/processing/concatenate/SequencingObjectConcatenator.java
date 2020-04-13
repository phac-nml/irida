package ca.corefacility.bioinformatics.irida.processing.concatenate;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

/**
 * Class to concatenate multiple {@link SequencingObject}s and return a single new {@link SequencingObject}. This class
 * should be extended by implementations for specific {@link SequencingObject}s
 *
 * @param <Type> the {@link SequencingObject} class to concatenate
 */
public abstract class SequencingObjectConcatenator<Type extends SequencingObject> {

	/**
	 * Concatenate a set of {@link SequencingObject}s of a given type
	 *
	 * @param toConcatenate the set of {@link SequencingObject}s to concatenate
	 * @param filename      base name of the new file to create
	 * @return the newly created {@link SequencingObject} class
	 * @throws ConcatenateException if there is an error during concatenation
	 */
	public abstract Type concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException;

}
