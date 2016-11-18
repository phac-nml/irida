package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;

@Entity
public class FileProcessorErrorQCEntry extends QCEntry {

	public FileProcessorErrorQCEntry() {
		super();
	}

	public FileProcessorErrorQCEntry(Sample sample) {
		super(sample);
	}

	@Override
	public String getIcon() {
		return "fa fa-cogs";
	}

}
