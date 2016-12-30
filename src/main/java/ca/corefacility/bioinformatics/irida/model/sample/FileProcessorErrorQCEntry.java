package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;

/**
 * {@link QCEntry} for a failed {@link FileProcessor}
 * 
 * @see FileProcessor
 */
@Entity
public class FileProcessorErrorQCEntry extends QCEntry {

	public FileProcessorErrorQCEntry() {
		super();
	}
	
	public FileProcessorErrorQCEntry(SequencingObject sequencingObject) {
		super(sequencingObject, false);
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.PROCESSING;
	}

}
