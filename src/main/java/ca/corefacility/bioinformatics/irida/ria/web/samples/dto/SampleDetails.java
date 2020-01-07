package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import java.util.Set;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
	private Sample sample;
	private Set<MetadataEntry> metadata;
	private boolean modifiable;

	public SampleDetails(Sample sample, boolean modifiable, Set<MetadataEntry> metadata) {
		this.sample = sample;
		this.metadata = metadata;
		this.modifiable = modifiable;
	}

	public Sample getSample() {
		return sample;
	}

	public Set<MetadataEntry> getMetadata() {
		return metadata;
	}

	public boolean isModifiable() {
		return modifiable;
	}
}
