package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FileProcessorErrorQCEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@ManyToOne
	public Sample sample;

	public FileProcessorErrorQCEntry(Sample sample) {
		this.sample = sample;
	}

	public Long getId() {
		return id;
	}
}
