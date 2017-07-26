package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SequenceFilePairConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SingleEndSequenceFileConcatenator;

public class SequencingObjectConcatenatorFactory {

	@SuppressWarnings("unchecked")
	public static <T extends SequencingObject> SequencingObjectConcatenator<T> getConcatenator(Class<T> type) {

		if (type.equals(SingleEndSequenceFile.class)) {
			return (SequencingObjectConcatenator<T>) new SingleEndSequenceFileConcatenator();
		} else if (type.equals(SequenceFilePair.class)) {
			return (SequencingObjectConcatenator<T>) new SequenceFilePairConcatenator();
		} else {
			throw new IllegalArgumentException("No concatenator exists for type " + type);
		}
	}

	public static SequencingObjectConcatenator<? extends SequencingObject> getConcatenator(
			Collection<? extends SequencingObject> objects) {
		Set<?> types = objects.stream().map(Object::getClass).collect(Collectors.toSet());
		if (types.size() > 1) {
			throw new IllegalArgumentException("Cannont concatenate different filetypes");
		}

		@SuppressWarnings("unchecked")
		Class<? extends SequencingObject> type = (Class<? extends SequencingObject>) types.iterator().next();

		return getConcatenator(type);
	}
}
