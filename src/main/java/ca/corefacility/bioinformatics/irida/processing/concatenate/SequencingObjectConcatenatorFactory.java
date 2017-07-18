package ca.corefacility.bioinformatics.irida.processing.concatenate;

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
}
