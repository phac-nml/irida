package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class UIPipelineDetailsResponse extends AjaxResponse {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
