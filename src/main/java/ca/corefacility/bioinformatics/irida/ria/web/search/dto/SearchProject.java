package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public class SearchProject extends SearchItem {
    final String organism;
    final Long samples;

    public SearchProject(Project project, Long samples) {
        super(project.getId(), project.getName(), project.getCreatedDate(), project.getModifiedDate());
        this.organism = project.getOrganism();
        this.samples = samples;
    }

    public String getOrganism() {
        return organism;
    }

    public Long getSamples() {
        return samples;
    }
}
