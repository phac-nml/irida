package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
    private Sample sample;
    private boolean modifiable;
    private final Long projectId; // If set, means sample is in the cart

    public SampleDetails(Sample sample, boolean modifiable, Long cartProjectId) {
        this.sample = sample;
        this.modifiable = modifiable;
        this.projectId = cartProjectId;
    }

    public Sample getSample() {
        return sample;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public Long getProjectId() {
        return projectId;
    }

}
