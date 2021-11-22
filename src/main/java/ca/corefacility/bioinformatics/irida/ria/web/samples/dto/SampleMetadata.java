package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to return {@link Sample} metadata back to the user interface.
 */
public class SampleMetadata {
    private Map<MetadataTemplateField, MetadataEntry> metadata;

    public SampleMetadata(Set<MetadataEntry> metadata) {
        this.metadata = getMapForEntries(metadata);
    }

    public Map<MetadataTemplateField, MetadataEntry> getMetadata() {
        return metadata;
    }

    /**
     * Transform the input Set of {@link MetadataEntry}  into a Map of {@link MetadataTemplateField} to {@link MetadataEntry}
     *
     * @param metadataEntries the Set of entries
     * @return the built map
     */
    private Map<MetadataTemplateField, MetadataEntry> getMapForEntries(Set<MetadataEntry> metadataEntries) {
        Map<MetadataTemplateField, MetadataEntry> metadata = metadataEntries.stream().collect(Collectors.toMap(MetadataEntry::getField, e -> e));

        return metadata;
    }
}
