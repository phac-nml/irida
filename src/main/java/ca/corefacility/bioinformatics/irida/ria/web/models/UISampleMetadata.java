package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import com.google.common.collect.ImmutableMap;

public class UISampleMetadata {
	private Long id;
	private String label;
	private List<Map<String, String>> metadata;

	public UISampleMetadata(Sample sample) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.metadata = getMetadataForSample(sample);
	}

	private List<Map<String, String>> getMetadataForSample(Sample sample) {
		List<Map<String, String>> entries = new ArrayList<>();
		Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
		for (MetadataTemplateField field : sampleMetadata.keySet()) {
			MetadataEntry entry = sampleMetadata.getOrDefault(field, new MetadataEntry());
			entries.add(ImmutableMap.of(field.getLabel(), entry.getValue()));
		}
		return entries;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<Map<String, String>> getMetadata() {
		return metadata;
	}
}
