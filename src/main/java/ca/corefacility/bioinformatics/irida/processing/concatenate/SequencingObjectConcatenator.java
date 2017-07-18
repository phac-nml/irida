package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public interface SequencingObjectConcatenator<Type extends SequencingObject> {
	public Type concatenateFiles(Set<? extends SequencingObject> toConcatenate) throws ConcatenateException;
}
