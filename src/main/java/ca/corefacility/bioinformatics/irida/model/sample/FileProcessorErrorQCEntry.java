package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;

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

	public FileProcessorErrorQCEntry(Sample sample) {
		super(sample);
	}

	@Override
	public QCEntryType getType() {
		return QCEntryType.PROCESSING;
	}

}
