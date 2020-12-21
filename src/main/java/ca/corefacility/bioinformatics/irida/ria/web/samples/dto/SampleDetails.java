package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
    private Sample sample;
    private Map<MetadataTemplateField, MetadataEntry> metadata;
    private boolean modifiable;
    private final Long projectId; // If set, means sample is in the cart

    public SampleDetails(Sample sample, boolean modifiable, Set<MetadataEntry> metadata, Long cartProjectId) {
        this.sample = sample;
        this.modifiable = modifiable;
        this.projectId = cartProjectId;
        this.metadata = getMapForEntries(metadata);
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
