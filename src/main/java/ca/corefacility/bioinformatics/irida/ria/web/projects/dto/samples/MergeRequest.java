package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.List;

public class MergeRequest {
    private List<Long> ids;
    private String name;

    public List<Long> getIds() {
        return ids;
    }

    public String getName() {
        return name;
    }
}
