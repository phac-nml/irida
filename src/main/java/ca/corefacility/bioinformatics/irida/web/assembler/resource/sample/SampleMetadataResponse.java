package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response class so we can add links to sample metadata
 */
public class SampleMetadataResponse extends IridaResourceSupport {
	private String sampleName;
	private Map<MetadataTemplateField, MetadataEntry> metadata;

	@Deprecated
	public SampleMetadataResponse(Map<MetadataTemplateField, MetadataEntry> metadata) {
		this.metadata = metadata;
	}

	public SampleMetadataResponse(Sample sample, Set<MetadataEntry> metadataEntrySet) {
		this.sampleName = sample.getSampleName();
		metadata = new HashMap<>();
		for (MetadataEntry entry : metadataEntrySet) {
			metadata.put(entry.getField(), entry);
		}
	}

	@JsonProperty
	public String getSampleName() {
		return sampleName;
	}

	@JsonProperty
	public Map<MetadataTemplateField, MetadataEntry> getMetadata() {
		return metadata;
	}

	@JsonProperty
	public void setMetadata(Map<MetadataTemplateField, MetadataEntry> metadata) {
		this.metadata = metadata;
	}
}