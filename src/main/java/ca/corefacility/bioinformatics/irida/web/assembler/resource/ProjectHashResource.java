package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import org.springframework.hateoas.ResourceSupport;

/**
 * Resource class for storing a full project hash for the REST API
 */
public class ProjectHashResource extends IridaResourceSupport {
    private Integer projectHash;

    public ProjectHashResource() {
    }

    public ProjectHashResource(Integer projectHash) {
        this.projectHash = projectHash;
    }

    public Integer getProjectHash() {
        return projectHash;
    }
}
