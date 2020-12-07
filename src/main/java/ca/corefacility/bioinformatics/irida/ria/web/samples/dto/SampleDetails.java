package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
	private Sample sample;
	private Map<MetadataTemplateField, MetadataEntry> metadata;
	private boolean modifiable;
	private Long projectId; // If set, means sample is in the cart

	public SampleDetails(Sample sample, boolean modifiable, Long cartProjectId) {
		this.sample = sample;
		this.metadata = sample.getMetadata();
		this.modifiable = modifiable;
		this.projectId = cartProjectId;
	}

	public Sample getSample() {
		return sample;
	}

	public Map<MetadataTemplateField, MetadataEntry> getMetadata() {
		return metadata;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public Long getProjectId() {
		return projectId;
	}
}
