package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.List;

/**
 * DTO to handle merging 2 samples
 */
public class MergeRequest {

    // Sample to use as the root, maintains metadata and other details.
    private Long primary;

    // List two samples ids to merge
    private List<Long> ids;

    // Optional: New name to rename the merged sample to
    private String newName;

    public String getNewName() {
        return newName;
    }

    public Long getPrimary() {
        return primary;
    }

    public List<Long> getIds() {
        return ids;
    }
}
