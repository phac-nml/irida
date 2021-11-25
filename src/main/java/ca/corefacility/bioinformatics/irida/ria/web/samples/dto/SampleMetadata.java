package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to return {@link Sample} metadata back to the user interface.
 */
public class SampleMetadata {
	private List<SampleMetadataFieldEntry> metadata;

	public SampleMetadata(Set<MetadataEntry> metadata) {
		this.metadata = getMapForEntries(metadata);
	}

	public List<SampleMetadataFieldEntry> getMetadata() {
		return metadata;
	}

	/**
	 * Transform the input Set of {@link MetadataEntry}  into a {@link SampleMetadata}
	 *
	 * @param metadataEntries the Set of entries
	 * @return the {@link SampleMetadata} object
	 */
	private List<SampleMetadataFieldEntry> getMapForEntries(Set<MetadataEntry> metadataEntries) {
		List<SampleMetadataFieldEntry> metadata = metadataEntries.stream()
				.map(s -> new SampleMetadataFieldEntry(s.getField()
						.getId(), s.getField()
						.getLabel(), s.getValue(), s.getId()))
				.collect(Collectors.toList());

		return metadata;
	}
}
