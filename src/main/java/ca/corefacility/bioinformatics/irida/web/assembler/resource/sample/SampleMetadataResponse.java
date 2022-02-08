package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response class for grouping {@link MetadataEntry}s for response in the REST API
 */
public class SampleMetadataResponse extends IridaRepresentationModel {
	Map<MetadataTemplateField, MetadataEntry> metadata;

	public SampleMetadataResponse(Set<MetadataEntry> metadataEntrySet) {
		metadata = new HashMap<>();
		for (MetadataEntry entry : metadataEntrySet) {
			metadata.put(entry.getField(), entry);
		}
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