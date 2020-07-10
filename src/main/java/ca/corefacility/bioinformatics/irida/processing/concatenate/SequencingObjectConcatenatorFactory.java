package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SequenceFilePairConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SingleEndSequenceFileConcatenator;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Factory class for returning an instance of
 * {@link SequencingObjectConcatenator} for a given filetype
 */
public class SequencingObjectConcatenatorFactory {

	/**
	 * Get a {@link SequencingObjectConcatenator} for the given class type
	 *
	 * @param type the class to get a concatenator for
	 * @param <T>  The type this concatenator should act on
	 * @param iridaFileStorageUtility The file storage component
	 * @return the new {@link SequencingObjectConcatenator}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SequencingObject> SequencingObjectConcatenator<T> getConcatenator(Class<T> type, IridaFileStorageUtility iridaFileStorageUtility) {

		// return the concatenator for the class
		if (type.equals(SingleEndSequenceFile.class)) {
			return (SequencingObjectConcatenator<T>) new SingleEndSequenceFileConcatenator(iridaFileStorageUtility);
		} else if (type.equals(SequenceFilePair.class)) {
			return (SequencingObjectConcatenator<T>) new SequenceFilePairConcatenator(iridaFileStorageUtility);
		} else {
			throw new IllegalArgumentException("No concatenator exists for type " + type);
		}
	}

	/**
	 * Get a {@link SequencingObjectConcatenator} for a given collection of
	 * {@link SequencingObject}s
	 *
	 * @param objects the {@link SequencingObject}s to get the concatenator for
	 * @param iridaFileStorageUtility The file storage component
	 * @return the new {@link SequencingObjectConcatenator}
	 */
	public static SequencingObjectConcatenator<? extends SequencingObject> getConcatenator(
			Collection<? extends SequencingObject> objects, IridaFileStorageUtility iridaFileStorageUtility) {

		// get all the classes for the objects
		Set<?> types = objects.stream().map(Object::getClass).collect(Collectors.toSet());

		// if there's more than 1 class throw an exception
		if (types.size() > 1) {
			throw new IllegalArgumentException("Cannont concatenate different filetypes");
		}

		// otherwise get the concatenator
		@SuppressWarnings("unchecked")
		Class<? extends SequencingObject> type = (Class<? extends SequencingObject>) types.iterator().next();

		return getConcatenator(type, iridaFileStorageUtility);
	}
}
