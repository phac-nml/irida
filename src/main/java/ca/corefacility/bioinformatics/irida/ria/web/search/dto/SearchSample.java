package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import java.util.List;

public class SearchSample extends SearchItem {
    final String organism;
    final List<SearchProject> projects;

    public SearchSample(Sample sample, List<SearchProject> projects) {
        super(sample.getId(), sample.getSampleName(), sample.getCreatedDate(), sample.getModifiedDate());
        this.organism = sample.getOrganism();
        this.projects = projects;
    }

    public String getOrganism() {
        return organism;
    }

    public List<SearchProject> getProjects() {
        return projects;
    }
}
