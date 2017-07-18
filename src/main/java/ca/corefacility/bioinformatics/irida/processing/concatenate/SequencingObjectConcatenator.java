package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public abstract class SequencingObjectConcatenator<Type extends SequencingObject> {
	public abstract Type concatenateFiles(Set<? extends SequencingObject> toConcatenate) throws ConcatenateException;

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
