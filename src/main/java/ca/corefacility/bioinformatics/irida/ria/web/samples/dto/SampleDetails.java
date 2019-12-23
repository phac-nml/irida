package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
	private Sample sample;
	private Set<MetadataEntry> metadata;
	private boolean modifiable;

	public SampleDetails(Sample sample, boolean modifiable) {
		this.sample = sample;
		this.metadata = sample.getMetadataEntries();
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
